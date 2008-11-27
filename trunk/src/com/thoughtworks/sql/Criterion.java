package com.thoughtworks.sql;

import static com.thoughtworks.sql.Constants.AND;
import static com.thoughtworks.sql.Constants.LEFT_PARENTHESIS;
import static com.thoughtworks.sql.Constants.RIGHT_PARENTHESIS;
import static com.thoughtworks.sql.Constants.SPACE;
import static com.thoughtworks.sql.Constants.OR;

public abstract class Criterion {
    private Field expression;

    private Criterion() {
    }

    Criterion(Field expression) {
        this.expression = expression;
    }

    public static Criterion and(final Criterion criterion, final Criterion... criterions) {
        return new Criterion() {

            protected void populate(StringBuilder sb) {
                sb.append(criterion);
                for (Criterion criterion : criterions) {
                    sb.append(SPACE).append(AND).append(SPACE).append(criterion);
                }
            }
        };
    }

    public static Criterion or(final Criterion criterion, final Criterion... criterions) {
        return new Criterion() {

            protected void populate(StringBuilder sb) {
                sb.append(criterion);
                for (Criterion criterion : criterions) {
                    sb.append(SPACE).append(OR).append(SPACE).append(criterion.toString());
                }
            }
        };
    }

    protected abstract void populate(StringBuilder sb);

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(LEFT_PARENTHESIS);
        populate(builder);
        builder.append(RIGHT_PARENTHESIS);
        return builder.toString();
    }
}
