package com.iexceed.seatmanagement.roles.repository;

import com.iexceed.seatmanagement.roles.entity.Role;
import com.iexceed.seatmanagement.roles.enums.RoleCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository
        extends MongoRepository<Role, String> {

    Optional<Role> findByCode(RoleCode code);
}