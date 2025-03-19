package cz.evolveum.policymanager.model;

public record EmailDomainIsRule(String value) implements PolicyRule {
    @Override
    public boolean applies(User user) {
        return user.emailAddress() != null &&
                user.emailAddress().endsWith("@" + value);
    }

    @Override
    public String getType() {
        return "emailDomainIs";
    }

    @Override
    public Object getValue() {
        return value;
    }
}