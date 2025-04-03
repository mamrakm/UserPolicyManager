package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.dto.PolicyDTO;
import cz.ememsoft.policymanager.dto.UserDTO;
import cz.ememsoft.policymanager.entity.UserEntity;
import cz.ememsoft.policymanager.mapper.PolicyMapper;
import cz.ememsoft.policymanager.mapper.UserMapper;
import cz.ememsoft.policymanager.repository.PolicyRepository;
import cz.ememsoft.policymanager.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link PolicyEvaluationService} interface.
 * Provides business logic for evaluating policies against users and initializing default policies.
 * <p>
 * This service determines which policies apply to a user based on the policy rules
 * and user attributes, and manages policy assignments.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class PolicyEvaluationServiceImpl implements PolicyEvaluationService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final UserMapper userMapper;
    private final PolicyMapper policyMapper;

    /**
     * Initializes the system with default policies if no policies exist.
     * This method is automatically called after the bean is constructed.
     * <p>
     * The default policies are:
     * <ul>
     *   <li>underaged - applies to users younger than 18 years</li>
     *   <li>internal-user - applies to users with @evolveum.com email addresses</li>
     *   <li>developer-full-access - applies to users who are members of "Software Development"</li>
     * </ul>
     * </p>
     */
    @PostConstruct
    @Override
    @Transactional
    public void initialize() {
        if (policyRepository.count() == 0) {
            // Create underaged policy
            PolicyDTO underagedPolicy = PolicyDTO.builder()
                    .id("underaged")
                    .name("Underaged User")
                    .ruleType("youngerThan")
                    .ruleValue("18")
                    .build();

            // Create internal user policy
            PolicyDTO internalUserPolicy = PolicyDTO.builder()
                    .id("internal-user")
                    .name("Internal User")
                    .ruleType("emailDomainIs")
                    .ruleValue("evolveum.com")
                    .build();

            // Create developer policy
            PolicyDTO developerPolicy = PolicyDTO.builder()
                    .id("developer-full-access")
                    .name("Developer (Full Access)")
                    .ruleType("isMemberOf")
                    .ruleValue("Software Development")
                    .build();

            // Save policies
            policyRepository.save(policyMapper.dtoToEntity(underagedPolicy));
            policyRepository.save(policyMapper.dtoToEntity(internalUserPolicy));
            policyRepository.save(policyMapper.dtoToEntity(developerPolicy));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserDTO evaluatePolicies(@NonNull UserDTO userDTO) {
        List<String> applicablePolicies = new ArrayList<>();

        List<PolicyDTO> policies = policyMapper.entitiesToDtos(policyRepository.findAll());

        for (PolicyDTO policy : policies) {
            if (policyApplies(policy, userDTO)) {
                applicablePolicies.add(policy.getId());
            }
        }

        // Update the user's policies
        userDTO.setPolicy(applicablePolicies);
        return userDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void recomputeAllUsers() {
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            UserDTO userDTO = userMapper.entityToDto(user);
            UserDTO updatedUserDTO = evaluatePolicies(userDTO);
            UserEntity updatedUser = userMapper.dtoToEntity(updatedUserDTO);
            userRepository.save(updatedUser);
        }
    }

    /**
     * Determines if a policy applies to a user based on the policy's rule type and value.
     *
     * @param policy the policy to evaluate
     * @param user   the user to check against the policy
     * @return true if the policy applies to the user, false otherwise
     * @throws IllegalArgumentException if the rule type is not recognized
     */
    private boolean policyApplies(@NonNull PolicyDTO policy, @NonNull UserDTO user) {
        String ruleType = policy.getRuleType();
        String ruleValue = policy.getRuleValue();

        if (ruleType == null || ruleValue == null) {
            return false;
        }

        return switch (ruleType) {
            case "youngerThan" -> isYoungerThan(user, Integer.parseInt(ruleValue));
            case "emailDomainIs" -> hasEmailDomain(user, ruleValue);
            case "isMemberOf" -> isMemberOf(user, ruleValue);
            default -> throw new IllegalArgumentException("Unknown rule type: " + ruleType);
        };
    }

    /**
     * Checks if a user is younger than the specified age.
     *
     * @param user the user to check
     * @param age  the age threshold
     * @return true if the user is younger than the specified age, false otherwise
     */
    private boolean isYoungerThan(@NonNull UserDTO user, int age) {
        return Optional.ofNullable(user.getBirthDate())
                .map(birthDate -> Period.between(birthDate, LocalDate.now()).getYears() < age)
                .orElse(false);
    }

    /**
     * Checks if a user's email address has the specified domain.
     *
     * @param user   the user to check
     * @param domain the domain to check for (without @)
     * @return true if the user's email domain matches, false otherwise
     */
    private boolean hasEmailDomain(@NonNull UserDTO user, @NonNull String domain) {
        return Optional.ofNullable(user.getEmailAddress())
                .map(email -> email.endsWith("@" + domain))
                .orElse(false);
    }

    /**
     * Checks if a user is a member of the specified organization unit.
     *
     * @param user             the user to check
     * @param organizationUnit the organization unit to check membership for
     * @return true if the user is a member of the specified organization unit, false otherwise
     */
    private boolean isMemberOf(@NonNull UserDTO user, @NonNull String organizationUnit) {
        return Optional.ofNullable(user.getOrganizationUnit())
                .map(units -> units.contains(organizationUnit))
                .orElse(false);
    }
}