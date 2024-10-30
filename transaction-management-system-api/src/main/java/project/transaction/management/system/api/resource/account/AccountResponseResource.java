package project.transaction.management.system.api.resource.account;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseResource {

    private String accountNumber;
    private String accountType;
    private String name;
    private Long balance;
    private LocalDate createdAt;
    private LocalDate updatedAt;


}
