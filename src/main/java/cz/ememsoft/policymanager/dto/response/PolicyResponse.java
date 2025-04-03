package cz.ememsoft.policymanager.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for policy response objects.
 * This class is used exclusively in controllers to return policy data to API clients.
 * It contains the complete policy information with nested classes for different rule types.
 * <p>
 * The response includes the policy ID, name, and the rule that defines the policy's conditions.
 * Exactly one rule type (youngerThan, emailDomainIs, or isMemberOf) will be included
 * in the response, corresponding to the policy's configuration.
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
public class PolicyResponse {

    private String id;
    private String name;
    private YoungerThanResponse youngerThan;
    private EmailDomainIsResponse emailDomainIs;
    private IsMemberOfResponse isMemberOf;

    /**
     * Nested class representing the "younger than" rule in policy responses.
     * Contains the age threshold value for the rule.
     * <p>
     * When this rule is present in a policy response, it indicates that the policy
     * applies to users who are younger than the specified age.
     * </p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YoungerThanResponse {
        /**
         * The age threshold in years.
         */
        private int value;
    }

    /**
     * Nested class representing the "email domain is" rule in policy responses.
     * Contains the email domain value for the rule.
     * <p>
     * When this rule is present in a policy response, it indicates that the policy
     * applies to users whose email addresses match the specified domain.
     * </p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailDomainIsResponse {
        /**
         * The email domain value (e.g., "example.com").
         */
        private String value;
    }

    /**
     * Nested class representing the "is member of" rule in policy responses.
     * Contains the organization unit value for the rule.
     * <p>
     * When this rule is present in a policy response, it indicates that the policy
     * applies to users who are members of the specified organization unit.
     * </p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IsMemberOfResponse {
        /**
         * The organization unit name.
         */
        private String value;
    }
}