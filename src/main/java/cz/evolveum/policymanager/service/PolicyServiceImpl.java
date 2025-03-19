package cz.evolveum.policymanager.service;

import cz.evolveum.policymanager.exception.ResourceNotFoundException;
import cz.evolveum.policymanager.model.Policy;
import cz.evolveum.policymanager.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyServiceImpl implements PolicyService {
    private final PolicyRepository policyRepository;
    private final PolicyEvaluationService policyEvaluationService;

    public PolicyServiceImpl(PolicyRepository policyRepository, PolicyEvaluationService policyEvaluationService) {
        this.policyRepository = policyRepository;
        this.policyEvaluationService = policyEvaluationService;
    }

    @Override
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Override
    public Policy getPolicyById(String id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
    }

    @Override
    public Policy createPolicy(Policy policy) {
        Policy savedPolicy = policyRepository.save(policy);

        policyEvaluationService.recomputeAllUsers();

        return savedPolicy;
    }

    @Override
    public Policy updatePolicy(String id, Policy updatedPolicy) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }

        Policy policyWithCorrectId = new Policy(
                id,
                updatedPolicy.name(),
                updatedPolicy.youngerThanRule(),
                updatedPolicy.emailDomainIsRule(),
                updatedPolicy.isMemberOfRule()
        );

        Policy savedPolicy = policyRepository.save(policyWithCorrectId);

        policyEvaluationService.recomputeAllUsers();

        return savedPolicy;
    }

    @Override
    public void deletePolicy(String id) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }

        policyRepository.deleteById(id);

        policyEvaluationService.recomputeAllUsers();
    }
}