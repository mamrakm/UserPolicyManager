package cz.ememsoft.policymanager;

import cz.ememsoft.policymanager.model.EmailDomainIsRule;
import cz.ememsoft.policymanager.model.IsMemberOfRule;
import cz.ememsoft.policymanager.model.Policy;
import cz.ememsoft.policymanager.model.User;
import cz.ememsoft.policymanager.model.YoungerThanRule;
import cz.ememsoft.policymanager.repository.PolicyRepository;
import cz.ememsoft.policymanager.repository.UserRepository;
import cz.ememsoft.policymanager.service.PolicyEvaluationService;
import cz.ememsoft.policymanager.service.PolicyEvaluationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyEvaluationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PolicyRepository policyRepository;

    private PolicyEvaluationService policyEvaluationService;
    private User testUser;
    private List<Policy> testPolicies;

    @BeforeEach
    void setUp() {
        policyEvaluationService = new PolicyEvaluationServiceImpl(userRepository, policyRepository);

        testUser = new User(
                "jdoe",
                "John",
                "Doe",
                "jdoe@evolveum.com",
                List.of("Software Development", "Support"),
                LocalDate.of(2007, 9, 7),
                LocalDate.of(2024, 5, 7),
                new ArrayList<>()
        );

        testPolicies = new ArrayList<>();

        Policy underagedPolicy = new Policy(
                "underaged",
                "Underaged User",
                new YoungerThanRule(18),
                null,
                null
        );
        testPolicies.add(underagedPolicy);

        Policy internalUserPolicy = new Policy(
                "internal-user",
                "Internal User",
                null,
                new EmailDomainIsRule("evolveum.com"),
                null
        );
        testPolicies.add(internalUserPolicy);

        Policy developerPolicy = new Policy(
                "developer-full-access",
                "Developer (Full Access)",
                null,
                null,
                new IsMemberOfRule("Software Development")
        );
        testPolicies.add(developerPolicy);
    }

    @Test
    void testEvaluatePolicies() {
        when(policyRepository.findAll()).thenReturn(testPolicies);

        User evaluatedUser = policyEvaluationService.evaluatePolicies(testUser);

        assertNotNull(evaluatedUser.policy());
        assertEquals(3, evaluatedUser.policy().size());
        assertTrue(evaluatedUser.policy().contains("underaged"));
        assertTrue(evaluatedUser.policy().contains("internal-user"));
        assertTrue(evaluatedUser.policy().contains("developer-full-access"));
    }

    @Test
    void testEvaluatePolicies_NoApplicablePolicies() {
        User adultExternalUser = new User(
                "adult",
                "Adult",
                "User",
                "adult@external.com",
                new ArrayList<>(),
                LocalDate.of(1990, 1, 1),
                LocalDate.now(),
                new ArrayList<>()
        );

        when(policyRepository.findAll()).thenReturn(testPolicies);

        User evaluatedUser = policyEvaluationService.evaluatePolicies(adultExternalUser);

        assertNotNull(evaluatedUser.policy());
        assertEquals(0, evaluatedUser.policy().size());
    }

    @Test
    void testRecomputeAllUsers() {
        List<User> allUsers = List.of(testUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(policyRepository.findAll()).thenReturn(testPolicies);

        policyEvaluationService.recomputeAllUsers();

        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).save(any(User.class));
    }
}