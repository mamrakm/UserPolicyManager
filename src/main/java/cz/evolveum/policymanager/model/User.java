package cz.evolveum.policymanager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record User(
        @NotBlank(message = "Username is required")
        String name,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @Email(message = "Valid email address is required")
        String emailAddress,

        List<String> organizationUnit,

        @Past(message = "Birth date must be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate registeredOn,

        List<String> policy
) {
    public static User create(String name, String firstName, String lastName, String emailAddress) {
        return new User(
                name,
                firstName,
                lastName,
                emailAddress,
                new ArrayList<>(),
                null,
                LocalDate.now(),
                new ArrayList<>()
        );
    }

    public User withPolicies(List<String> newPolicies) {
        return new User(name, firstName, lastName, emailAddress, organizationUnit, birthDate, registeredOn, newPolicies);
    }

    public User withOrganizationUnits(List<String> newOrganizationUnits) {
        return new User(name, firstName, lastName, emailAddress, newOrganizationUnits, birthDate, registeredOn, policy);
    }

    public User withBirthDate(LocalDate newBirthDate) {
        return new User(name, firstName, lastName, emailAddress, organizationUnit, newBirthDate, registeredOn, policy);
    }

    public User {
        if (organizationUnit == null) {
            organizationUnit = new ArrayList<>();
        }

        if (policy == null) {
            policy = new ArrayList<>();
        }
    }
}
