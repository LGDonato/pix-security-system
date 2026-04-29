package com.lucasdonato.pixsecurity.security.repository;

import com.lucasdonato.pixsecurity.security.document.TrustedBinding;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrustedBindingRepository extends MongoRepository<TrustedBinding, String> {
}
