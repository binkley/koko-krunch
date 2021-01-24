package hm.binkley.labs.cereal

import hm.binkley.labs.cereal.Crunchiness.MEDIUM
import java.math.BigInteger.TWO
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID.randomUUID

fun main() {
    val written = KokoKrunch(
        beans = 20,
        bint = TWO.pow(Long.SIZE_BITS + 1),
        bool = true,
        byte = 3.toByte(),
        ch = 'A',
        crunch = Crunch(MEDIUM),
        d = 3.14159,
        drool = false,
        f = 1.234f,
        `how long` = Duration.ofDays(1L).plusNanos(1L),
        long = 9_876_543_210,
        now = Instant.now(),
        nullablePrimitive = null,
        optional = null,
        required = "BOB",
        s = 1024.toShort(),
        uuid = randomUUID(),
        `when` = LocalDateTime.now(),
        z = 13,
    )
    val bytes = written.write()

    println("WRITE -> $written")
    println("BYTES (len=${bytes.size}) -> ${bytes.pretty()}")
    val read = bytes.read<KokoKrunch>()
    println("READ -> $read")
    println("EQ? -> ${read == written}")
    println()
    bytes.dump()
}

private fun ByteArray.dump() {
    with(toByteBuffer()) {
        val magic = ByteArray(MAGIC.length)
        get(magic)
        val version = byte
        println("${String(magic)} $version")
        val klass = readKClass<Any>()
        println(klass)

        (0 until readInt()).forEach {
            val field = readField(klass)
            println("#$it $field = ${readValue(field, int)}")
            readSentinel()
        }

        readSentinel()
    }
}
