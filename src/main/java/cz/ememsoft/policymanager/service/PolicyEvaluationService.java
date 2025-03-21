package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.model.User;

public interface PolicyEvaluationService {
    User evaluatePolicies(User user);

    void recomputeAllUsers();

    void initialize();
}