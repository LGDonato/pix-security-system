package com.lucasdonato.pixsecurity.security.service;

import com.lucasdonato.pixsecurity.security.document.PixLimit;
import com.lucasdonato.pixsecurity.security.dto.PixLimitRequest;
import com.lucasdonato.pixsecurity.security.dto.PixLimitResponse;
import com.lucasdonato.pixsecurity.security.repository.PixLimitRepository;
import com.lucasdonato.pixsecurity.shared.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class PixLimitService {

    private final PixLimitRepository pixLimitRepository;

    public PixLimitService(PixLimitRepository pixLimitRepository) {
        this.pixLimitRepository = pixLimitRepository;
    }

    public PixLimitResponse upsert(PixLimitRequest request) {
        LocalDateTime now = LocalDateTime.now();
        PixLimit pixLimit = pixLimitRepository.findByCustomerIdAndActiveTrue(request.customerId())
                .orElseGet(() -> createNewLimit(request.customerId(), now));

        pixLimit.setTransactionLimitCents(request.transactionLimitCents());
        pixLimit.setDailyLimitCents(request.dailyLimitCents());
        pixLimit.setNighttimeLimitCents(request.nighttimeLimitCents());
        pixLimit.setUpdatedAt(now);

        return PixLimitResponse.from(pixLimitRepository.save(pixLimit));
    }

    public PixLimitResponse findActiveByCustomerId(String customerId) {
        return pixLimitRepository.findByCustomerIdAndActiveTrue(customerId)
                .map(PixLimitResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Limite PIX não encontrado para este cliente"));
    }

    private PixLimit createNewLimit(String customerId, LocalDateTime now) {
        PixLimit pixLimit = new PixLimit();
        pixLimit.setCustomerId(customerId);
        pixLimit.setActive(true);
        pixLimit.setCreatedAt(now);
        pixLimit.setUpdatedAt(now);

        return pixLimit;
    }
}
