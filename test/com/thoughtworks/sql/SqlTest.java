package com.thoughtworks.sql;

import static com.thoughtworks.sql.Criterion.and;
import static com.thoughtworks.sql.Field.field;
import static com.thoughtworks.sql.Join.inner;
import static com.thoughtworks.sql.Join.left;
import static com.thoughtworks.sql.Join.out;
import static com.thoughtworks.sql.Join.right;
import static com.thoughtworks.sql.Order.asc;
import static com.thoughtworks.sql.Order.desc;
import static com.thoughtworks.sql.Sql.select;
import static com.thoughtworks.sql.Table.table;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class SqlTest {
    @Test
    public void should_assemble_simple_query_sql() {
        Field field = field("getdate()");
        Sql sql = select(field);
        assertThat(sql.toString(), equalTo("SELECT getdate() "));
        assertThat(select(field("isnull(null, 'true')")).toString(), equalTo("SELECT isnull(null, 'true') "));
    }

    @Test
    public void should_select_all_fields_if_no_given_field() {
        assertThat(select().toString(), equalTo("SELECT * "));
    }

    @Test
    public void should_append_select_fields_to_sql() {
        Field first = field("getdate()");
        Sql sql = select(first);
        Field second = field("getdate()").as("currentTime");
        sql.appendSelectFields(second);
        assertThat(sql, equalTo(select(first, second)));
        Field third = field("ss");
        Field forth = field("dd").as("f");
        sql.appendSelectFields(third, forth);
        assertThat(sql, equalTo(select(first, second, third, forth)));
    }

    @Test
    public void should_add_from_clause_to_sql() {
        Sql sql = select(field("s"));
        sql.from(table("d"));
        assertThat(sql.toString(), equalTo("SELECT s FROM d "));
    }

    @Test
    public void should_append_inner_join_clause_after_from_clause() {
        Sql sql = select(field("d")).from(table("table1"));
        sql.join(inner(table("table2"), table("table1").field("id").eq(table("table2").field("id"))));
        assertThat(sql.toString(), equalTo("SELECT d FROM table1 INNER JOIN table2 ON (table1.id=table2.id) "));
    }

    @Test
    public void should_prepend_join_clause_before_first_join_clause_existing() {
        Sql sql = select(field("d")).from(table("table1"))
                .join(inner(table("table2"), table("table1").field("id").eq(table("table2").field("id"))),
                        inner(table("table3"), table("table1").field("id").eq(table("table3").field("id"))));
        assertThat(sql.toString(), equalTo("SELECT d FROM table1 "
                + "INNER JOIN table2 ON (table1.id=table2.id) "
                + "INNER JOIN table3 ON (table1.id=table3.id) "));
    }

    @Test
    public void should_append_where_clause_after_sql() {
        Sql sql = select().from(table("table")).where(field("field").eq("?"));
        assertThat(sql.toString(), equalTo("SELECT * FROM table WHERE (field=?) "));
    }

    @Test
    public void should_allow_constraints_chain_in_where_clause() {
        Sql sql = select().from(table("table")).where(and(field("a").eq("'a'"), field("b").eq("'b'")));
        assertThat(sql.toString(), equalTo("SELECT * FROM table WHERE ((a='a') AND (b='b')) "));
    }

    @Test
    public void should_append_group_by_clause_after_where_clause() {
        Sql sql = select().from(table("table")).where(field("d").eq("'d'")).groupBy(field("d"));
        assertThat(sql.toString(), equalTo("SELECT * FROM table WHERE (d='d') GROUP BY d "));
    }

    @Test
    public void should_append_multiple_groupBies_after_from_clause() {
        Sql sql = select().from(table("table")).groupBy(field("d"), field("c"));
        assertThat(sql.toString(), equalTo("SELECT * FROM table GROUP BY d, c "));
    }

    @Test
    public void should_append_orderBy_fields_after_where_clause() {
        assertThat(select().from(table("table")).orderBy(asc(field("d"))).toString(),
                equalTo("SELECT * FROM table ORDER BY d ASC "));
        assertThat(select().from(table("table")).orderBy(desc(field("d"))).toString(),
                equalTo("SELECT * FROM table ORDER BY d DESC "));
    }

    @Test
    public void should_generate_full_sql() {
        Table t = table("table").as("t");
        Table t1 = table("table1").as("t1");
        Field tId = t.field("id");
        Field t1Id = t1.field("id");
        Field t1Time = t1.field("time");

        Sql sql = select(tId).from(t).join(inner(t1, tId.eq(t1Id)))
                .where(and(tId.eq("'a'"), t1Time.between("'1900'", "'2000'")))
                .groupBy(tId)
                .orderBy(asc(tId));
        assertThat(sql.toString(), equalTo("SELECT t.id "
                + "FROM table AS t "
                + "INNER JOIN table1 AS t1 ON (t.id=t1.id) "
                + "WHERE ((t.id='a') AND (t1.time BETWEEN '1900' AND '2000')) "
                + "GROUP BY t.id "
                + "ORDER BY t.id ASC "));
    }

    @Test
    public void should_append_joins_of_different_types() {
        Sql sql = select().from(table("table"))
                .join(left(table("table2"), table("table").field("id").eq(table("table2").field("id"))));
        assertThat(sql.toString(), equalTo("SELECT * FROM table LEFT JOIN table2 ON (table.id=table2.id) "));
        sql = select().from(table("table"))
                .join(right(table("table2"), table("table").field("id").eq(table("table2").field("id"))));
        assertThat(sql.toString(), equalTo("SELECT * FROM table RIGHT JOIN table2 ON (table.id=table2.id) "));
        sql = select().from(table("table"))
                .join(out(table("table2"), table("table").field("id").eq(table("table2").field("id"))));
        assertThat(sql.toString(), equalTo("SELECT * FROM table OUT JOIN table2 ON (table.id=table2.id) "));
    }

    @Test
    public void should_accept_sub_query_as_data_set() {
        Sql sql = select().from(select().from(table("table")).as("table2"));
        assertThat(sql.toString(), equalTo("SELECT * FROM (SELECT * FROM table ) AS table2 "));
    }

    @Test
    public void should_accept_sub_query_in_where_clause() {
        Sql sql = select().from(table("table")).where(field("d").in(field("d"), select().from(table("table2"))));
        assertThat(sql.toString(), equalTo("SELECT * FROM table WHERE (d IN (SELECT * FROM table2 )) "));
    }

}