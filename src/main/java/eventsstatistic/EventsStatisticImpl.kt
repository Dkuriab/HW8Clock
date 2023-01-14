package eventsstatistic

import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventsStatisticImpl(
    private val clock: Clock,
    private val expireTimeInHours: Long = 1
) : EventsStatistic {
    private val eventsStatistic = mutableMapOf<String, MutableList<Instant>>()

    override fun incEvent(name: String) {
        if (!eventsStatistic.containsKey(name)) {
            eventsStatistic[name] = mutableListOf()
        }
        eventsStatistic[name]?.add(clock.instant())
    }

    override fun getEventStatisticByName(name: String): Double {
        removeExpiredEvents()
        return getEventRPM(name)
    }

    override fun getAllEventStatistic(): Map<String, Double> {
        removeExpiredEvents()

        return eventsStatistic.mapValues { getEventRPM(it.key) }
    }

    override fun printStatistic() {
        getAllEventStatistic().forEach {
            println("${it.key} RMP = ${it.value}")
        }
    }

    private fun getEventRPM(name: String): Double =
        eventsStatistic[name]?.let {
            it.size.toDouble() / MINUTES_IN_HOUR
        } ?: 0.0


    private fun removeExpiredEvents() {
        val lastValidTime = clock.instant().minus(expireTimeInHours, ChronoUnit.HOURS)

        println(lastValidTime)

        for (eventRecording in eventsStatistic) {
            println("${ eventRecording.key } - ${ eventRecording.value }")
            eventsStatistic[eventRecording.key] = eventRecording.value.filter {
                it.isAfter(lastValidTime)
            }.toMutableList()
        }
    }

    companion object {
        const val MINUTES_IN_HOUR = 60
    }
}