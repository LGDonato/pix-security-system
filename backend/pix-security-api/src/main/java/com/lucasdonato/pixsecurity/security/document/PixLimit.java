package com.lucasdonato.pixsecurity.security.document;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pix_limits")
@Getter
@Setter
public class PixLimit {

    @Id
    private String id;

    private String customerId;

    private Long transactionLimitCents;

    private Long dailyLimitCents;

    private Long nighttimeLimitCents;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
