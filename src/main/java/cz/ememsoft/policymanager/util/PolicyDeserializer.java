package cz.ememsoft.policymanager.util;

import cz.ememsoft.policymanager.model.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class PolicyDeserializer extends StdDeserializer<Policy> {

    public PolicyDeserializer() {
        this(null);
    }

    public PolicyDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Policy deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String id = node.get("id").asText();
        String name = node.get("name").asText();

        YoungerThanRule youngerThanRule = null;
        EmailDomainIsRule emailDomainIsRule = null;
        IsMemberOfRule isMemberOfRule = null;

        if (node.has("youngerThan")) {
            JsonNode youngerThan = node.get("youngerThan");
            if (youngerThan.has("value")) {
                youngerThanRule = new YoungerThanRule(youngerThan.get("value").asInt());
            }
        }

        if (node.has("emailDomainIs")) {
            JsonNode emailDomain = node.get("emailDomainIs");
            if (emailDomain.has("value")) {
                emailDomainIsRule = new EmailDomainIsRule(emailDomain.get("value").asText());
            }
        }

        if (node.has("isMemberOf")) {
            JsonNode isMemberOf = node.get("isMemberOf");
            if (isMemberOf.has("value")) {
                isMemberOfRule = new IsMemberOfRule(isMemberOf.get("value").asText());
            }
        }

        return new Policy(id, name, youngerThanRule, emailDomainIsRule, isMemberOfRule);
    }
}
