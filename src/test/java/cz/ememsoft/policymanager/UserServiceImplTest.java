package cz.ememsoft.policymanager;

import cz.ememsoft.policymanager.exception.ResourceNotFoundException;
import cz.ememsoft.policymanager.model.User;
import cz.ememsoft.policymanager.repository.UserRepository;
import cz.ememsoft.policymanager.service.PolicyEvaluationService;
import cz.ememsoft.policymanager.service.UserService;
import cz.ememsoft.policymanager.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PolicyEvaluationService policyEvaluationService;

    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, policyEvaluationService);

        testUser = new User(
                "jdoe",
                "John",
                "Doe",
                "jdoe@evolveum.com",
                List.of("Software Development", "Support"),
                LocalDate.of(2007, 9, 7),
                LocalDate.of(2024, 5, 7),
                List.of("underaged", "internal-user", "developer-full-access")
        );
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("jdoe", result.getFirst().name());
    }

    @Test
    void testGetUserByName() {
        when(userRepository.findById("jdoe")).thenReturn(Optional.of(testUser));

        User result = userService.getUserByName("jdoe");

        assertNotNull(result);
        assertEquals("jdoe", result.name());
    }

    @Test
    void testGetUserByName_NotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.getUserByName("nonexistent")
        );
    }

    @Test
    void testCreateUser() {
        User newUser = new User(
                "newuser",
                "New",
                "User",
                "new@evolveum.com",
                new ArrayList<>(),
                null,
                LocalDate.now(),
                new ArrayList<>()
        );

        User evaluatedUser = new User(
                "newuser",
                "New",
                "User",
                "new@evolveum.com",
                new ArrayList<>(),
                null,
                LocalDate.now(),
                List.of("internal-user")
        );

        when(policyEvaluationService.evaluatePolicies(newUser)).thenReturn(evaluatedUser);
        when(userRepository.save(evaluatedUser)).thenReturn(evaluatedUser);

        User result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals("newuser", result.name());
        assertEquals(1, result.policy().size());
    }

    @Test
    void testUpdateUser() {
        User updatedUser = new User(
                "jdoe",
                "John",
                "Doe Updated",
                "jdoe@evolveum.com",
                new ArrayList<>(),
                null,
                LocalDate.now(),
                new ArrayList<>()
        );

        when(userRepository.findById("jdoe")).thenReturn(Optional.of(testUser));
        when(policyEvaluationService.evaluatePolicies(any(User.class))).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser("jdoe", updatedUser);

        assertNotNull(result);
        assertEquals("Doe Updated", result.lastName());
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedUser = new User(
                "nonexistent",
                "Non",
                "Existent",
                "non@example.com",
                new ArrayList<>(),
                null,
                LocalDate.now(),
                new ArrayList<>()
        );

        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser("nonexistent", updatedUser)
        );
    }

    @Test
    void testDeleteUser() {
        when(userRepository.existsById("jdoe")).thenReturn(true);

        userService.deleteUser("jdoe");

        verify(userRepository, times(1)).deleteById("jdoe");
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUser("nonexistent")
        );
    }
}
