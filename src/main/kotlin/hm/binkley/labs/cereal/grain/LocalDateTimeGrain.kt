package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class LocalDateTimeGrain : Grain<LocalDateTime> {
    override fun consent(typeName: String) =
        LocalDateTime::class.java.name == typeName

    override fun absorb(it: LocalDateTime): Prep =
        7 * Int.SIZE_BYTES to { buf ->
            val date = it.toLocalDate()
            buf.putInt(date.year)
            buf.putInt(date.monthValue)
            buf.putInt(date.dayOfMonth)
            val time = it.toLocalTime()
            buf.putInt(time.hour)
            buf.putInt(time.minute)
            buf.putInt(time.second)
            buf.putInt(time.nano)
        }

    override fun extrude(buf: ByteBuffer, len: Int): LocalDateTime =
        LocalDateTime.of(
            LocalDate.of(
                buf.int,
                buf.int,
                buf.int
            ),
            LocalTime.of(
                buf.int,
                buf.int,
                buf.int,
                buf.int
            )
        )
}
