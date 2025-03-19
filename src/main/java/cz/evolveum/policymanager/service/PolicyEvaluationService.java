package cz.evolveum.policymanager.service;

import cz.evolveum.policymanager.model.User;

public interface PolicyEvaluationService {
    User evaluatePolicies(User user);

    void recomputeAllUsers();

    void initialize();
}