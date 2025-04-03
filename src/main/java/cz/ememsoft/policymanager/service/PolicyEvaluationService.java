package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.dto.UserDTO;
import lombok.NonNull;

/**
 * Service interface for policy evaluation operations.
 * Defines methods for evaluating policies against users and managing policy assignments.
 * <p>
 * This service is responsible for determining which policies apply to a given user
 * based on the policy rules and user attributes.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
public interface PolicyEvaluationService {

    /**
     * Evaluates all policies against the given user and assigns applicable policies.
     * <p>
     * This method examines all policies in the system and checks each one against
     * the user's attributes to determine if the policy should apply to the user.
     * </p>
     *
     * @param userDTO the user to evaluate policies against
     * @return the user with updated policy assignments
     * @throws NullPointerException if the user is null
     */
    UserDTO evaluatePolicies(@NonNull UserDTO userDTO);

    /**
     * Recomputes policy assignments for all users in the system.
     * <p>
     * This method is typically called when policies are modified or added,
     * to ensure that all users have the correct policies assigned to them.
     * </p>
     */
    void recomputeAllUsers();

    /**
     * Initializes the policy system with default policies if necessary.
     * <p>
     * This method is typically called during application startup to ensure
     * that the system has a base set of policies.
     * </p>
     */
    void initialize();
}