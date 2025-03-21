package cz.ememsoft.policymanager.repository;

import cz.ememsoft.policymanager.model.Policy;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends ListCrudRepository<Policy, String> {
}