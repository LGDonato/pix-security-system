package com.lucasdonato.pixsecurity.security.document;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trusted_bindings")
@Getter
@Setter
public class TrustedBinding {

    @Id
    private String id;

    private String customerId;

    private TrustedBindingType type;

    private String value;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;
}
