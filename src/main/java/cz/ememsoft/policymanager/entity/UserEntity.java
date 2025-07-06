package cz.ememsoft.policymanager.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a user in the system.
 * This entity is mapped to the 'users' table in the database.
 * <p>
 * The user entity contains all the essential information about a user including
 * their personal details, organization memberships, and applicable policies.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private String name;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String emailAddress;

    @ElementCollection
    @CollectionTable(name = "user_organization_units", joinColumns = @JoinColumn(name = "user_name"))
    @Column(name = "organization_unit")
    @Builder.Default
    private List<String> organizationUnit = new ArrayList<>();

    @Column
    private LocalDate birthDate;

    @Column
    private LocalDate registeredOn;

    @ElementCollection
    @CollectionTable(name = "user_policies", joinColumns = @JoinColumn(name = "user_name"))
    @Builder.Default
    private List<String> policy = new ArrayList<>();
}