package cz.ememsoft.policymanager;

import cz.ememsoft.policymanager.exception.ResourceNotFoundException;
import cz.ememsoft.policymanager.model.EmailDomainIsRule;
import cz.ememsoft.policymanager.model.Policy;
import cz.ememsoft.policymanager.repository.PolicyRepository;
import cz.ememsoft.policymanager.service.PolicyEvaluationService;
import cz.ememsoft.policymanager.service.PolicyService;
import cz.ememsoft.policymanager.service.PolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyEvaluationService policyEvaluationService;

    private PolicyService policyService;
    private Policy testPolicy;

    @BeforeEach
    void setUp() {
        policyService = new PolicyServiceImpl(policyRepository, policyEvaluationService);

        testPolicy = new Policy(
                "internal-user",
                "Internal User",
                null,
                new EmailDomainIsRule("evolveum.com"),
                null
        );
    }

    @Test
    void testGetAllPolicies() {
        when(policyRepository.findAll()).thenReturn(List.of(testPolicy));

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(1, result.size());
        assertEquals("internal-user", result.get(0).id());
    }

    @Test
    void testGetPolicyById() {
        when(policyRepository.findById("internal-user")).thenReturn(Optional.of(testPolicy));

        Policy result = policyService.getPolicyById("internal-user");

        assertNotNull(result);
        assertEquals("internal-user", result.id());
        assertEquals("Internal User", result.name());
    }

    @Test
    void testGetPolicyById_NotFound() {
        when(policyRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                policyService.getPolicyById("nonexistent")
        );
    }

    @Test
    void testCreatePolicy() {
        when(policyRepository.save(testPolicy)).thenReturn(testPolicy);
        doNothing().when(policyEvaluationService).recomputeAllUsers();

        Policy result = policyService.createPolicy(testPolicy);

        assertNotNull(result);
        assertEquals("internal-user", result.id());
        verify(policyEvaluationService, times(1)).recomputeAllUsers();
    }

    @Test
    void testUpdatePolicy() {
        Policy updatedPolicy = new Policy(
                "internal-user",
                "Internal User Updated",
                null,
                new EmailDomainIsRule("evolveum.org"), // Changed domain
                null
        );

        when(policyRepository.existsById("internal-user")).thenReturn(true);
        when(policyRepository.save(any(Policy.class))).thenReturn(updatedPolicy);
        doNothing().when(policyEvaluationService).recomputeAllUsers();

        Policy result = policyService.updatePolicy("internal-user", updatedPolicy);

        assertNotNull(result);
        assertEquals("Internal User Updated", result.name());
        assertEquals("evolveum.org", result.emailDomainIsRule().value());
        verify(policyEvaluationService, times(1)).recomputeAllUsers();
    }

    @Test
    void testUpdatePolicy_NotFound() {
        when(policyRepository.existsById("nonexistent")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                policyService.updatePolicy("nonexistent", testPolicy)
        );
    }

    @Test
    void testDeletePolicy() {
        when(policyRepository.existsById("internal-user")).thenReturn(true);
        doNothing().when(policyRepository).deleteById("internal-user");
        doNothing().when(policyEvaluationService).recomputeAllUsers();

        policyService.deletePolicy("internal-user");

        verify(policyRepository, times(1)).deleteById("internal-user");
        verify(policyEvaluationService, times(1)).recomputeAllUsers();
    }

    @Test
    void testDeletePolicy_NotFound() {
        when(policyRepository.existsById("nonexistent")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                policyService.deletePolicy("nonexistent")
        );
    }
}