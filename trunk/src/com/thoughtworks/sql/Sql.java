package com.thoughtworks.sql;

import static com.thoughtworks.sql.Constants.*;
import static com.thoughtworks.sql.Table.table;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;

public class Sql {

    private Table table;
    private List<Criterion> criterions = new ArrayList<Criterion>();
    private List<Field> fields = new ArrayList<Field>();
    private List<Join> joins = new ArrayList<Join>();
    private List<Field> groupBies = new ArrayList<Field>();
    private List<Order> orders = new ArrayList<Order>();

    private Sql(Field... fields) {
        this.fields.addAll(asList(fields));
    }

    public static Sql select(Field... fields) {
        return new Sql(fields);
    }

    public Sql from(Table table) {
        this.table = table;
        return this;
    }

    public Sql join(Join... join) {
        joins.addAll(asList(join));
        return this;
    }

    public Sql where(Criterion criterion) {
        criterions.add(criterion);
        return this;
    }

    public Sql groupBy(Field... groupBy) {
        groupBies.addAll(asList(groupBy));
        return this;
    }

    public Sql orderBy(Order... order) {
        orders.addAll(asList(order));
        return this;
    }

    public Sql appendSelectFields(Field... fields) {
        this.fields.addAll(asList(fields));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder();
        visitSelectClause(sql);
        visitFromClause(sql);
        visitJoinClause(sql);
        visitWhereClause(sql);
        visitGroupByClause(sql);
        visitOrderByClause(sql);
        return sql.toString();
    }

    private void visitOrderByClause(StringBuilder sql) {
        if (orders.isEmpty()) {
            return;
        }
        sql.append(ORDER_BY);
        for (Order order : orders) {
            sql.append(SPACE).append(order).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    private void visitGroupByClause(StringBuilder sql) {
        if (groupBies.isEmpty()) {
            return;
        }
        sql.append(GROUP_BY);
        for (Field groupBy : groupBies) {
            sql.append(SPACE).append(groupBy).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    private void visitWhereClause(StringBuilder sql) {
        if (criterions.isEmpty()) {
            return;
        }
        sql.append(WHERE);
        for (Criterion criterion : criterions) {
            sql.append(SPACE).append(criterion).append(SPACE);
        }
    }

    private void visitJoinClause(StringBuilder sql) {
        for (Join join : joins) {
            sql.append(join).append(SPACE);
        }
    }

    private void visitFromClause(StringBuilder sql) {
        if (table == null) {
            return;
        }
        sql.append(FROM).append(SPACE).append(table).append(SPACE);
    }

    private void visitSelectClause(StringBuilder sql) {
        sql.append(SELECT).append(SPACE);
        if (fields.isEmpty()) {
            sql.append(ALL).append(SPACE);
            return;
        }
        for (Field field : fields) {
            sql.append(field).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    public Table as(String alias) {
        return table(LEFT_PARENTHESIS + this.toString() + RIGHT_PARENTHESIS).as(alias);
    }
}