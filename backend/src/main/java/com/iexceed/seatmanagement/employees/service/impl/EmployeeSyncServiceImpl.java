package com.iexceed.seatmanagement.employees.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.seatmanagement.employees.dto.zoho.ZohoEmployeeRecordDTO;
import com.iexceed.seatmanagement.employees.entity.Employee;
import com.iexceed.seatmanagement.employees.mapper.EmployeeMapper;
import com.iexceed.seatmanagement.employees.repository.EmployeeRepository;
import com.iexceed.seatmanagement.employees.service.EmployeeSyncService;
import com.iexceed.seatmanagement.integrations.zoho.client.ZohoPeopleClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class EmployeeSyncServiceImpl implements EmployeeSyncService {

    private static final int PAGE_SIZE = 100;

    private final ZohoPeopleClient zohoPeopleClient;
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;
    private final Lock syncLock = new ReentrantLock();

    @Async("AsynctaskExecutor")
    @Override
    public void syncEmployees() {
        if (!syncLock.tryLock()) {
            log.warn("Sync already in progress, skipping.");
            return;
        }

        long start = System.currentTimeMillis();

        try {
            log.info("Starting background employee sync from Zoho");
            Integer syncedEmployees = fetchAndSyncEmployees().block();
            log.info("Background sync completed. Synced {} employees in {} ms",
                    syncedEmployees, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("Error in background sync: {}", e.getMessage(), e);
        } finally {
            syncLock.unlock();
        }
    }

    private Mono<Integer> fetchAndSyncEmployees() {
        return zohoPeopleClient.getAccessToken()
                .flatMap(token -> fetchAndSyncPage(token, 1, 0));
    }

    private Mono<Integer> fetchAndSyncPage(String token, int from, int totalSaved) {
        log.info("Fetching employees from Zoho. sIndex={}, limit={}", from, PAGE_SIZE);

        return zohoPeopleClient.fetchEmployeesFromZoho(token, from, PAGE_SIZE)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(1))
                                .maxBackoff(Duration.ofSeconds(5))
                                .doBeforeRetry(retrySignal ->
                                        log.warn("Retrying Zoho employee fetch. sIndex={}, attempt={}, reason={}",
                                                from,
                                                retrySignal.totalRetries() + 1,
                                                retrySignal.failure().getMessage()
                                        )
                                )
                )
                .map(this::extractEmployeeRecords)
                .flatMap(records -> {
                    if (records.isEmpty()) {
                        log.info("No more employee records found at sIndex={}", from);
                        return Mono.just(totalSaved);
                    }

                    int savedThisPage = saveEmployees(records);
                    int newTotal = totalSaved + savedThisPage;

                    log.info("Processed page sIndex={}, fetched={}, saved={}, totalSaved={}",
                            from, records.size(), savedThisPage, newTotal);

                    if (records.size() < PAGE_SIZE) {
                        log.info("Last page reached at sIndex={}, fetched={}", from, records.size());
                        return Mono.just(newTotal);
                    }

                    return fetchAndSyncPage(token, from + PAGE_SIZE, newTotal);
                });
    }

    private List<ZohoEmployeeRecordDTO> extractEmployeeRecords(Map<String, Object> response) {
        try {
            Object responseObj = response.get("response");
            if (!(responseObj instanceof Map<?, ?> responseMapRaw)) {
                log.warn("Missing or invalid 'response' node");
                return Collections.emptyList();
            }

            Map<String, Object> responseMap = (Map<String, Object>) responseMapRaw;

            Object resultObj = responseMap.get("result");
            if (!(resultObj instanceof List<?> resultList)) {
                log.warn("Missing or invalid 'result' node");
                return Collections.emptyList();
            }

            List<ZohoEmployeeRecordDTO> records = new ArrayList<>();

            for (Object item : resultList) {
                if (!(item instanceof Map<?, ?> employeeWrapperRaw)) {
                    continue;
                }

                Map<String, Object> employeeWrapper = (Map<String, Object>) employeeWrapperRaw;

                for (Object value : employeeWrapper.values()) {
                    if (!(value instanceof List<?> employeeList)) {
                        continue;
                    }

                    for (Object employeeObj : employeeList) {
                        try {
                            ZohoEmployeeRecordDTO dto = objectMapper.convertValue(
                                    employeeObj,
                                    ZohoEmployeeRecordDTO.class
                            );
                            records.add(dto);
                        } catch (Exception ex) {
                            log.error("Failed to map employee object from Zoho payload. payload={}", employeeObj, ex);
                        }
                    }
                }
            }

            return records;
        } catch (Exception ex) {
            log.error("Failed to extract Zoho employee records", ex);
            return Collections.emptyList();
        }
    }

    private int saveEmployees(List<ZohoEmployeeRecordDTO> records) {
        int savedCount = 0;

        for (ZohoEmployeeRecordDTO dto : records) {
            try {
                if (dto.getZohoId() == null || dto.getZohoId().isBlank()) {
                    log.warn("Skipping employee due to missing zohoId. employeeCode={}", dto.getEmployeeId());
                    continue;
                }

                Employee employee = EmployeeMapper.toEmployee(dto);

                employeeRepository.findByZohoEmployeeId(dto.getZohoId())
                        .ifPresent(existing -> employee.setId(existing.getId()));

                employeeRepository.save(employee);
                savedCount++;

            } catch (Exception ex) {
                log.error("Failed to save employee. zohoId={}, employeeCode={}",
                        dto.getZohoId(), dto.getEmployeeId(), ex);
            }
        }

        return savedCount;
    }
}