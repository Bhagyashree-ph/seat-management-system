package com.iexceed.seatmanagement.users.repository;

import com.iexceed.seatmanagement.users.entity.SystemUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SystemUserRepository extends MongoRepository<SystemUser,String> {

    Optional<SystemUser> findByUsername(String username);

    Optional<SystemUser> findByEmployeeId(String employeeId);

    boolean existsByUsername(String username);

}