package com.lucasdonato.pixsecurity.customer.controller;

import com.lucasdonato.pixsecurity.customer.dto.CustomerRequest;
import com.lucasdonato.pixsecurity.customer.dto.CustomerResponse;
import com.lucasdonato.pixsecurity.customer.service.CustomerService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.create(request);

        return ResponseEntity
                .created(URI.create("/customers/" + response.id()))
                .body(response);
    }
}
