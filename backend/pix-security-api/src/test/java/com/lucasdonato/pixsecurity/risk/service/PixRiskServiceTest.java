package com.lucasdonato.pixsecurity.risk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lucasdonato.pixsecurity.customer.entity.Customer;
import com.lucasdonato.pixsecurity.customer.repository.CustomerRepository;
import com.lucasdonato.pixsecurity.risk.dto.PixRiskRequest;
import com.lucasdonato.pixsecurity.risk.dto.PixRiskResponse;
import com.lucasdonato.pixsecurity.risk.model.RiskLevel;
import com.lucasdonato.pixsecurity.security.document.PixLimit;
import com.lucasdonato.pixsecurity.security.document.TrustedBindingType;
import com.lucasdonato.pixsecurity.security.repository.PixLimitRepository;
import com.lucasdonato.pixsecurity.security.repository.TrustedBindingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Habilita o Mockito no JUnit 5 sem carregar o contexto do Spring.
@ExtendWith(MockitoExtension.class)
class PixRiskServiceTest {

    private static final UUID CUSTOMER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String CUSTOMER_ID = CUSTOMER_UUID.toString();
    private static final String DEVICE_ID = "device-android-abc123";

    // Repositorios mockados para testar apenas a regra de negocio do PixRiskService.
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PixLimitRepository pixLimitRepository;

    @Mock
    private TrustedBindingRepository trustedBindingRepository;

    // Injeta os mocks acima no service, simulando a injecao de dependencias do Spring.
    @InjectMocks
    private PixRiskService pixRiskService;

