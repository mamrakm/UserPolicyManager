package cz.ememsoft.policymanager.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for policy creation and update requests.
 * This class is used exclusively in controllers to receive policy data from API clients.
 * It contains validation annotations to ensure data integrity and nested classes
 * for the different types of policy rules.
 * <p>
 * The policy request includes the policy ID, name, and exactly one rule definition
 * (youngerThan, emailDomainIs, or isMemberOf).
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyRequest {

    @NotBlank(message = "Policy ID is required")
    private String id;

    @NotBlank(message = "Policy name is required")
    private String name;

    private YoungerThanRequest youngerThan;
    private EmailDomainIsRequest emailDomainIs;
    private IsMemberOfRequest isMemberOf;

    /**
     * Nested class representing the "younger than" rule for policies.
     * This rule applies to users below a certain age threshold.
     * <p>
     * The value field represents the age in years below which the policy will apply.
     * </p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YoungerThanRequest {
        /**
         * The age threshold in years.
         */
        private int value;
    }

    /**
     * Nested class representing the "email domain is" rule for policies.
     * This rule applies to users with email addresses matching the specified domain.
     * <p>
     * The value field contains the email domain without the @ symbol.
     * </p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailDomainIsRequest {
        /**
         * The email domain value (e.g., "example.com").
         */
        private String value;
    }

    /**
     * Nested class representing the "is member of" rule for policies.
     * This rule applies to users who are members of a specified organization unit.
     * <p>
     * The value field contains the exact name of the organization unit.
     * </p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IsMemberOfRequest {
        /**
         * The organization unit name.
         */
        private String value;
    }
}