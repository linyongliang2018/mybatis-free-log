package com.swqsv.mybatisfreelog

import com.intellij.execution.filters.Filter
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * 捕获 Preparing + Parameters + `<== Total:`，并保留 Mapper/Logger 前缀以标识查询来源。
 */
class MyBatisConsoleFilter(
    private val project: Project,
) : Filter {

    private var pendingPreparing: String? = null
    private var pendingLogPrefix: String? = null
    private var pendingRestoredSql: String? = null

    override fun applyFilter(line: String, entireTextLength: Int): Filter.Result? {
        return try {
            onLine(line)
            null
        } catch (ignored: Throwable) {
            null
        }
    }

    private fun flushPendingWithoutTotal() {
        val sql = pendingRestoredSql ?: return
        val prefix = pendingLogPrefix
        pendingRestoredSql = null
        pendingLogPrefix = null
        project.service<MyBatisLogService>().appendLogEntry(MyBatisLogEntry("—", sql, prefix))
    }

    private fun onLine(line: String) {
        val total = MyBatisLogParser.parseTotalLine(line)
        if (total != null) {
            val sql = pendingRestoredSql
            if (sql != null) {
                val prefix = pendingLogPrefix ?: MyBatisLogParser.parseLogPrefix(line)
                pendingRestoredSql = null
                pendingLogPrefix = null
                project.service<MyBatisLogService>().appendLogEntry(MyBatisLogEntry(total, sql, prefix))
            }
            return
        }

        val preparing = MyBatisLogParser.parsePreparingLine(line)
        if (preparing != null) {
            if (pendingRestoredSql != null) {
                flushPendingWithoutTotal()
            }
            pendingPreparing = preparing
            pendingLogPrefix = MyBatisLogParser.parseLogPrefix(line) ?: pendingLogPrefix
            return
        }

        if (line.contains("Parameters", ignoreCase = true) && line.contains("==>")) {
            val template = pendingPreparing ?: return
            val params = MyBatisLogParser.parseParametersLine(line) ?: return
            val restored = SqlRestoreService.replacePlaceholders(template, params)
            pendingPreparing = null
            pendingRestoredSql = restored
            MyBatisLogParser.parseLogPrefix(line)?.let { pendingLogPrefix = it }
        }
    }
}
