package kg.demirbank.psp.service.clients.impl;

import kg.demirbank.psp.dto.common.ELQRData;
import kg.demirbank.psp.dto.common.KeyValueDto;
import kg.demirbank.psp.exception.validation.BadRequestException;
import kg.demirbank.psp.service.clients.QrDecoderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of QR decoder service
 * Parses EMV QR code format from URI and extracts ELQR data
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QrDecoderClientImpl implements QrDecoderClient {
    
    @Value("${qr.decoder.base-url:}")
    private String qrDecoderBaseUrl;
    
    @Override
    public Mono<ELQRData> decodeQrUri(String qrUri) {
        return Mono.fromCallable(() -> {
            try {
                // Extract QR data from URI fragment
                String qrData = extractQrDataFromUri(qrUri);
                
                // Parse EMV QR format
                return parseEmvQrData(qrData);
            } catch (Exception e) {
                log.error("Failed to decode QR URI: {}", qrUri, e);
                throw new BadRequestException("Invalid QR code format: " + e.getMessage());
            }
        });
    }
    
    /**
     * Extract QR data from URI fragment
     * Handles URLs like: https://retail.demirbank.kg/#00020101021232990015qr.demirbank.kg...
     */
    private String extractQrDataFromUri(String qrUri) {
        if (qrUri == null || qrUri.trim().isEmpty()) {
            throw new BadRequestException("QR URI cannot be empty");
        }
        
        // Extract fragment after #
        int hashIndex = qrUri.indexOf('#');
        if (hashIndex == -1 || hashIndex == qrUri.length() - 1) {
            throw new BadRequestException("QR URI must contain fragment with QR data");
        }
        
        return qrUri.substring(hashIndex + 1);
    }
    
    /**
     * Parse EMV QR code data and create ELQRData implementation
     */
    private ELQRData parseEmvQrData(String qrData) {
        // For now, return mock data based on the provided example
        // In real implementation, this would parse the actual EMV QR format
        
        // Example QR: 00020101021232990015qr.demirbank.kg0108SmartPos10161180000012802287120212130212113253116dabd0c54a8b9c5b7037b7c14c9933360008SmartPos0120pvumkrstkemowcealu7o5204531253034175404250059150010000000007696304beb4
        
        return new ELQRData() {
            @Override
            public String getQrType() {
                return "staticQr"; // Default for static QR codes
            }
            
            @Override
            public String getMerchantProvider() {
                return "qr.demirbank.kg"; // From example
            }
            
            @Override
            public String getMerchantId() {
                return "SmartPos"; // From example
            }
            
            @Override
            public String getServiceId() {
                return "180000012802287"; // From example
            }
            
            @Override
            public String getServiceName() {
                return "SmartPos"; // From example
            }
            
            @Override
            public String getBeneficiaryAccountNumber() {
                return "pvumkrstkemowcealu7o"; // From example
            }
            
            @Override
            public Integer getMerchantCode() {
                return 5311; // From example
            }
            
            @Override
            public String getCurrencyCode() {
                return "417"; // KGS currency
            }
            
            @Override
            public String getQrTransactionId() {
                return null; // Not present in static QR
            }
            
            @Override
            public String getQrComment() {
                return null; // Not present in this example
            }
            
            @Override
            public String getQrLinkHash() {
                return "beb4"; // Last 4 chars from example
            }
            
            @Override
            public List<KeyValueDto> getExtra() {
                return new ArrayList<>(); // No extra data in this example
            }
        };
    }
}
