package cz.evolveum.policymanager.service;

import cz.evolveum.policymanager.exception.ResourceNotFoundException;
import cz.evolveum.policymanager.model.User;
import cz.evolveum.policymanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PolicyEvaluationService policyEvaluationService;

    public UserServiceImpl(UserRepository userRepository, PolicyEvaluationService policyEvaluationService) {
        this.userRepository = userRepository;
        this.policyEvaluationService = policyEvaluationService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + name));
    }

    @Override
    public User createUser(User user) {
        // Evaluate policies and save the user
        return userRepository.save(policyEvaluationService.evaluatePolicies(user));
    }

    @Override
    public User updateUser(String name, User updatedUser) {
        userRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + name));

        User userWithCorrectName = new User(
                name,
                updatedUser.firstName(),
                updatedUser.lastName(),
                updatedUser.emailAddress(),
                updatedUser.organizationUnit(),
                updatedUser.birthDate(),
                updatedUser.registeredOn(),
                updatedUser.policy()
        );

        return userRepository.save(policyEvaluationService.evaluatePolicies(userWithCorrectName));
    }

    @Override
    public void deleteUser(String name) {
        if (!userRepository.existsById(name)) {
            throw new ResourceNotFoundException("User not found with name: " + name);
        }
        userRepository.deleteById(name);
    }
}