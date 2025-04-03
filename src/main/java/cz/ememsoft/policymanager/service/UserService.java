package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.dto.UserDTO;
import lombok.NonNull;

import java.util.List;

/**
 * Service interface for managing users.
 * Provides methods for CRUD operations on users and handles business logic.
 * <p>
 * The UserService acts as an intermediate layer between controllers and repositories,
 * providing a unified interface for user operations while enforcing business rules.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
public interface UserService {
    
    /**
     * Retrieves all users from the system.
     *
     * @return a list of all users represented as UserDTO objects
     */
    List<UserDTO> getAllUsers();
    
    /**
     * Retrieves a specific user by their unique username.
     *
     * @param name the unique username of the user to retrieve
     * @return the user as a UserDTO
     * @throws cz.ememsoft.policymanager.exception.ResourceNotFoundException if no user with the given name exists
     * @throws NullPointerException if the name is null
     */
    UserDTO getUserByName(@NonNull String name);
    
    /**
     * Creates a new user in the system.
     * <p>
     * This method also evaluates applicable policies and assigns them to the user.
     * </p>
     *
     * @param userDTO the user data to create
     * @return the created user as a UserDTO, including assigned policies
     * @throws IllegalArgumentException if the user data is invalid
     * @throws NullPointerException if the userDTO is null
     */
    UserDTO createUser(@NonNull UserDTO userDTO);
    
    /**
     * Updates an existing user in the system.
     * <p>
     * This method also re-evaluates applicable policies and updates policy assignments.
     * </p>
     *
     * @param name the unique username of the user to update
     * @param userDTO the new user data
     * @return the updated user as a UserDTO, including updated policy assignments
     * @throws cz.ememsoft.policymanager.exception.ResourceNotFoundException if no user with the given name exists
     * @throws IllegalArgumentException if the user data is invalid
     * @throws NullPointerException if either the name or userDTO is null
     */
    UserDTO updateUser(@NonNull String name, @NonNull UserDTO userDTO);
    
    /**
     * Deletes a user from the system.
     *
     * @param name the unique username of the user to delete
     * @throws cz.ememsoft.policymanager.exception.ResourceNotFoundException if no user with the given name exists
     * @throws NullPointerException if the name is null
     */
    void deleteUser(@NonNull String name);
}
