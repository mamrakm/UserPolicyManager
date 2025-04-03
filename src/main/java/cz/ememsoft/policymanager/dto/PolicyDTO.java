package cz.ememsoft.policymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal Data Transfer Object (DTO) for policy data.
 * This class is used for passing policy data between services and for internal processing.
 * It acts as an intermediary between the controller layer (request/response DTOs) and
 * the persistence layer (entities). Policy rules are represented by ruleType and ruleValue fields.
 * <p>
 * The simplified structure with ruleType and ruleValue fields allows for easier storage
 * and retrieval from the database, while still maintaining the necessary information
 * to evaluate policies against users.
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
public class PolicyDTO {

    private String id;
    private String name;
    private String ruleType;  // "youngerThan", "emailDomainIs", or "isMemberOf"
    private String ruleValue;
}