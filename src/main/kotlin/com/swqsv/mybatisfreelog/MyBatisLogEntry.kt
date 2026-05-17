package com.swqsv.mybatisfreelog

/**
 * 一条可展示的日志。
 * [logPrefix] 为控制台行中 ` - ==>` 之前的片段（时间、级别、Mapper/Logger），用于标识查询来源。
 * [total] 为结果行数；无 Total 行时为 "—"。
 */
data class MyBatisLogEntry(
    val total: String,
    val sql: String,
    val logPrefix: String? = null,
)
