package com.mensis.app.data

import com.mensis.app.CycleLog
import com.mensis.app.CycleRecord
import org.json.JSONArray
import org.json.JSONObject

/** Builds a human-readable JSON backup of all local data. */
object Exporter {
    fun buildJson(settings: Settings, cycles: List<CycleRecord>, logs: List<CycleLog>): String {
        val root = JSONObject()
        root.put("app", "Mensis")
        root.put("version", 2)
        root.put("exportedAt", java.time.LocalDateTime.now().toString())

        root.put("profile", JSONObject().apply {
            put("userName", settings.userName)
            put("cycleLength", settings.cycleLength)
            put("periodLength", settings.periodLength)
            put("pregnancyMode", settings.pregnancyMode)
            settings.pregnancyReferenceDate?.let {
                put("pregnancyReferenceType", settings.pregnancyReferenceType)
                put("pregnancyReferenceDate", it.toString())
            }
        })

        root.put("cycles", JSONArray().apply {
            cycles.forEach { c ->
                put(JSONObject().apply {
                    put("start", c.startDate.toString())
                    c.endDate?.let { put("end", it.toString()) }
                })
            }
        })

        root.put("logs", JSONArray().apply {
            logs.forEach { l ->
                put(JSONObject().apply {
                    put("date", l.date.toString())
                    l.temperature?.let { put("temperature", it) }
                    l.flow?.let { put("flow", it) }
                    l.pain?.let { put("pain", it) }
                    l.mucus?.let { put("mucus", it) }
                    l.lhTestResult?.let { put("lhTest", it) }
                    l.mood?.let { put("mood", it) }
                    l.symptoms?.let { put("symptoms", it) }
                    l.medications?.let { put("medications", it) }
                    l.intercourse?.let { put("intercourse", it) }
                    l.weight?.let { put("weight", it) }
                    l.sleepHours?.let { put("sleepHours", it) }
                    l.waterGlasses?.let { put("waterGlasses", it) }
                    l.activity?.let { put("activity", it) }
                    l.notes?.let { put("notes", it) }
                })
            }
        })
        return root.toString(2)
    }
}
