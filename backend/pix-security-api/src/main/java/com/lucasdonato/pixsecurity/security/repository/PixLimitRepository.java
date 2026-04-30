package com.lucasdonato.pixsecurity.security.repository;

import com.lucasdonato.pixsecurity.security.document.PixLimit;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PixLimitRepository extends MongoRepository<PixLimit, String> {

    Optional<PixLimit> findByCustomerIdAndActiveTrue(String customerId);
}
