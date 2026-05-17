package com.swqsv.mybatisfreelog

/**
 * 拼成与 MyBatis Log 插件一致的文本块：分隔线、带序号与来源的注释头、格式化 SQL、末尾一行 Total。
 */
object MyBatisLogDocumentBuilder {

    private const val SEPARATOR = "-----------------------"

    fun build(entries: List<MyBatisLogEntry>): String {
        if (entries.isEmpty()) return ""
        return buildString {
            entries.forEachIndexed { index, entry ->
                appendLine(SEPARATOR)
                appendLine(buildHeader(index + 1, entry.logPrefix))
                appendLine(SqlFormatService.format(entry.sql))
                appendLine(buildTotalFooter(entry.total))
            }
        }.trimEnd() + "\n"
    }

    /** 例：`-- 1 2026-04-25 14:39:04.347 DEBUG c.s.m.m.T.querySomeThings - ==>` */
    private fun buildHeader(sequence: Int, logPrefix: String?): String {
        val source = logPrefix?.trim().orEmpty()
        return if (source.isEmpty()) {
            "-- $sequence - ==>"
        } else {
            "-- $sequence $source - ==>"
        }
    }

    /** 例：`-- <==      Total: 0`（Total 仅出现在块末尾一次） */
    private fun buildTotalFooter(total: String): String = "-- <==      Total: $total"
}
