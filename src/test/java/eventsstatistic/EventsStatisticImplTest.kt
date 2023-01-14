package eventsstatistic

import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

internal class EventsStatisticImplTest {
    var settableClock: SettableClock = SettableClock(Instant.now())

    @Test
    fun validStatisticInOneHour() {
        val eventsStatistic = EventsStatisticImpl(settableClock)
        val eventName = "TestName"

        assertEquals(0.0,  eventsStatistic.getEventStatisticByName(eventName))

        eventsStatistic.incEvent(eventName)
        settableClock.incrementTime(ChronoUnit.MINUTES, 15)
        eventsStatistic.incEvent(eventName)
        settableClock.incrementTime(ChronoUnit.MINUTES, 15)
        eventsStatistic.incEvent(eventName)

        assertEquals(3.0 / 60,  eventsStatistic.getEventStatisticByName(eventName))
    }

    @Test
    fun correctExpiredEventsRemoval() {
        val eventsStatistic = EventsStatisticImpl(settableClock)
        val eventName = "TestName"

        eventsStatistic.incEvent(eventName)
        assertEquals(1.0 / 60,  eventsStatistic.getEventStatisticByName(eventName))
        settableClock.incrementTime(ChronoUnit.HOURS, 1)
        assertEquals(0.0,  eventsStatistic.getEventStatisticByName(eventName))
    }

    @Test
    fun validEventsExpiringWithCustomExpireTime() {
        val eventsStatistic = EventsStatisticImpl(settableClock, 2)

        val eventName = "TestName"

        eventsStatistic.incEvent(eventName)
        assertEquals(1.0 / 60,  eventsStatistic.getEventStatisticByName(eventName))
        settableClock.incrementTime(ChronoUnit.HOURS, 1)
        assertEquals(1.0 / 60,  eventsStatistic.getEventStatisticByName(eventName))
        settableClock.incrementTime(ChronoUnit.HOURS, 1)
        assertEquals(0.0,  eventsStatistic.getEventStatisticByName(eventName))
    }

    @Test
    fun validMultipleEventsStatistics() {
        val eventsStatistic = EventsStatisticImpl(settableClock)
        val firstEventName = "First"
        val secondEventName = "Second"

        val statisticsMap = mapOf(
            firstEventName to 5.0 / 60,
            secondEventName to 2.0 / 60,
        )

        eventsStatistic.incEvent(secondEventName) // should be expired at the end
        settableClock.incrementTime(ChronoUnit.MINUTES, 15)

        eventsStatistic.incEvent(firstEventName)
        eventsStatistic.incEvent(firstEventName)
        eventsStatistic.incEvent(secondEventName)
        settableClock.incrementTime(ChronoUnit.MINUTES, 40)

        eventsStatistic.incEvent(firstEventName)
        settableClock.incrementTime(ChronoUnit.MINUTES, 1)

        eventsStatistic.incEvent(firstEventName)
        eventsStatistic.incEvent(secondEventName)
        settableClock.incrementTime(ChronoUnit.MINUTES, 1)

        eventsStatistic.incEvent(firstEventName)
        settableClock.incrementTime(ChronoUnit.MINUTES, 17)

        assertEquals(statisticsMap, eventsStatistic.getAllEventStatistic())
    }
}