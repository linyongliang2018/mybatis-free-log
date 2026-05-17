package com.swqsv.mybatisfreelog

import org.junit.Assert.assertTrue
import org.junit.Test

class SqlFormatServiceTest {

    @Test
    fun `breaks major clauses and select list`() {
        val sql = "SELECT apply_id, apply_id_en, project_id FROM TEST_RECORD WHERE id = 'A005';"
        val out = SqlFormatService.format(sql)
        assertTrue(out.contains("FROM"))
        assertTrue(out.contains("WHERE"))
        assertTrue(out.lines().size >= 3)
    }
}
