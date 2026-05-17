package com.swqsv.mybatisfreelog

object SqlRestoreService {

    fun replacePlaceholders(sqlWithQuestionMarks: String, params: List<ParsedParam>): String {
        var rest = sqlWithQuestionMarks
        for (p in params) {
            val idx = rest.indexOf('?')
            if (idx < 0) break
            val lit = p.toSqlLiteral()
            rest = rest.substring(0, idx) + lit + rest.substring(idx + 1)
        }
        var out = rest.trim { it <= ' ' }
        if (out.isNotEmpty() && !out.endsWith(';')) {
            out = "$out;"
        }
        return out
    }

    private fun ParsedParam.toSqlLiteral(): String = when (this) {
        ParsedParam.Null -> "null"
        is ParsedParam.BooleanLiteral -> if (v) "true" else "false"
        is ParsedParam.NumberRaw -> s.trim { it <= ' ' }
        is ParsedParam.QuotedString -> escapeAndQuoteString(s)
    }

    private fun escapeAndQuoteString(value: String): String {
        val v = value.replace("'", "''")
        return "'$v'"
    }
}
