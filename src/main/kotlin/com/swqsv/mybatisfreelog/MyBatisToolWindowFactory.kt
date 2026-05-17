package com.swqsv.mybatisfreelog

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MyBatisToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setIcon(MybatisFreeLogIcons.ToolWindow)
        val panel = MyBatisToolWindowPanel(project)
        val content = ContentFactory.getInstance().createContent(
            panel,
            "MyBatis Free Log",
            false,
        )
        content.isCloseable = false
        toolWindow.contentManager.addContent(content)
    }
}
