package com.swqsv.mybatisfreelog

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 按顺序保留已解析条目（受 [MyBatisLogSettings.maxEntries] 限制），并通知 ToolWindow。
 */
@Service(Service.Level.PROJECT)
class MyBatisLogService(
    @Suppress("unused") private val project: Project,
) {
    private val entries = CopyOnWriteArrayList<MyBatisLogEntry>()

    private val appendListeners = CopyOnWriteArrayList<(MyBatisLogEntry) -> Unit>()
    private val refreshListeners = CopyOnWriteArrayList<() -> Unit>()
    private val clearListeners = CopyOnWriteArrayList<() -> Unit>()

    fun getAllEntries(): List<MyBatisLogEntry> = entries.toList()

    fun addAppendListener(l: (MyBatisLogEntry) -> Unit) {
        appendListeners.add(l)
    }

    fun removeAppendListener(l: (MyBatisLogEntry) -> Unit) {
        appendListeners.remove(l)
    }

    fun addRefreshListener(l: () -> Unit) {
        refreshListeners.add(l)
    }

    fun removeRefreshListener(l: () -> Unit) {
        refreshListeners.remove(l)
    }

    fun addClearListener(l: () -> Unit) {
        clearListeners.add(l)
    }

    fun removeClearListener(l: () -> Unit) {
        clearListeners.remove(l)
    }

    fun appendLogEntry(entry: MyBatisLogEntry) {
        val t = entry.sql.trim { it <= ' ' }
        if (t.isEmpty()) return
        val normalized = MyBatisLogEntry(entry.total, t, entry.logPrefix)
        entries.add(normalized)
        val trimmed = trimExcess()
        val copy = normalized
        ApplicationManager.getApplication().invokeLater {
            if (trimmed) {
                for (c in refreshListeners) {
                    c.invoke()
                }
            } else {
                for (c in appendListeners) {
                    c.invoke(copy)
                }
            }
        }
    }

    /** 修改配置后裁剪历史并刷新 ToolWindow 全文。 */
    fun applyMaxEntriesLimit() {
        trimExcess()
        ApplicationManager.getApplication().invokeLater {
            for (c in refreshListeners) {
                c.invoke()
            }
        }
    }

    fun clear() {
        entries.clear()
        ApplicationManager.getApplication().invokeLater {
            for (c in clearListeners) {
                c.invoke()
            }
        }
    }

    private fun trimExcess(): Boolean {
        val max = MyBatisLogSettings.getInstance().maxEntries
        var removed = false
        while (entries.size > max) {
            entries.removeAt(0)
            removed = true
        }
        return removed
    }
}
