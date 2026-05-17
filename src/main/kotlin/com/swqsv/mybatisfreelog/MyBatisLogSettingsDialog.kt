package com.swqsv.mybatisfreelog

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class MyBatisLogSettingsDialog(
    private val project: Project,
) : DialogWrapper(project) {

    private val settings = MyBatisLogSettings.getInstance()
    private val maxSpinner = JSpinner(
        SpinnerNumberModel(
            settings.maxEntries,
            MyBatisLogSettings.MIN_ENTRIES,
            MyBatisLogSettings.MAX_ENTRIES,
            100,
        ),
    )

    init {
        title = "MyBatis Free Log 设置"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout(JBUI.scale(8), 0))
        panel.border = JBUI.Borders.empty(8)
        panel.add(JBLabel("最多保留最近 SQL 条数（默认 ${MyBatisLogSettings.DEFAULT_MAX_ENTRIES}）："), BorderLayout.NORTH)
        panel.add(maxSpinner, BorderLayout.CENTER)
        return panel
    }

    override fun doOKAction() {
        val v = (maxSpinner.value as? Number)?.toInt() ?: MyBatisLogSettings.DEFAULT_MAX_ENTRIES
        settings.maxEntries = v
        project.service<MyBatisLogService>().applyMaxEntriesLimit()
        super.doOKAction()
    }
}
