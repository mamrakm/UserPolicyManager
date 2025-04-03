package cz.ememsoft.policymanager.controller;

import cz.ememsoft.policymanager.dto.UserDTO;
import cz.ememsoft.policymanager.dto.request.UserRequest;
import cz.ememsoft.policymanager.dto.response.UserResponse;
import cz.ememsoft.policymanager.exception.ResourceNotFoundException;
import cz.ememsoft.policymanager.mapper.UserMapper;
import cz.ememsoft.policymanager.service.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user management operations.
 * Provides endpoints for creating, retrieving, updating, and deleting users.
 * <p>
 * This controller handles the conversion between request/response DTOs and internal DTOs,
 * delegating the business logic to the {@link UserService}.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Retrieves all users.
     *
     * @return a list of all users with HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers();
        List<UserResponse> userResponses = userMapper.dtosToResponses(userDTOs);
        return ResponseEntity.ok(userResponses);
    }

    /**
     * Retrieves a specific user by their name.
     *
     * @param name the unique name of the user to retrieve
     * @return the user with HTTP status 200 (OK)
     * @throws ResourceNotFoundException if the user is not found
     * @throws NullPointerException      if the name is null
     */
    @GetMapping("/{name}")
    public ResponseEntity<UserResponse> getUserByName(@NonNull @PathVariable String name) {
        UserDTO userDTO = userService.getUserByName(name);
        UserResponse userResponse = userMapper.dtoToResponse(userDTO);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Creates a new user.
     *
     * @param userRequest the user data to create
     * @return the created user with HTTP status 201 (Created)
     * @throws IllegalArgumentException if the user data is invalid
     * @throws NullPointerException     if the userRequest is null
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@NonNull @Valid @RequestBody UserRequest userRequest) {
        UserDTO userDTO = userMapper.requestToDto(userRequest);
        UserDTO createdUserDTO = userService.createUser(userDTO);
        UserResponse userResponse = userMapper.dtoToResponse(createdUserDTO);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    /**
     * Updates an existing user.
     *
     * @param name        the unique name of the user to update
     * @param userRequest the new user data
     * @return the updated user with HTTP status 200 (OK)
     * @throws ResourceNotFoundException if the user is not found
     * @throws IllegalArgumentException  if the user data is invalid
     * @throws NullPointerException      if either the name or userRequest is null
     */
    @PutMapping("/{name}")
    public ResponseEntity<UserResponse> updateUser(@NonNull @PathVariable String name,
                                                   @NonNull @Valid @RequestBody UserRequest userRequest) {
        UserDTO userDTO = userMapper.requestToDto(userRequest);
        UserDTO updatedUserDTO = userService.updateUser(name, userDTO);
        UserResponse userResponse = userMapper.dtoToResponse(updatedUserDTO);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Deletes a user.
     *
     * @param name the unique name of the user to delete
     * @return HTTP status 204 (No Content)
     * @throws ResourceNotFoundException if the user is not found
     * @throws NullPointerException      if the name is null
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteUser(@NonNull @PathVariable String name) {
        userService.deleteUser(name);
        return ResponseEntity.noContent().build();
    }
}