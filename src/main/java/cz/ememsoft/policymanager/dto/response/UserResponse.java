package cz.ememsoft.policymanager.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for user response objects.
 * This class is used exclusively in controllers to return user data to API clients.
 * It contains the complete user information including automatically applied policies.
 * <p>
 * The response DTO includes all user fields, including system-generated fields like
 * registration date and the list of policies that apply to the user.
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


public class UserResponse {

    private String name;
    private String firstName;
    private String lastName;
    private String emailAddress;
    @Builder.Default
    private List<String> organizationUnit = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registeredOn;
    @Builder.Default
    private List<String> policy = new ArrayList<>();
}