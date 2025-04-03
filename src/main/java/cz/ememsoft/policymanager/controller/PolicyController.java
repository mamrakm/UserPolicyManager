package cz.ememsoft.policymanager.controller;

import cz.ememsoft.policymanager.dto.PolicyDTO;
import cz.ememsoft.policymanager.dto.request.PolicyRequest;
import cz.ememsoft.policymanager.dto.response.PolicyResponse;
import cz.ememsoft.policymanager.exception.ResourceNotFoundException;
import cz.ememsoft.policymanager.mapper.PolicyMapper;
import cz.ememsoft.policymanager.service.PolicyService;
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
 * REST controller for policy management operations.
 * Provides endpoints for creating, retrieving, updating, and deleting policies.
 * <p>
 * This controller handles the conversion between request/response DTOs and internal DTOs,
 * delegating the business logic to the {@link PolicyService}.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final PolicyMapper policyMapper;

    /**
     * Retrieves all policies.
     *
     * @return a list of all policies with HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        List<PolicyDTO> policyDTOs = policyService.getAllPolicies();
        List<PolicyResponse> policyResponses = policyMapper.dtosToResponses(policyDTOs);
        return ResponseEntity.ok(policyResponses);
    }

    /**
     * Retrieves a specific policy by its ID.
     *
     * @param id the unique ID of the policy to retrieve
     * @return the policy with HTTP status 200 (OK)
     * @throws ResourceNotFoundException if the policy is not found
     * @throws NullPointerException      if the id is null
     */
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> getPolicyById(@NonNull @PathVariable String id) {
        PolicyDTO policyDTO = policyService.getPolicyById(id);
        PolicyResponse policyResponse = policyMapper.dtoToResponse(policyDTO);
        return ResponseEntity.ok(policyResponse);
    }

    /**
     * Creates a new policy.
     *
     * @param policyRequest the policy data to create
     * @return the created policy with HTTP status 201 (Created)
     * @throws IllegalArgumentException if the policy data is invalid
     * @throws NullPointerException     if the policyRequest is null
     */
    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(@NonNull @Valid @RequestBody PolicyRequest policyRequest) {
        PolicyDTO policyDTO = policyMapper.requestToDto(policyRequest);
        PolicyDTO createdPolicyDTO = policyService.createPolicy(policyDTO);
        PolicyResponse policyResponse = policyMapper.dtoToResponse(createdPolicyDTO);
        return new ResponseEntity<>(policyResponse, HttpStatus.CREATED);
    }

    /**
     * Updates an existing policy.
     *
     * @param id            the unique ID of the policy to update
     * @param policyRequest the new policy data
     * @return the updated policy with HTTP status 200 (OK)
     * @throws ResourceNotFoundException if the policy is not found
     * @throws IllegalArgumentException  if the policy data is invalid
     * @throws NullPointerException      if either the id or policyRequest is null
     */
    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponse> updatePolicy(
            @NonNull @PathVariable String id,
            @NonNull @Valid @RequestBody PolicyRequest policyRequest) {

        PolicyDTO policyDTO = policyMapper.requestToDto(policyRequest);
        PolicyDTO updatedPolicyDTO = policyService.updatePolicy(id, policyDTO);
        PolicyResponse policyResponse = policyMapper.dtoToResponse(updatedPolicyDTO);
        return ResponseEntity.ok(policyResponse);
    }

    /**
     * Deletes a policy.
     *
     * @param id the unique ID of the policy to delete
     * @return HTTP status 204 (No Content)
     * @throws ResourceNotFoundException if the policy is not found
     * @throws NullPointerException      if the id is null
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@NonNull @PathVariable String id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}