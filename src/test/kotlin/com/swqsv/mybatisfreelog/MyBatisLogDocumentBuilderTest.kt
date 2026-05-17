package com.swqsv.mybatisfreelog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MyBatisLogDocumentBuilderTest {

    @Test
    fun `total appears only once at end of block`() {
        val doc = MyBatisLogDocumentBuilder.build(
            listOf(
                MyBatisLogEntry(
                    total = "0",
                    sql = "SELECT 1 FROM t WHERE id = 'a';",
                    logPrefix = "2026-04-25 14:39:04.347 DEBUG c.s.m.m.T.querySomeThings",
                ),
            ),
        )
        assertTrue(doc.contains("c.s.m.m.T.querySomeThings - ==>"))
        assertTrue(doc.contains("-- <==      Total: 0"))
        assertFalse(doc.contains("Total: 0\nSELECT")) // 头部不应带 Total
        val totalCount = doc.split("Total: 0").size - 1
        assertEquals(1, totalCount)
    }
}
