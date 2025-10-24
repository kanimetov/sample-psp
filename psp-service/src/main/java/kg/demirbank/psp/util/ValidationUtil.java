package kg.demirbank.psp.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import kg.demirbank.psp.exception.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Utility class for DTO validation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationUtil {

    private final Validator validator;

    /**
     * Validates a DTO object and throws ValidationException if validation fails
     * 
     * @param dto the DTO object to validate
     * @param <T> the type of DTO
     * @throws ValidationException if validation fails
     */
    public <T> void validateDto(T dto) {
        if (dto == null) {
            throw new ValidationException("DTO object cannot be null");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append(violation.getPropertyPath())
                          .append(" ")
                          .append(violation.getMessage())
                          .append("; ");
            }
            
            String error = errorMessage.toString();
            log.warn("DTO validation failed: {}", error);
            throw new ValidationException(error);
        }
        
        log.debug("DTO validation successful for: {}", dto.getClass().getSimpleName());
    }

    /**
     * Validates a DTO object and returns validation result
     * 
     * @param dto the DTO object to validate
     * @param <T> the type of DTO
     * @return true if validation passes, false otherwise
     */
    public <T> boolean isValid(T dto) {
        if (dto == null) {
            return false;
        }

        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        return violations.isEmpty();
    }

    /**
     * Gets validation error message for a DTO object
     * 
     * @param dto the DTO object to validate
     * @param <T> the type of DTO
     * @return validation error message or null if validation passes
     */
    public <T> String getValidationErrorMessage(T dto) {
        if (dto == null) {
            return "DTO object cannot be null";
        }

        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (violations.isEmpty()) {
            return null;
        }

        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (ConstraintViolation<T> violation : violations) {
            errorMessage.append(violation.getPropertyPath())
                      .append(" ")
                      .append(violation.getMessage())
                      .append("; ");
        }
        
        return errorMessage.toString();
    }
}