    @Test
    void shouldReturnLowWhenCustomerIsActiveLimitIsOkAndDeviceIsTrusted() {
        // Cenario feliz: cliente ativo, limite configurado e dispositivo confiavel.
        PixRiskRequest request = request(50_000L, LocalDateTime.of(2026, 4, 29, 14, 0));
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Optional.of(customer(Customer.Status.ACTIVE)));
        when(pixLimitRepository.findByCustomerIdAndActiveTrue(CUSTOMER_ID)).thenReturn(Optional.of(pixLimit()));
        when(trustedBindingRepository.existsByCustomerIdAndTypeAndValueAndActiveTrue(
                CUSTOMER_ID,
                TrustedBindingType.DEVICE,
                DEVICE_ID
        )).thenReturn(true);

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isTrue();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(response.reason()).isEqualTo("Transação aprovada com baixo risco");
    }

    @Test
    void shouldReturnMediumWhenDeviceIsNotTrusted() {
        // Mesmo com valor permitido, device sem vinculo confiavel aumenta o risco para MEDIUM.
        PixRiskRequest request = request(50_000L, LocalDateTime.of(2026, 4, 29, 14, 0));
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Optional.of(customer(Customer.Status.ACTIVE)));
        when(pixLimitRepository.findByCustomerIdAndActiveTrue(CUSTOMER_ID)).thenReturn(Optional.of(pixLimit()));
        when(trustedBindingRepository.existsByCustomerIdAndTypeAndValueAndActiveTrue(
                CUSTOMER_ID,
                TrustedBindingType.DEVICE,
                DEVICE_ID
        )).thenReturn(false);

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isTrue();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(response.reason()).isEqualTo("Dispositivo não confiável, transação requer monitoramento");
    }

    @Test
    void shouldReturnHighWhenAmountIsAboveTransactionLimit() {
        // Valor acima do limite por transacao reprova antes da verificacao do device.
        PixRiskRequest request = request(150_000L, LocalDateTime.of(2026, 4, 29, 14, 0));
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Optional.of(customer(Customer.Status.ACTIVE)));
        when(pixLimitRepository.findByCustomerIdAndActiveTrue(CUSTOMER_ID)).thenReturn(Optional.of(pixLimit()));

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isFalse();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(response.reason()).isEqualTo("Valor acima do limite por transação");
        verify(trustedBindingRepository, never()).existsByCustomerIdAndTypeAndValueAndActiveTrue(
                CUSTOMER_ID,
                TrustedBindingType.DEVICE,
                DEVICE_ID
        );
    }

    @Test
    void shouldReturnBlockedWhenCustomerIdIsInvalid() {
        // UUID invalido deve bloquear sem consultar banco ou repositorios externos.
        PixRiskRequest request = new PixRiskRequest(
                "invalid-customer-id",
                50_000L,
                DEVICE_ID,
                LocalDateTime.of(2026, 4, 29, 14, 0)
        );

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isFalse();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.BLOCKED);
        assertThat(response.reason()).isEqualTo("Cliente inválido");
        verify(customerRepository, never()).findById(CUSTOMER_UUID);
    }

    @Test
    void shouldReturnBlockedWhenCustomerDoesNotExist() {
        // Cliente inexistente bloqueia a transacao antes de buscar limites PIX.
        PixRiskRequest request = request(50_000L, LocalDateTime.of(2026, 4, 29, 14, 0));
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Optional.empty());

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isFalse();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.BLOCKED);
        assertThat(response.reason()).isEqualTo("Cliente não encontrado");
        verify(pixLimitRepository, never()).findByCustomerIdAndActiveTrue(CUSTOMER_ID);
    }

    @Test
    void shouldReturnBlockedWhenCustomerIsInactive() {
        // Cliente inativo bloqueia a transacao mesmo que o request esteja valido.
        PixRiskRequest request = request(50_000L, LocalDateTime.of(2026, 4, 29, 14, 0));
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Optional.of(customer(Customer.Status.INACTIVE)));

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isFalse();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.BLOCKED);
        assertThat(response.reason()).isEqualTo("Cliente inativo");
        verify(pixLimitRepository, never()).findByCustomerIdAndActiveTrue(CUSTOMER_ID);
    }

    @Test
    void shouldReturnBlockedWhenPixLimitIsNotConfigured() {
        // Sem limite PIX ativo configurado, o motor nao permite a transacao.
        PixRiskRequest request = request(50_000L, LocalDateTime.of(2026, 4, 29, 14, 0));
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Optional.of(customer(Customer.Status.ACTIVE)));
        when(pixLimitRepository.findByCustomerIdAndActiveTrue(CUSTOMER_ID)).thenReturn(Optional.empty());

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isFalse();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.BLOCKED);
        assertThat(response.reason()).isEqualTo("Limite PIX não configurado");
        verify(trustedBindingRepository, never()).existsByCustomerIdAndTypeAndValueAndActiveTrue(
                CUSTOMER_ID,
                TrustedBindingType.DEVICE,
                DEVICE_ID
        );
    }

    @Test
    void shouldReturnBlockedWhenAmountIsAboveNighttimeLimit() {
        // Transacao as 21h entra no periodo noturno e usa o limite noturno.
        PixRiskRequest request = request(80_000L, LocalDateTime.of(2026, 4, 29, 21, 0));
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Optional.of(customer(Customer.Status.ACTIVE)));
        when(pixLimitRepository.findByCustomerIdAndActiveTrue(CUSTOMER_ID)).thenReturn(Optional.of(pixLimit()));

        PixRiskResponse response = pixRiskService.validate(request);

        assertThat(response.approved()).isFalse();
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.BLOCKED);
        assertThat(response.reason()).isEqualTo("Valor acima do limite noturno");
        verify(trustedBindingRepository, never()).existsByCustomerIdAndTypeAndValueAndActiveTrue(
                CUSTOMER_ID,
                TrustedBindingType.DEVICE,
                DEVICE_ID
        );
    }

    private PixRiskRequest request(Long amountCents, LocalDateTime transactionDateTime) {
        // Factory para manter os testes focados no valor e horario de cada cenario.
        return new PixRiskRequest(CUSTOMER_ID, amountCents, DEVICE_ID, transactionDateTime);
    }

    private Customer customer(Customer.Status status) {
        // Cliente fake suficiente para exercitar as regras de status.
        Customer customer = new Customer("12345678901", "Lucas Donato", "lucas.donato@example.com", "11987654321");
        customer.setId(CUSTOMER_UUID);
        customer.setStatus(status);

        return customer;
    }

    private PixLimit pixLimit() {
        // Limites padrao usados pelos cenarios de risco.
        PixLimit pixLimit = new PixLimit();
        pixLimit.setCustomerId(CUSTOMER_ID);
        pixLimit.setTransactionLimitCents(100_000L);
        pixLimit.setDailyLimitCents(500_000L);
        pixLimit.setNighttimeLimitCents(60_000L);
        pixLimit.setActive(true);

        return pixLimit;
    }
}
