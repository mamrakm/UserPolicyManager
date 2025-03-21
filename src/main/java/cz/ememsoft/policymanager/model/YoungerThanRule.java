package cz.ememsoft.policymanager.model;

import java.time.LocalDate;
import java.time.Period;

public record YoungerThanRule(int value) implements PolicyRule {
    @Override
    public boolean applies(User user) {
        return user.birthDate() != null &&
                Period.between(user.birthDate(), LocalDate.now()).getYears() < value;
    }

    @Override
    public String getType() {
        return "youngerThan";
    }

    @Override
    public Object getValue() {
        return value;
    }
}