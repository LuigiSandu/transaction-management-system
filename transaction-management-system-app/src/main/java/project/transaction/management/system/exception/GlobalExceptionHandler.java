package project.transaction.management.system.exception;


import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
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

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String ERROR_RESPONSE_WITH_ID = "Error response {} with id {}";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        final String errors = String.format(buildErrorMessages(exception));
        final String uuid = UUID.randomUUID().toString();
        log.info(ERROR_RESPONSE_WITH_ID, errors, uuid);
        return new ResponseEntity<>(
                ExceptionResponse.builder()
                        .code(ErrorCode.BAD_REQUEST.name())
                        .message(errors)
                        .id(uuid)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    private static String buildErrorMessages(MethodArgumentNotValidException exception) {
        Set<String> errorFields = new TreeSet<>();
        for (final FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorFields.add(String.format(error.getDefaultMessage() != null ? error.getDefaultMessage() : "", error.getField()));
        }
        for (final ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            errorFields.add(error.getDefaultMessage());
        }
        return String.join(" ", errorFields);
    }
}
