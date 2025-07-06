package cz.ememsoft.policymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Data Transfer Object (DTO) for user data.
 * This class is used for passing user data between services and for internal processing.
 * It acts as an intermediary between the controller layer (request/response DTOs) and
 * the persistence layer (entities).
 * <p>
 * The DTO contains all user data fields including system-managed fields that
 * are not directly modifiable by API clients (like policy associations).
 * This allows for complete processing by service layer components while maintaining
 * separation from both the persistence model and the API contract.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String name;
    private String firstName;
    private String lastName;
    private String emailAddress;
    @Builder.Default
    private List<String> organizationUnit = new ArrayList<>();
    private LocalDate birthDate;
    private LocalDate registeredOn;
    @Builder.Default
    private List<String> policy = new ArrayList<>();
}