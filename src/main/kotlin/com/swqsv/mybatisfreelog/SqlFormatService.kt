package com.swqsv.mybatisfreelog

/**
 * 轻量 SQL 排版：在主要子句与 SELECT 列表逗号处换行，便于阅读与框选。
 */
object SqlFormatService {

    private val WHITESPACE = Regex("\\s+")

    /** 较长关键字优先匹配，避免 FROM 误伤 LEFT JOIN 等。 */
    private val CLAUSE_KEYWORDS = listOf(
        "LEFT OUTER JOIN",
        "RIGHT OUTER JOIN",
        "FULL OUTER JOIN",
        "INNER JOIN",
        "LEFT JOIN",
        "RIGHT JOIN",
        "CROSS JOIN",
        "OUTER JOIN",
        "INSERT INTO",
        "DELETE FROM",
        "ORDER BY",
        "GROUP BY",
        "JOIN",
        "FROM",
        "WHERE",
        "HAVING",
        "SET",
        "VALUES",
        "UPDATE",
        "INTO",
    )

    fun format(sql: String): String {
        val hadSemicolon = sql.trimEnd().endsWith(';')
        var s = sql.trim().trimEnd(';').trim()
        if (s.isEmpty()) return if (hadSemicolon) ";" else ""
        s = s.replace(WHITESPACE, " ")

        for (keyword in CLAUSE_KEYWORDS) {
            val pattern = Regex("(?i)\\s+${Regex.escape(keyword)}\\s+")
            s = s.replace(pattern) { "\n$keyword " }
        }

        s = breakTopLevelCommas(s)

        val lines = s.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { index, line ->
                if (index == 0) line else "    $line"
            }

        val body = lines.joinToString("\n")
        return if (hadSemicolon) "$body;" else body
    }

    /**
     * 在顶层逗号后换行（用于 SELECT 字段列表等），不进入括号内部。
     */
    internal fun breakTopLevelCommas(sql: String): String {
        val out = StringBuilder()
        var depth = 0
        var i = 0
        while (i < sql.length) {
            val c = sql[i]
            when (c) {
                '(' -> depth++
                ')' -> depth = (depth - 1).coerceAtLeast(0)
                ',' -> if (depth == 0) {
                    out.append(',')
                    out.append('\n')
                    i++
                    while (i < sql.length && sql[i] == ' ') i++
                    continue
                }
            }
            out.append(c)
            i++
        }
        return out.toString()
    }
}
