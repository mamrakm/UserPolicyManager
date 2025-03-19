package cz.evolveum.policymanager.model;

public sealed interface PolicyRule permits YoungerThanRule, EmailDomainIsRule, IsMemberOfRule {
    boolean applies(User user);
    String getType();
    Object getValue();
}
