package heraclius.tools

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    val zoneId: ZoneId = ZoneId.of("Asia/Shanghai")
    val defaultFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun format(datetime: LocalDateTime, formatter: DateTimeFormatter = defaultFormatter): String {
        return datetime.format(formatter)
    }

    fun from(timestamp: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(timestamp)
        return LocalDateTime.ofInstant(instant, zoneId)
    }

    fun from(string: String, formatter: DateTimeFormatter = defaultFormatter): LocalDateTime {
        return LocalDateTime.parse(string, formatter)
    }

    fun timestamp(datetime: LocalDateTime = now()): Long {
        return datetime.atZone(zoneId).toInstant().toEpochMilli()
    }

    fun now(): LocalDateTime {
        return LocalDateTime.now(zoneId)
    }
}
