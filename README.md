# MyBatis Free Log

IntelliJ IDEA 插件：从 Run/Debug 控制台解析 MyBatis / MyBatis-Plus 的 `Preparing`、`Parameters`、`Total` 日志，还原为可执行 SQL，并在工具窗口中按 MyBatis Log 风格展示。

## 功能

- 监听控制台 `==> Preparing` / `==> Parameters` / `<== Total`
- 将 `?` 占位符替换为实际参数（字符串、数字、null、布尔、日期等）
- 底部工具窗口 **MyBatis Free Log**：格式化换行、可框选复制、显示 Mapper/Logger 来源
- 每条 SQL 末尾一行 `-- <==      Total: n`
- **设置**：最多保留最近 N 条 SQL（默认 1000）

## 环境要求

- JDK 17+
- IntelliJ IDEA 2025.2+（`sinceBuild=252`）
- Gradle 9.x（使用项目自带 `gradlew`）

## 构建与运行

```bash
# Windows
.\gradlew.bat buildPlugin

# 在 IDE 中：Gradle → runIde
```

插件包输出：`build/distributions/mybatis-free-log-*.zip`

## 项目结构

```
src/main/kotlin/com/swqsv/mybatisfreelog/
  MyBatisConsoleFilterProvider / MyBatisConsoleFilter   # 控制台捕获
  MyBatisLogParser / SqlRestoreService                  # 解析与还原
  MyBatisLogService / MyBatisLogDocumentBuilder         # 存储与展示文本
  MyBatisToolWindowFactory / MyBatisToolWindowPanel     # 工具窗口
  MyBatisLogSettings                                    # 条数上限配置
```

版本变更见 [CHANGELOG.md](CHANGELOG.md)。

## 许可证

见仓库许可证文件（若未单独声明，以作者约定为准）。
