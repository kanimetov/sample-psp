package kg.demirbank.psp.api;

import jakarta.validation.Valid;
import kg.demirbank.psp.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Incoming controller handles:
 * 1. External API from clients (PSP external endpoints)
 * 2. Incoming requests from Operator (beneficiary side)
 */
@RestController
public class IncomingController {

    // ========== Incoming from Operator (Beneficiary side) ==========
    // Base path: /in/qr/{version}/tx
    
    @PostMapping("/in/qr/{version}/tx/check")
    public ResponseEntity<CheckResponseDto> inboundCheck(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @Valid @RequestBody CheckRequestDto body) {
        CheckResponseDto resp = new CheckResponseDto();
        resp.setBeneficiaryName("c***e A***o");
        resp.setTransactionType(null); // TODO: set proper value
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/in/qr/{version}/tx/create")
    public ResponseEntity<CreateResponseDto> inboundCreate(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @Valid @RequestBody CreateRequestDto body) {
        CreateResponseDto resp = new CreateResponseDto();
        resp.setTransactionId("fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7");
        resp.setStatus(null); // TODO: set proper status
        resp.setTransactionType(body.getTransactionType());
        resp.setAmount(body.getAmount());
        resp.setBeneficiaryName("Sample Beneficiary");
        resp.setCustomerType(1); // TODO: set proper value
        resp.setReceiptId(body.getReceiptId());
        resp.setCreatedDate("2022-11-01T12:00:00Z");
        resp.setExecutedDate("");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/in/qr/{version}/tx/execute/{transactionId}")
    public ResponseEntity<StatusDto> inboundExecute(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash) {
        StatusDto resp = new StatusDto();
        resp.setTransactionId(transactionId);
        resp.setStatus(null); // TODO: set proper Status enum value
        resp.setTransactionType(null); // TODO: set proper CustomerType enum value
        resp.setAmount(40000L);
        resp.setBeneficiaryName("c***e A***o");
        resp.setCustomerType("1");
        resp.setReceiptId("7218199");
        resp.setCreatedDate("2022-11-01T12:00:00Z");
        resp.setExecutedDate("2022-11-01T12:02:00Z");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/in/qr/{version}/tx/update/{transactionId}")
    public ResponseEntity<?> inboundUpdate(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @Valid @RequestBody UpdateDto body) {
        if (version == null || version.isBlank()) {
            return ResponseEntity.badRequest().body("QR version not specified");
        }
        if (transactionId == null || transactionId.isBlank()) {
            return ResponseEntity.badRequest().body("Transaction ID not specified");
        }
        // ACK response (200 OK empty body)
        return ResponseEntity.ok().build();
    }
}

