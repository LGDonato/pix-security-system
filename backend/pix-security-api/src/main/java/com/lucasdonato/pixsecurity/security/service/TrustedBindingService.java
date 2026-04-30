package com.lucasdonato.pixsecurity.security.service;

import com.lucasdonato.pixsecurity.security.document.TrustedBinding;
import com.lucasdonato.pixsecurity.security.dto.TrustedBindingRequest;
import com.lucasdonato.pixsecurity.security.dto.TrustedBindingResponse;
import com.lucasdonato.pixsecurity.security.repository.TrustedBindingRepository;
import com.lucasdonato.pixsecurity.shared.exception.DuplicateResourceException;
import com.lucasdonato.pixsecurity.shared.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrustedBindingService {

    private final TrustedBindingRepository trustedBindingRepository;

    public TrustedBindingService(TrustedBindingRepository trustedBindingRepository) {
        this.trustedBindingRepository = trustedBindingRepository;
    }

    public TrustedBindingResponse create(TrustedBindingRequest request) {
        // Regra de negocio: um cliente nao pode ter vinculo ativo duplicado para o mesmo tipo e valor.
        if (trustedBindingRepository.existsByCustomerIdAndTypeAndValueAndActiveTrue(
                request.customerId(),
                request.type(),
                request.value()
        )) {
            throw new DuplicateResourceException("Vínculo confiável já cadastrado para este cliente");
        }

        TrustedBinding trustedBinding = new TrustedBinding();
        trustedBinding.setCustomerId(request.customerId());
        trustedBinding.setType(request.type());
        trustedBinding.setValue(request.value());
        trustedBinding.setExpiresAt(request.expiresAt());
        trustedBinding.setActive(true);
        trustedBinding.setCreatedAt(LocalDateTime.now());

        return TrustedBindingResponse.from(trustedBindingRepository.save(trustedBinding));
    }

    public List<TrustedBindingResponse> listByCustomerId(String customerId) {
        return trustedBindingRepository.findByCustomerId(customerId)
                .stream()
                .map(TrustedBindingResponse::from)
                .toList();
    }

    public void delete(String id) {
        if (!trustedBindingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vínculo confiável não encontrado");
        }

        trustedBindingRepository.deleteById(id);
    }
}
