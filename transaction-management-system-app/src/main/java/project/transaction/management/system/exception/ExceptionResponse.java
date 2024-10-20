package project.transaction.management.system.exception;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ExceptionResponse {

    private final String code;
    private final String message;
    private final String id;
}
