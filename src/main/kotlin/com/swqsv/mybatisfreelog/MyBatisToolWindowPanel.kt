package com.swqsv.mybatisfreelog

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.datatransfer.StringSelection
import javax.swing.JButton
import javax.swing.JPanel

/**
 * 只读 SQL 编辑器视图：支持框选、语法高亮、格式化换行；布局参考 MyBatis Log 文本块样式。
 */
class MyBatisToolWindowPanel(
    private val project: Project,
) : JPanel(BorderLayout()) {

    private val service: MyBatisLogService = project.service()

    private val logDocument = EditorFactory.getInstance().createDocument("").also {
        it.setReadOnly(true)
    }

    private val editorField: EditorTextField = object : EditorTextField(
        logDocument,
        project,
        sqlFileType(),
    ) {
        init {
            setOneLineMode(false)
            addSettingsProvider { editor ->
                editor.settings.isUseSoftWraps = true
            }
        }
    }

    private val onAppend: (MyBatisLogEntry) -> Unit = { refreshDocument(scrollToEnd = true) }
    private val onRefresh: () -> Unit = { refreshDocument(scrollToEnd = true) }
    private val onClear: () -> Unit = { setEditorText("") }

    init {
        val actions = JPanel(FlowLayout(FlowLayout.LEFT, JBUI.scale(4), 0))
        val clear = JButton("清空")
        val copy = JButton("复制")
        val settings = JButton("设置")
        clear.addActionListener { service.clear() }
        copy.addActionListener { copySelection() }
        settings.addActionListener { MyBatisLogSettingsDialog(project).show() }
        actions.add(clear)
        actions.add(copy)
        actions.add(settings)

        val scroll = JBScrollPane(editorField)
        add(actions, BorderLayout.NORTH)
        add(scroll, BorderLayout.CENTER)
        border = JBUI.Borders.empty(4)

        refreshDocument(scrollToEnd = false)

        service.addAppendListener(onAppend)
        service.addRefreshListener(onRefresh)
        service.addClearListener(onClear)
    }

    private fun refreshDocument(scrollToEnd: Boolean) {
        val text = MyBatisLogDocumentBuilder.build(service.getAllEntries())
        setEditorText(text)
        if (scrollToEnd) {
            scrollEditorToEnd()
        }
    }

    private fun setEditorText(text: String) {
        ApplicationManager.getApplication().runWriteAction {
            logDocument.setReadOnly(false)
            editorField.setText(text)
            logDocument.setReadOnly(true)
        }
    }

    private fun scrollEditorToEnd() {
        val editor = editorField.editor ?: return
        val offset = editor.document.textLength
        editor.caretModel.moveToOffset(offset)
        editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
    }

    private fun sqlFileType() =
        FileTypeManager.getInstance().getFileTypeByExtension("sql")
            .takeIf { it != PlainTextFileType.INSTANCE && it.defaultExtension == "sql" }
            ?: PlainTextFileType.INSTANCE

    private fun copySelection() {
        val editor = editorField.editor
        val text = editor?.selectionModel?.selectedText
        if (!text.isNullOrEmpty()) {
            CopyPasteManager.getInstance().setContents(StringSelection(text))
            return
        }
        val all = editor?.document?.text
        if (!all.isNullOrEmpty()) {
            CopyPasteManager.getInstance().setContents(StringSelection(all))
        }
    }
}
