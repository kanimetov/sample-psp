package kg.demirbank.psp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response DTO for PSP Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    
    /**
     * HTTP status code
     */
    private Integer code;
    
    /**
     * Error message
     */
    private String message;
    
    /**
     * Detailed error description (optional)
     */
    private String details;
    
    /**
     * Timestamp when error occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Request path where error occurred (optional)
     */
    private String path;
}

