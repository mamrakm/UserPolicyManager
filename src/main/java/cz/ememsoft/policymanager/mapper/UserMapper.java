package cz.ememsoft.policymanager.mapper;

import cz.ememsoft.policymanager.dto.UserDTO;
import cz.ememsoft.policymanager.dto.request.UserRequest;
import cz.ememsoft.policymanager.dto.response.UserResponse;
import cz.ememsoft.policymanager.entity.UserEntity;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper interface for user-related objects.
 * Provides mapping methods between different user representations (Request, DTO, Entity, Response).
 * Uses Spring's component model for dependency injection and ignores null values during mapping.
 * <p>
 * This mapper ensures proper conversion between the various DTO layers and the entity layer,
 * maintaining separation between the API contract and the internal data model.
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
public interface UserMapper {

    /**
     * Maps a UserRequest object to a UserDTO.
     * Ignores the registeredOn and policy fields as they are set by the service.
     *
     * @param request the user request object from the controller
     * @return a UserDTO object for service processing
     * @throws NullPointerException if required fields in the request are null
     */
    @Mapping(target = "registeredOn", ignore = true)
    @Mapping(target = "policy", ignore = true)
    UserDTO requestToDto(@NonNull UserRequest request);
    
    /**
     * Maps a UserDTO to a UserEntity for persistence.
     *
     * @param dto the user DTO to be converted to an entity
     * @return a UserEntity suitable for database storage
     * @throws NullPointerException if required fields in the DTO are null
     */
    UserEntity dtoToEntity(@NonNull UserDTO dto);
    
    /**
     * Maps a UserEntity to a UserDTO.
     *
     * @param entity the user entity from the database
     * @return a UserDTO for service processing
     * @throws NullPointerException if the entity is null
     */
    UserDTO entityToDto(@NonNull UserEntity entity);
    
    /**
     * Maps a UserDTO to a UserResponse for API clients.
     *
     * @param dto the user DTO
     * @return a UserResponse suitable for controller responses
     * @throws NullPointerException if the DTO is null
     */
    UserResponse dtoToResponse(@NonNull UserDTO dto);
    
    /**
     * Maps a UserEntity directly to a UserResponse.
     * Uses the entityToDto and dtoToResponse methods in sequence.
     *
     * @param entity the user entity from the database
     * @return a UserResponse for controller responses
     * @throws NullPointerException if the entity is null
     */
    default UserResponse entityToResponse(@NonNull UserEntity entity) {
        return dtoToResponse(entityToDto(entity));
    }
    
    /**
     * Maps a list of UserEntity objects to a list of UserDTO objects.
     *
     * @param entities a list of user entities
     * @return a list of UserDTO objects
     * @throws NullPointerException if the entities list is null
     */
    List<UserDTO> entitiesToDtos(@NonNull List<UserEntity> entities);
    
    /**
     * Maps a list of UserDTO objects to a list of UserResponse objects.
     *
     * @param dtos a list of user DTOs
     * @return a list of UserResponse objects
     * @throws NullPointerException if the DTOs list is null
     */
    List<UserResponse> dtosToResponses(@NonNull List<UserDTO> dtos);
    
    /**
     * Updates a UserEntity with data from a UserDTO.
     * Only non-null properties in the DTO will be applied to the entity.
     *
     * @param dto the user DTO containing update data
     * @param entity the entity to update
     * @throws NullPointerException if either the DTO or entity is null
     */
    void updateEntityFromDto(@NonNull UserDTO dto, @NonNull @MappingTarget UserEntity entity);
}
