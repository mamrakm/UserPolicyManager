package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.dto.PolicyDTO;
import cz.ememsoft.policymanager.entity.PolicyEntity;
import cz.ememsoft.policymanager.exception.ResourceNotFoundException;
import cz.ememsoft.policymanager.mapper.PolicyMapper;
import cz.ememsoft.policymanager.repository.PolicyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the {@link PolicyService} interface.
 * Provides business logic for policy management and coordinates
 * policy evaluation when policies are modified.
 * <p>
 * This service ensures that whenever policies are created, updated, or deleted,
 * the policy assignments for all users are recomputed to maintain consistency.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    private final PolicyEvaluationService policyEvaluationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<PolicyDTO> getAllPolicies() {
        List<PolicyEntity> policies = policyRepository.findAll();
        return policyMapper.entitiesToDtos(policies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PolicyDTO getPolicyById(@NonNull String id) {
        PolicyEntity policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        return policyMapper.entityToDto(policy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PolicyDTO createPolicy(@NonNull PolicyDTO policyDTO) {
        // Convert to entity and save
        PolicyEntity policyEntity = policyMapper.dtoToEntity(policyDTO);
        PolicyEntity savedEntity = policyRepository.save(policyEntity);

        // Recompute policy assignments for all users
        policyEvaluationService.recomputeAllUsers();

        // Return the saved DTO
        return policyMapper.entityToDto(savedEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PolicyDTO updatePolicy(@NonNull String id, @NonNull PolicyDTO policyDTO) {
        // Verify the policy exists
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }

        // Ensure the policy has the correct ID
        policyDTO.setId(id);

        // Convert to entity and save
        PolicyEntity policyEntity = policyMapper.dtoToEntity(policyDTO);
        PolicyEntity savedEntity = policyRepository.save(policyEntity);

        // Recompute policy assignments for all users
        policyEvaluationService.recomputeAllUsers();

        // Return the saved DTO
        return policyMapper.entityToDto(savedEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deletePolicy(@NonNull String id) {
        // Verify the policy exists
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }

        // Delete the policy
        policyRepository.deleteById(id);

        // Recompute policy assignments for all users
        policyEvaluationService.recomputeAllUsers();
    }
}