package com.swqsv.mybatisfreelog

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * 统一图标：工具窗口条、及代码中 [javax.swing.Action] 等请使用同一路径的 SVG，IDE 会按 1x/2x/HiDPI 自动缩放。
 * 市场与「Plugins」列表使用 [META-INF/pluginIcon.svg]（与资源文件视觉一致，独立 gradient id 避免与工具栏 SVG 冲突）。
 */
object MybatisFreeLogIcons {
    @JvmField
    val ToolWindow: Icon = IconLoader.getIcon("/icons/mybatis_free_log.svg", MybatisFreeLogIcons::class.java)
}
