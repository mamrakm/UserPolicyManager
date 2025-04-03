package cz.ememsoft.policymanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "policies")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Policy(
        @Id
        String id,

        String name,

        @JsonProperty("youngerThan")
        @Convert(converter = YoungerThanRuleConverter.class)
        YoungerThanRule youngerThanRule,

        @JsonProperty("emailDomainIs")
        @Convert(converter = EmailDomainIsRuleConverter.class)
        EmailDomainIsRule emailDomainIsRule,

        @JsonProperty("isMemberOf")
        @Convert(converter = IsMemberOfRuleConverter.class)
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

@Converter
class YoungerThanRuleConverter implements AttributeConverter<YoungerThanRule, Integer> {
    @Override
    public Integer convertToDatabaseColumn(YoungerThanRule attribute) {
        return attribute != null ? attribute.value() : null;
    }

    @Override
    public YoungerThanRule convertToEntityAttribute(Integer dbData) {
        return dbData != null ? new YoungerThanRule(dbData) : null;
    }
}

@Converter
class EmailDomainIsRuleConverter implements AttributeConverter<EmailDomainIsRule, String> {
    @Override
    public String convertToDatabaseColumn(EmailDomainIsRule attribute) {
        return attribute != null ? attribute.value() : null;
    }

    @Override
    public EmailDomainIsRule convertToEntityAttribute(String dbData) {
        return dbData != null ? new EmailDomainIsRule(dbData) : null;
    }
}

@Converter
class IsMemberOfRuleConverter implements AttributeConverter<IsMemberOfRule, String> {
    @Override
    public String convertToDatabaseColumn(IsMemberOfRule attribute) {
        return attribute != null ? attribute.value() : null;
    }

    @Override
    public IsMemberOfRule convertToEntityAttribute(String dbData) {
        return dbData != null ? new IsMemberOfRule(dbData) : null;
    }
}