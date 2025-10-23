package kg.demirbank.psp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.demirbank.psp.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class for JSON operations
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Deserialize JSON string to object
     * 
     * @param json JSON string
     * @param clazz Target class
     * @param <T> Type of target class
     * @return Deserialized object
     * @throws BadRequestException if deserialization fails
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new BadRequestException("Invalid JSON format");
        }
    }
    
    /**
     * Serialize object to JSON string
     * 
     * @param object Object to serialize
     * @return JSON string
     * @throws BadRequestException if serialization fails
     */
    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage());
            throw new BadRequestException("Failed to serialize object to JSON");
        }
    }
}
