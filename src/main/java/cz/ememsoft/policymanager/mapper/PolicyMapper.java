package cz.ememsoft.policymanager.mapper;

import cz.ememsoft.policymanager.dto.PolicyDTO;
import cz.ememsoft.policymanager.dto.request.PolicyRequest;
import cz.ememsoft.policymanager.dto.response.PolicyResponse;
import cz.ememsoft.policymanager.entity.PolicyEntity;
import lombok.NonNull;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;

/**
 * MapStruct mapper interface for policy-related objects.
 * Provides mapping methods between different policy representations (Request, DTO, Entity, Response).
 * Uses Spring's component model for dependency injection and ignores null values during mapping.
 * <p>
 * This mapper handles the conversion between the complex policy request/response structure
 * (with nested rule objects) and the simpler internal DTO and entity structure
 * (with ruleType and ruleValue fields).
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PolicyMapper {

    /**
     * Maps a PolicyRequest object to a PolicyDTO.
     * Uses expressions to determine the rule type and value based on which nested rule
     * object is present in the request.
     *
     * @param request the policy request object from the controller
     * @return a PolicyDTO with ruleType and ruleValue set based on the request
     * @throws IllegalArgumentException if no rule type is found in the request
     * @throws NullPointerException     if required fields in the request are null
     */
    @Mapping(target = "ruleType", expression = "java(determineRuleType(request).orElseThrow(() -> new IllegalArgumentException(\"No rule type found in policy request\")))")
    @Mapping(target = "ruleValue", expression = "java(determineRuleValue(request).orElseThrow(() -> new IllegalArgumentException(\"No rule value found in policy request\")))")
    PolicyDTO requestToDto(@NonNull PolicyRequest request);

    /**
     * Maps a PolicyDTO to a PolicyEntity for persistence.
     *
     * @param dto the policy DTO to be converted to an entity
     * @return a PolicyEntity suitable for database storage
     * @throws NullPointerException if required fields in the DTO are null
     */
    PolicyEntity dtoToEntity(@NonNull PolicyDTO dto);

    /**
     * Maps a PolicyEntity to a PolicyDTO.
     *
     * @param entity the policy entity from the database
     * @return a PolicyDTO for service processing
     * @throws NullPointerException if the entity is null
     */
    PolicyDTO entityToDto(@NonNull PolicyEntity entity);

    /**
     * Maps a PolicyDTO to a PolicyResponse for API clients.
     * Uses expressions to create the appropriate rule object (youngerThan, emailDomainIs, or isMemberOf)
     * based on the ruleType field in the DTO.
     *
     * @param dto the policy DTO
     * @return a PolicyResponse with the appropriate rule object set
     * @throws NullPointerException if the DTO is null
     */
    @Mapping(target = "youngerThan", expression = "java(mapYoungerThan(dto).orElse(null))")
    @Mapping(target = "emailDomainIs", expression = "java(mapEmailDomainIs(dto).orElse(null))")
    @Mapping(target = "isMemberOf", expression = "java(mapIsMemberOf(dto).orElse(null))")
    PolicyResponse dtoToResponse(@NonNull PolicyDTO dto);

    /**
     * Maps a PolicyEntity directly to a PolicyResponse.
     * Uses the entityToDto and dtoToResponse methods in sequence.
     *
     * @param entity the policy entity from the database
     * @return a PolicyResponse for controller responses
     * @throws NullPointerException if the entity is null
     */
    default PolicyResponse entityToResponse(@NonNull PolicyEntity entity) {
        return dtoToResponse(entityToDto(entity));
    }

    /**
     * Maps a list of PolicyEntity objects to a list of PolicyDTO objects.
     *
     * @param entities a list of policy entities
     * @return a list of PolicyDTO objects
     * @throws NullPointerException if the entities list is null
     */
    List<PolicyDTO> entitiesToDtos(@NonNull List<PolicyEntity> entities);

    /**
     * Maps a list of PolicyDTO objects to a list of PolicyResponse objects.
     *
     * @param dtos a list of policy DTOs
     * @return a list of PolicyResponse objects
     * @throws NullPointerException if the DTOs list is null
     */
    List<PolicyResponse> dtosToResponses(@NonNull List<PolicyDTO> dtos);

    /**
     * Updates a PolicyEntity with data from a PolicyDTO.
     * Only non-null properties in the DTO will be applied to the entity.
     *
     * @param dto    the policy DTO containing update data
     * @param entity the entity to update
     * @throws NullPointerException if either the DTO or entity is null
     */
    void updateEntityFromDto(@NonNull PolicyDTO dto, @NonNull @MappingTarget PolicyEntity entity);

    /**
     * Determines the rule type based on which rule is present in the policy request.
     *
     * @param request the policy request containing one of the possible rules
     * @return an Optional containing the rule type if found, or empty Optional if no rule is found
     */
    default Optional<String> determineRuleType(@NonNull PolicyRequest request) {
        if (request.getYoungerThan() != null) return Optional.of("youngerThan");
        if (request.getEmailDomainIs() != null) return Optional.of("emailDomainIs");
        if (request.getIsMemberOf() != null) return Optional.of("isMemberOf");
        return Optional.empty();
    }

    /**
     * Extracts the rule value from the appropriate rule object in the policy request.
     *
     * @param request the policy request containing one of the possible rules
     * @return an Optional containing the rule value if found, or empty Optional if no rule is found
     */
    default Optional<String> determineRuleValue(@NonNull PolicyRequest request) {
        if (request.getYoungerThan() != null) return Optional.of(String.valueOf(request.getYoungerThan().getValue()));
        if (request.getEmailDomainIs() != null) return Optional.of(request.getEmailDomainIs().getValue());
        if (request.getIsMemberOf() != null) return Optional.of(request.getIsMemberOf().getValue());
        return Optional.empty();
    }

    /**
     * Creates a YoungerThanResponse object if the DTO has a "youngerThan" rule type.
     *
     * @param dto the policy DTO
     * @return an Optional containing a YoungerThanResponse if applicable, or empty Optional otherwise
     * @throws NumberFormatException if the ruleValue cannot be parsed as an integer
     */
    default Optional<PolicyResponse.YoungerThanResponse> mapYoungerThan(@NonNull PolicyDTO dto) {
        if ("youngerThan".equals(dto.getRuleType())) {
            return Optional.of(new PolicyResponse.YoungerThanResponse(Integer.parseInt(dto.getRuleValue())));
        }
        return Optional.empty();
    }

    /**
     * Creates an EmailDomainIsResponse object if the DTO has an "emailDomainIs" rule type.
     *
     * @param dto the policy DTO
     * @return an Optional containing an EmailDomainIsResponse if applicable, or empty Optional otherwise
     */
    default Optional<PolicyResponse.EmailDomainIsResponse> mapEmailDomainIs(@NonNull PolicyDTO dto) {
        if ("emailDomainIs".equals(dto.getRuleType())) {
            return Optional.of(new PolicyResponse.EmailDomainIsResponse(dto.getRuleValue()));
        }
        return Optional.empty();
    }

    /**
     * Creates an IsMemberOfResponse object if the DTO has an "isMemberOf" rule type.
     *
     * @param dto the policy DTO
     * @return an Optional containing an IsMemberOfResponse if applicable, or empty Optional otherwise
     */
    default Optional<PolicyResponse.IsMemberOfResponse> mapIsMemberOf(@NonNull PolicyDTO dto) {
        if ("isMemberOf".equals(dto.getRuleType())) {
            return Optional.of(new PolicyResponse.IsMemberOfResponse(dto.getRuleValue()));
        }
        return Optional.empty();
    }
}