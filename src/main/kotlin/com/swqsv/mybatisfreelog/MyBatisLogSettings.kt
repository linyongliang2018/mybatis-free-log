package com.swqsv.mybatisfreelog

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.APP)
@State(name = "MyBatisFreeLogSettings", storages = [Storage("mybatisFreeLog.xml")])
class MyBatisLogSettings : PersistentStateComponent<MyBatisLogSettings.State> {

    data class State(
        var maxEntries: Int = DEFAULT_MAX_ENTRIES,
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
        this.state.maxEntries = this.state.maxEntries.coerceIn(MIN_ENTRIES, MAX_ENTRIES)
    }

    var maxEntries: Int
        get() = state.maxEntries.coerceIn(MIN_ENTRIES, MAX_ENTRIES)
        set(value) {
            state.maxEntries = value.coerceIn(MIN_ENTRIES, MAX_ENTRIES)
        }

    companion object {
        const val DEFAULT_MAX_ENTRIES = 1000
        const val MIN_ENTRIES = 1
        const val MAX_ENTRIES = 100_000

        fun getInstance(): MyBatisLogSettings =
            ApplicationManager.getApplication().getService(MyBatisLogSettings::class.java)
    }
}
