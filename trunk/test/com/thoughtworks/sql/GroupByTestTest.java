package com.thoughtworks.sql;

import org.junit.Test;
import static com.thoughtworks.sql.Field.field;

public class GroupByTestTest {
    @Test
    public void should_generate_groupby_clause() {
        GroupBy groupBy = GroupBy.groupBy(field("ss"));
    }
}