package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.dto.UserDTO;
import cz.ememsoft.policymanager.entity.UserEntity;
import cz.ememsoft.policymanager.exception.ResourceNotFoundException;
import cz.ememsoft.policymanager.mapper.UserMapper;
import cz.ememsoft.policymanager.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the {@link UserService} interface.
 * Provides business logic for user management and policy evaluation.
 * <p>
 * This service coordinates between repositories and mappers, enforces business rules,
 * and ensures that policies are properly evaluated and applied to users.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PolicyEvaluationService policyEvaluationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return userMapper.entitiesToDtos(users);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByName(@NonNull String name) {
        UserEntity user = userRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + name));
        return userMapper.entityToDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserDTO createUser(@NonNull UserDTO userDTO) {
        // Set registration date if it's not set
        if (userDTO.getRegisteredOn() == null) {
            userDTO.setRegisteredOn(LocalDate.now());
        }

        // Evaluate policies
        UserDTO evaluatedUserDTO = policyEvaluationService.evaluatePolicies(userDTO);

        // Convert to entity and save
        UserEntity userEntity = userMapper.dtoToEntity(evaluatedUserDTO);
        UserEntity savedEntity = userRepository.save(userEntity);

        // Return the saved DTO
        return userMapper.entityToDto(savedEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserDTO updateUser(@NonNull String name, @NonNull UserDTO userDTO) {
        // Verify the user exists
        if (!userRepository.existsById(name)) {
            throw new ResourceNotFoundException("User not found with name: " + name);
        }

        // Ensure the user has the correct name
        userDTO.setName(name);

        // Evaluate policies
        UserDTO evaluatedUserDTO = policyEvaluationService.evaluatePolicies(userDTO);

        // Convert to entity and save
        UserEntity userEntity = userMapper.dtoToEntity(evaluatedUserDTO);
        UserEntity savedEntity = userRepository.save(userEntity);

        // Return the saved DTO
        return userMapper.entityToDto(savedEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUser(@NonNull String name) {
        // Verify the user exists
        if (!userRepository.existsById(name)) {
            throw new ResourceNotFoundException("User not found with name: " + name);
        }

        userRepository.deleteById(name);
    }
}