package com.lucasdonato.pixsecurity.security.service;

import com.lucasdonato.pixsecurity.security.document.TrustedBinding;
import com.lucasdonato.pixsecurity.security.dto.TrustedBindingRequest;
import com.lucasdonato.pixsecurity.security.dto.TrustedBindingResponse;
import com.lucasdonato.pixsecurity.security.repository.TrustedBindingRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class TrustedBindingService {

    private final TrustedBindingRepository trustedBindingRepository;

    public TrustedBindingService(TrustedBindingRepository trustedBindingRepository) {
        this.trustedBindingRepository = trustedBindingRepository;
    }

    public TrustedBindingResponse create(TrustedBindingRequest request) {
        TrustedBinding trustedBinding = new TrustedBinding();
        trustedBinding.setCustomerId(request.customerId());
        trustedBinding.setType(request.type());
        trustedBinding.setValue(request.value());
        trustedBinding.setExpiresAt(request.expiresAt());
        trustedBinding.setActive(true);
        trustedBinding.setCreatedAt(LocalDateTime.now());

        return TrustedBindingResponse.from(trustedBindingRepository.save(trustedBinding));
    }
}
