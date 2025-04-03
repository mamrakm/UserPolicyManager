package cz.ememsoft.policymanager.repository;

import cz.ememsoft.policymanager.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface for {@link UserEntity} objects.
 * Provides methods to perform CRUD operations and custom queries on user entities.
 * <p>
 * This repository extends JpaRepository, which provides built-in methods for common
 * operations like findAll(), findById(), save(), delete(), etc.
 * </p>
 *
 * @author EMeMSoft spol. s r.o.
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    // Standard JPA methods are inherited from JpaRepository
    // Custom query methods can be added here if needed
}
