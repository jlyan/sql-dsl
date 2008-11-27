package com.thoughtworks.sql;

import static com.thoughtworks.sql.Constants.AND;
import static com.thoughtworks.sql.Constants.BETWEEN;
import static com.thoughtworks.sql.Constants.COMMA;
import static com.thoughtworks.sql.Constants.EQUAL_OPERATOR;
import static com.thoughtworks.sql.Constants.GREATER_OPERATOR;
import static com.thoughtworks.sql.Constants.IN;
import static com.thoughtworks.sql.Constants.IS_NOT_NULL;
import static com.thoughtworks.sql.Constants.IS_NULL;
import static com.thoughtworks.sql.Constants.LEFT_PARENTHESIS;
import static com.thoughtworks.sql.Constants.LESS_OPERATOR;
import static com.thoughtworks.sql.Constants.LIKE;
import static com.thoughtworks.sql.Constants.NOT_EQUAL_OPERATOR;
import static com.thoughtworks.sql.Constants.RIGHT_PARENTHESIS;
import static com.thoughtworks.sql.Constants.SPACE;

public class Field extends DBObject<Field> {

    protected Field(String expression) {
        super(expression);
    }

    public static Field field(String expression) {
        return new Field(expression);
    }

    public Criterion eq(final Object value) {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(EQUAL_OPERATOR).append(value);
            }
        };
    }

    public Criterion neq(final Object value) {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(NOT_EQUAL_OPERATOR).append(value);
            }
        };
    }

    public Criterion gt(final Object value) {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(GREATER_OPERATOR).append(value);
            }
        };
    }

    public Criterion lt(final Object value) {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(LESS_OPERATOR).append(value);
            }
        };
    }

    public Criterion isNull() {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(IS_NULL);
            }
        };
    }

    public Criterion isNotNull() {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(IS_NOT_NULL);
            }
        };
    }

    public Criterion between(final Object lower, final Object upper) {
        final Field field = this;
        return new Criterion(this) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(BETWEEN).append(SPACE).append(lower).append(SPACE).append(AND)
                        .append(SPACE).append(upper);
            }
        };
    }

    public Criterion like(final String value) {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(LIKE).append(SPACE).append(value);
            }
        };
    }

    public <T> Criterion in(final T... value) {
        final Field field = this;
        return new Criterion(field) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(IN).append(SPACE).append(LEFT_PARENTHESIS);
                for (T t : value) {
                    sb.append(t.toString()).append(COMMA);
                }
                sb.deleteCharAt(sb.length() - 1).append(RIGHT_PARENTHESIS);
            }
        };
    }

    public Criterion in(final Field expression, final Sql sql) {
        final Field field = this;
        return new Criterion(this) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(IN).append(SPACE).append(LEFT_PARENTHESIS).append(sql)
                        .append(RIGHT_PARENTHESIS);
            }
        };
    }
}
