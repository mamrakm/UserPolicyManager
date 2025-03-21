package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.model.Policy;

import java.util.List;

public interface PolicyService {
    List<Policy> getAllPolicies();

    Policy getPolicyById(String id);

    Policy createPolicy(Policy policy);

    Policy updatePolicy(String id, Policy policy);

    void deletePolicy(String id);
}
