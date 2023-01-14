package eventsstatistic

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class SettableClock(var settableNow: Instant): Clock() {

    fun incrementTime(unit: ChronoUnit, value: Long) {
        settableNow = settableNow.plus(value, unit)
    }

    override fun instant(): Instant = settableNow

    override fun withZone(zone: ZoneId?): Clock = fixed(settableNow, zone)

    override fun getZone(): ZoneId = systemDefaultZone().zone
}