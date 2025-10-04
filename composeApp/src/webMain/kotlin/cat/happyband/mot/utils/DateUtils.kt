package cat.happyband.mot.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun getStartOfDayISO(): String {
    // 1. Obtenir el moment actual en UTC (estàndard per al servidor)
    val now = Clock.System.now()

    // 2. Convertir-ho a la data/hora local en UTC (la mateixa que el servidor)
    val nowInUtc = now.toLocalDateTime(TimeZone.UTC)

    // 3. Crear una nova data/hora amb l'hora a 00:00:00
    val startOfDay = LocalDateTime(
        year = nowInUtc.year,
        month = nowInUtc.month,
        dayOfMonth = nowInUtc.dayOfMonth,
        hour = 0,
        minute = 0,
        second = 0,
        nanosecond = 0
    )

    // 4. Convertir-ho de nou a Instant i formatar-ho com a cadena ISO 8601
    return startOfDay.toInstant(TimeZone.UTC).toString()
}