package cz.ememsoft.policymanager.repository;

import cz.ememsoft.policymanager.model.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ListCrudRepository<User, String> {
}