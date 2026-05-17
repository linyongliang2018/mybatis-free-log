package com.swqsv.mybatisfreelog

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

class MyBatisConsoleFilterProvider : ConsoleFilterProvider {
    override fun getDefaultFilters(project: Project): Array<Filter> {
        return arrayOf(MyBatisConsoleFilter(project))
    }
}
