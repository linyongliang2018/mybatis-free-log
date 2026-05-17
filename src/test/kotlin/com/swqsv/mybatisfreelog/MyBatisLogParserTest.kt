package com.swqsv.mybatisfreelog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MyBatisLogParserTest {

    @Test
    fun `parse preparing and parameters example`() {
        val p = "==>  Preparing: select * from user where id = ? and name = ?"
        val a = "==> Parameters: 1(Long), Tom(String)"
        val sql = MyBatisLogParser.parsePreparingLine(p)!!
        val pr = MyBatisLogParser.parseParametersLine(a)!!
        val out = SqlRestoreService.replacePlaceholders(sql, pr)
        assertEquals("select * from user where id = 1 and name = 'Tom';", out)
    }

    @Test
    fun `null and boolean`() {
        val p = "==>  Preparing: update t set a = ? where b = ? and c = ?"
        val a = "==> Parameters: null, true(Boolean), 2026-01-01(Date)"
        val sql = MyBatisLogParser.parsePreparingLine(p)!!
        val pr = MyBatisLogParser.parseParametersLine(a)!!
        val out = SqlRestoreService.replacePlaceholders(sql, pr)
        assertEquals("update t set a = null where b = true and c = '2026-01-01';", out)
    }

    @Test
    fun `parse total line from real log`() {
        val line =
            "2026-04-25 14:39:04.348 DEBUG c.s.m.m.T.querySomeThings - <==      Total: 0"
        assertEquals("0", MyBatisLogParser.parseTotalLine(line))
    }

    @Test
    fun `parse log prefix with mapper logger`() {
        val line =
            "2026-04-25 14:39:04.347 DEBUG c.s.m.m.T.querySomeThings - ==>  Preparing: SELECT 1"
        assertEquals(
            "2026-04-25 14:39:04.347 DEBUG c.s.m.m.T.querySomeThings",
            MyBatisLogParser.parseLogPrefix(line),
        )
    }
}
