package com.swqsv.mybatisfreelog

import java.util.ArrayDeque
import java.util.regex.Pattern

object MyBatisLogParser {

    private val PREPARING: Pattern = Pattern.compile("==>\\s*Preparing:\\s*(.*)", Pattern.CASE_INSENSITIVE)
    private val PARAMETERS: Pattern = Pattern.compile("==>\\s*Parameters:\\s*(.*)", Pattern.CASE_INSENSITIVE)
    private val TOTAL: Pattern = Pattern.compile(
        // 例：<==      Total: 0
        "<==\\s*Total:\\s*(-?\\d+)",
        Pattern.CASE_INSENSITIVE,
    )
    private val NULL_WITH_TYPE: Pattern = Pattern.compile("^null\\([A-Za-z0-9_.$]+\\)\$")

    /** 提取 `2026-04-25 14:39:04.347 DEBUG c.s.m.m.T.querySomeThings`（` - ==>` / ` - <==` 之前）。 */
    private val LOG_PREFIX: Pattern = Pattern.compile("""^(.+?)\s+-\s+(?:==>|<==)""")

    fun parseLogPrefix(line: String): String? {
        val t = line.trim { it <= ' ' }
        val m = LOG_PREFIX.matcher(t)
        if (!m.find()) return null
        return m.group(1)?.trim { it <= ' ' }?.takeIf { it.isNotEmpty() }
    }

    fun parsePreparingLine(line: String): String? {
        val t = line.trim { it <= ' ' }
        if (!t.contains("==>") || !t.contains("Preparing", ignoreCase = true)) return null
        val m = PREPARING.matcher(t)
        if (!m.find()) return null
        val sql = m.group(1)?.trim { it <= ' ' }?.trimEnd { it <= ' ' } ?: return null
        if (sql.isEmpty()) return null
        return sql
    }

    /**
     * 从 `<==      Total: 0` 等行中解析出结果行数/影响行数（SELECT 为查询行数，UPDATE/INSERT 为影响行数，依 MyBatis 版本而定）。
     */
    fun parseTotalLine(line: String): String? {
        val t = line.trim { it <= ' ' }
        if (!t.contains("<==") || !t.contains("Total", ignoreCase = true)) return null
        val m = TOTAL.matcher(t)
        if (!m.find()) return null
        return m.group(1)
    }

    fun parseParametersLine(line: String): List<ParsedParam>? {
        val t = line.trim { it <= ' ' }
        if (!t.contains("==>") || !t.contains("Parameters", ignoreCase = true)) return null
        val m = PARAMETERS.matcher(t)
        if (!m.find()) return null
        val paramPart = m.group(1) ?: return emptyList()
        val parts = splitTopLevelCommas(paramPart)
        if (parts.isEmpty()) return emptyList()
        return parts.map { parseParamToken(it) }
    }

    /**
     * Splits the Parameters payload by top-level commas (depth of parentheses = 0).
     */
    internal fun splitTopLevelCommas(s: String): List<String> {
        val t = s.trim { it <= ' ' }
        if (t.isEmpty()) return emptyList()
        val out = ArrayList<String>()
        val stack = ArrayDeque<Char>() // we only track paren depth
        var depth = 0
        var start = 0
        for (i in t.indices) {
            when (t[i]) {
                '(' -> depth++
                ')' -> depth = (depth - 1).coerceAtLeast(0)
            }
            if (t[i] == ',' && depth == 0) {
                out.add(t.substring(start, i).trim { it <= ' ' })
                start = i + 1
            }
        }
        out.add(t.substring(start).trim { it <= ' ' })
        return out.filter { it.isNotEmpty() }
    }

    private fun parseParamToken(token: String): ParsedParam {
        val t = token.trim { it <= ' ' }
        if (t == "null" || NULL_WITH_TYPE.matcher(t).matches()) {
            return ParsedParam.Null
        }
        val lastOpen = t.lastIndexOf('(')
        val lastClose = t.lastIndexOf(')')
        if (lastOpen <= 0 || lastClose <= lastOpen) {
            return ParsedParam.QuotedString(t) // best-effort
        }
        val raw = t.substring(0, lastOpen)
        val type = t.substring(lastOpen + 1, lastClose).trim { it <= ' ' }
        return when {
            isStringType(type) -> ParsedParam.QuotedString(raw)
            isBooleanType(type) -> {
                when (raw.lowercase()) {
                    "true" -> ParsedParam.BooleanLiteral(true)
                    "false" -> ParsedParam.BooleanLiteral(false)
                    else -> ParsedParam.QuotedString(raw)
                }
            }
            isDateLikeType(type) -> ParsedParam.QuotedString(raw) // 2026-01-01(Date) as '2026-01-01'
            isNumberType(type) -> ParsedParam.NumberRaw(raw) // 1(Integer) -> 1
            else -> ParsedParam.QuotedString(raw)
        }
    }

    private fun isStringType(type: String) =
        type == "String" || type == "CharSequence" || type == "Clob" || type == "NClob" || type == "NString"

    private fun isBooleanType(type: String) =
        type == "Boolean" || type == "boolean" || type == "bool"

    private fun isDateLikeType(type: String) =
        type == "Date" || type == "Time" || type == "Timestamp" ||
            type == "LocalDate" || type == "LocalTime" || type == "LocalDateTime" || type == "Instant" ||
            type.startsWith("java.sql") || type.startsWith("java.time") || type.contains("Date", ignoreCase = true)

    private fun isNumberType(type: String) =
        type in setOf("Long", "Integer", "Int", "Short", "Byte", "Double", "Float", "BigDecimal", "BigInteger", "Number")
}

sealed class ParsedParam {
    data object Null : ParsedParam()
    data class BooleanLiteral(val v: Boolean) : ParsedParam()
    /** Raw numeric text as in MyBatis log, without quotes. */
    data class NumberRaw(val s: String) : ParsedParam()
    /** String / date-like value, will be single-quoted and escaped. */
    data class QuotedString(val s: String) : ParsedParam()
}
