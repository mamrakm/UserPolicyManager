package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.dto.PolicyDTO;
import lombok.NonNull;

import java.util.List;

/**
 * Service interface for managing policies.
 * Provides methods for CRUD operations on policies and handles policy-related business logic.
 * <p>
 * The PolicyService acts as an intermediate layer between controllers and repositories,
 * providing a unified interface for policy operations while enforcing business rules.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
public interface PolicyService {

    /**
     * Retrieves all policies from the system.
     *
     * @return a list of all policies represented as PolicyDTO objects
     */
    List<PolicyDTO> getAllPolicies();

    /**
     * Retrieves a specific policy by its unique ID.
     *
     * @param id the unique ID of the policy to retrieve
     * @return the policy as a PolicyDTO
     * @throws cz.ememsoft.policymanager.exception.ResourceNotFoundException if no policy with the given ID exists
     * @throws NullPointerException                                          if the id is null
     */
    PolicyDTO getPolicyById(@NonNull String id);

    /**
     * Creates a new policy in the system.
     * <p>
     * After creating the policy, this method triggers a recomputation of all users' policy assignments.
     * </p>
     *
     * @param policyDTO the policy data to create
     * @return the created policy as a PolicyDTO
     * @throws IllegalArgumentException if the policy data is invalid
     * @throws NullPointerException     if the policyDTO is null
     */
    PolicyDTO createPolicy(@NonNull PolicyDTO policyDTO);

    /**
     * Updates an existing policy in the system.
     * <p>
     * After updating the policy, this method triggers a recomputation of all users' policy assignments.
     * </p>
     *
     * @param id        the unique ID of the policy to update
     * @param policyDTO the new policy data
     * @return the updated policy as a PolicyDTO
     * @throws cz.ememsoft.policymanager.exception.ResourceNotFoundException if no policy with the given ID exists
     * @throws IllegalArgumentException                                      if the policy data is invalid
     * @throws NullPointerException                                          if either the id or policyDTO is null
     */
    PolicyDTO updatePolicy(@NonNull String id, @NonNull PolicyDTO policyDTO);

    /**
     * Deletes a policy from the system.
     * <p>
     * After deleting the policy, this method triggers a recomputation of all users' policy assignments.
     * </p>
     *
     * @param id the unique ID of the policy to delete
     * @throws cz.ememsoft.policymanager.exception.ResourceNotFoundException if no policy with the given ID exists
     * @throws NullPointerException                                          if the id is null
     */
    void deletePolicy(@NonNull String id);
}