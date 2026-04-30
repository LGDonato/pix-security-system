package com.lucasdonato.pixsecurity.security.repository;

import com.lucasdonato.pixsecurity.security.document.TrustedBinding;
import com.lucasdonato.pixsecurity.security.document.TrustedBindingType;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrustedBindingRepository extends MongoRepository<TrustedBinding, String> {

    boolean existsByCustomerIdAndTypeAndValueAndActiveTrue(
            String customerId,
            TrustedBindingType type,
            String value
    );

    List<TrustedBinding> findByCustomerId(String customerId);
}
