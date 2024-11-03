package project.transaction.management.system.exception;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String ERROR_RESPONSE_WITH_ID = "Error response: \"{}\" with id {}";

    // Handle validation errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        String errors = buildErrorMessages(exception);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    // Handle illegal argument exceptions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(final IllegalArgumentException exception) {
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Invalid arguments provided";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // Handle NumberFormatException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ExceptionResponse> handleNumberFormatException(final NumberFormatException exception) {
        String errorMessage = "Invalid number format: " + exception.getMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(final Exception exception) {
        String errorMessage = "An unexpected error occurred";
        log.error("Unhandled exception: {}", exception.getMessage(), exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    // Utility method to build error response
    private ResponseEntity<ExceptionResponse> buildErrorResponse(HttpStatus status, String message) {
        String uuid = UUID.randomUUID().toString();
        log.warn(ERROR_RESPONSE_WITH_ID, message, uuid); // Log at WARN level for errors
        return new ResponseEntity<>(ExceptionResponse.builder()
                .code(ErrorCode.BAD_REQUEST.name()) // Consider customizing this for different error types
                .message(message)
                .id(uuid)
                .build(),
                status
        );
    }

    // Build error messages for validation errors
    private static String buildErrorMessages(MethodArgumentNotValidException exception) {
        Set<String> errorFields = new TreeSet<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorFields.add(String.format(error.getDefaultMessage() != null ? error.getDefaultMessage() : "", error.getField()));
        }
        for (ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            errorFields.add(error.getDefaultMessage());
        }
        return String.join(" ", errorFields);
    }
}
