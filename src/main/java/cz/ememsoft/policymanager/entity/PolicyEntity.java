package cz.ememsoft.policymanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity class representing a policy in the system.
 * This entity is mapped to the 'policies' table in the database.
 * <p>
 * Each policy contains a rule type and value that determines which users it applies to.
 * The policy entity serves as the configuration for policy evaluation against users.
 * </p>
 * <p>
 * The ruleType field specifies the type of rule (youngerThan, emailDomainIs, or isMemberOf),
 * while the ruleValue field contains the specific value for that rule.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */

@Entity
@Table(name = "policies")
@Getter
@Setter
@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String ruleType;

    @Column
    private String ruleValue;
}