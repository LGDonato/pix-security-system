package com.lucasdonato.pixsecurity.risk.service;

import com.lucasdonato.pixsecurity.customer.entity.Customer;
import com.lucasdonato.pixsecurity.customer.repository.CustomerRepository;
import com.lucasdonato.pixsecurity.risk.dto.PixRiskRequest;
import com.lucasdonato.pixsecurity.risk.dto.PixRiskResponse;
import com.lucasdonato.pixsecurity.risk.model.RiskLevel;
import com.lucasdonato.pixsecurity.security.document.PixLimit;
import com.lucasdonato.pixsecurity.security.document.TrustedBindingType;
import com.lucasdonato.pixsecurity.security.repository.PixLimitRepository;
import com.lucasdonato.pixsecurity.security.repository.TrustedBindingRepository;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PixRiskService {

    private static final LocalTime NIGHTTIME_START = LocalTime.of(20, 0);
    private static final LocalTime NIGHTTIME_END = LocalTime.of(6, 0);

    private final CustomerRepository customerRepository;
    private final PixLimitRepository pixLimitRepository;
    private final TrustedBindingRepository trustedBindingRepository;

    public PixRiskService(
            CustomerRepository customerRepository,
            PixLimitRepository pixLimitRepository,
            TrustedBindingRepository trustedBindingRepository
    ) {
        this.customerRepository = customerRepository;
        this.pixLimitRepository = pixLimitRepository;
        this.trustedBindingRepository = trustedBindingRepository;
    }

    public PixRiskResponse validate(PixRiskRequest request) {
        UUID customerUuid = parseCustomerId(request.customerId());
        if (customerUuid == null) {
            return blocked("Cliente inválido");
        }

        Optional<Customer> optionalCustomer = customerRepository.findById(customerUuid);
        if (optionalCustomer.isEmpty()) {
            return blocked("Cliente não encontrado");
        }

        Customer customer = optionalCustomer.get();
        if (Customer.Status.INACTIVE.equals(customer.getStatus())) {
            return blocked("Cliente inativo");
        }

        Optional<PixLimit> optionalPixLimit = pixLimitRepository.findByCustomerIdAndActiveTrue(request.customerId());
        if (optionalPixLimit.isEmpty()) {
            return blocked("Limite PIX não configurado");
        }

        PixLimit pixLimit = optionalPixLimit.get();
        if (isNighttime(request) && request.amountCents() > pixLimit.getNighttimeLimitCents()) {
            return blocked("Valor acima do limite noturno");
        }

        if (request.amountCents() > pixLimit.getTransactionLimitCents()) {
            return new PixRiskResponse(false, RiskLevel.HIGH, "Valor acima do limite por transação");
        }

        boolean trustedDevice = trustedBindingRepository.existsByCustomerIdAndTypeAndValueAndActiveTrue(
                request.customerId(),
                TrustedBindingType.DEVICE,
                request.deviceId()
        );

        if (!trustedDevice) {
            return new PixRiskResponse(
                    true,
                    RiskLevel.MEDIUM,
                    "Dispositivo não confiável, transação requer monitoramento"
            );
        }

        return new PixRiskResponse(true, RiskLevel.LOW, "Transação aprovada com baixo risco");
    }

    private UUID parseCustomerId(String customerId) {
        try {
            return UUID.fromString(customerId);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean isNighttime(PixRiskRequest request) {
        LocalTime transactionTime = request.transactionDateTime().toLocalTime();

        // Periodo noturno cruza a meia-noite: das 20:00 ate antes das 06:00.
        return !transactionTime.isBefore(NIGHTTIME_START) || transactionTime.isBefore(NIGHTTIME_END);
    }

    private PixRiskResponse blocked(String reason) {
        return new PixRiskResponse(false, RiskLevel.BLOCKED, reason);
    }
}
