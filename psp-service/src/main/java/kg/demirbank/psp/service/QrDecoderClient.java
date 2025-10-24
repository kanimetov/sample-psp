package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.common.ELQRData;
import reactor.core.publisher.Mono;

/**
 * Service for decoding QR code URIs
 * Handles parsing of QR code data from URI format
 */
public interface QrDecoderClient {
    
    /**
     * Decode QR URI and return ELQR data
     * 
     * @param qrUri Full QR URI containing encoded QR data
     * @return Mono containing decoded ELQR data
     */
    Mono<ELQRData> decodeQrUri(String qrUri);
}
