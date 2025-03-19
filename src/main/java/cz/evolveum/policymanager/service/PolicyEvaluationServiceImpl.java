package cz.evolveum.policymanager.service;

import cz.evolveum.policymanager.model.EmailDomainIsRule;
import cz.evolveum.policymanager.model.IsMemberOfRule;
import cz.evolveum.policymanager.model.Policy;
import cz.evolveum.policymanager.model.User;
import cz.evolveum.policymanager.model.YoungerThanRule;
import cz.evolveum.policymanager.repository.PolicyRepository;
import cz.evolveum.policymanager.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyEvaluationServiceImpl implements PolicyEvaluationService {
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;

    public PolicyEvaluationServiceImpl(UserRepository userRepository, PolicyRepository policyRepository) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
    }

    @PostConstruct
    @Override
    public void initialize() {
        if (policyRepository.count() == 0) {
            var underagedPolicy = new Policy(
                    "underaged",
                    "Underaged User",
                    new YoungerThanRule(18),
                    null,
                    null
            );

            var internalUserPolicy = new Policy(
                    "internal-user",
                    "Internal User",
                    null,
                    new EmailDomainIsRule("evolveum.com"),
                    null
            );

            var developerPolicy = new Policy(
                    "developer-full-access",
                    "Developer (Full Access)",
                    null,
                    null,
                    new IsMemberOfRule("Software Development")
            );

            policyRepository.save(underagedPolicy);
            policyRepository.save(internalUserPolicy);
            policyRepository.save(developerPolicy);
        }
    }

    @Override
    public User evaluatePolicies(User user) {
        List<String> applicablePolicies = policyRepository.findAll().stream()
                .filter(policy -> policy.appliesTo(user))
                .map(Policy::id)
                .collect(Collectors.toList());

        return user.withPolicies(applicablePolicies);
    }

    @Override
    public void recomputeAllUsers() {
        userRepository.findAll().forEach(user -> {
            User updatedUser = evaluatePolicies(user);
            userRepository.save(updatedUser);
        });
    }
}