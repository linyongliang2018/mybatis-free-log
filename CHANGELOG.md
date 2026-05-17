# Changelog

本文件记录 notable 变更，格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)。

## [1.0.0] - 2026-05-17

### 新增

- 控制台 `ConsoleFilter` 捕获 MyBatis `Preparing` / `Parameters` / `Total` 连续日志
- SQL 参数还原（`?` → 字面量，含字符串转义）
- 工具窗口只读 SQL 编辑器：框选复制、软换行、语法高亮
- MyBatis Log 风格文本块：分隔线、序号与 Mapper/Logger 来源、` - ==>` 头、末尾 `-- <==      Total: n`
- 轻量 SQL 格式化（子句与 SELECT 列表换行）
- 应用级设置：最多保留最近 SQL 条数（默认 1000），超出自动丢弃最旧记录
- 工具栏：清空、复制选中/全文、设置

[1.0.0]: https://github.com/linyongliang2018/mybatis-free-log/releases/tag/v1.0.0
