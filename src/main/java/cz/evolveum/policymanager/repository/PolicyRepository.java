package cz.evolveum.policymanager.repository;

import cz.evolveum.policymanager.model.Policy;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends ListCrudRepository<Policy, String> {
}