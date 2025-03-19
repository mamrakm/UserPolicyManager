package cz.evolveum.policymanager.model;

public record IsMemberOfRule(String value) implements PolicyRule {
    @Override
    public boolean applies(User user) {
        return user.organizationUnit() != null &&
                user.organizationUnit().contains(value);
    }

    @Override
    public String getType() {
        return "isMemberOf";
    }

    @Override
    public Object getValue() {
        return value;
    }
}