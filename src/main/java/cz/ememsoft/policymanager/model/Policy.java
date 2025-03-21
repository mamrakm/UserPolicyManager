package cz.ememsoft.policymanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.ememsoft.policymanager.util.PolicyDeserializer;

import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = PolicyDeserializer.class)
public record Policy(
        String id,
        String name,

        @JsonProperty("youngerThan")
        YoungerThanRule youngerThanRule,

        @JsonProperty("emailDomainIs")
        EmailDomainIsRule emailDomainIsRule,

        @JsonProperty("isMemberOf")
        IsMemberOfRule isMemberOfRule
) {
    public Policy {
        Objects.requireNonNull(id, "Policy ID cannot be null");
        Objects.requireNonNull(name, "Policy name cannot be null");

        if (youngerThanRule == null && emailDomainIsRule == null && isMemberOfRule == null) {
            throw new IllegalArgumentException("Policy must have at least one rule");
        }
    }

    public Optional<PolicyRule> getRule() {
        if (youngerThanRule != null) return Optional.of(youngerThanRule);
        if (emailDomainIsRule != null) return Optional.of(emailDomainIsRule);
        if (isMemberOfRule != null) return Optional.of(isMemberOfRule);
        return Optional.empty();
    }

    public boolean appliesTo(User user) {
        return getRule()
                .map(rule -> rule.applies(user))
                .orElse(false);
    }
}