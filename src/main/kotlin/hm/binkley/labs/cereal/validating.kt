package hm.binkley.labs.cereal

import java.lang.reflect.Field
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/** @todo Syntactic sugar causes cancer of the semicolon */
internal fun ByteBuffer.assertMetadata() = try {
    MAGIC.forEach {
        // NB -- "MAGIC" is all ASCII, no multibyte characters
        if (it.code.toByte() != byte) throw CerealException("Not $MAGIC data")
    }

    val version = byte
    if (VERSION != version) throw CerealException("Wrong $MAGIC version: expected $VERSION; got $version")

    this
} catch (e: BufferUnderflowException) {
    throw CerealException("Missing bytes: possibly truncated or corrupted, or class version changed")
}

internal fun <T : Any> ByteBuffer.assertEnoughData(
    klass: KClass<T>,
    block: ByteBuffer.(KClass<T>) -> T,
) = try {
    this.block(klass)
} catch (e: BufferUnderflowException) {
    throw CerealException("Missing bytes: possibly truncated or corrupted, or class version changed")
}

internal fun ByteBuffer.assertSentinel() = readSentinel().also {
    if (0.toByte() != it) {
        throw CerealException("Corrupted sentinel byte: ${it.pretty()} @ ${position() - 1}")
    }
}

internal fun <T : Any> ByteBuffer.assertClassName(expectedClass: KClass<T>) {
    val expectedClassName = expectedClass.jvmName
    val actualClassName = readString()

    if (expectedClassName != actualClassName) {
        throw CerealException("Wrong class: expected '$expectedClassName'; got '$actualClassName'")
    }
}

internal fun assertIntLength(actualLength: Int) {
    if (Int.SIZE_BYTES != actualLength) {
        throw CerealException("Expected int to be ${Int.SIZE_BYTES} bytes, not $actualLength")
    }
}

internal fun <T : Any> assertFieldCount(
    klass: KClass<T>,
    actualFieldCount: Int,
) {
    val expectedFieldCount = klass.serializedFields.size
    if (expectedFieldCount != actualFieldCount) {
        throw CerealException("Field counts changed between class versions: expected $expectedFieldCount; got $actualFieldCount")
    }
}

internal fun ByteBuffer.assertFieldTypeName(field: Field) {
    val expectedFieldTypeName = field.type.name
    val actualFieldTypeName = readString()
    if (expectedFieldTypeName != actualFieldTypeName) {
        // TODO: Why are expected/actual flipped?
        throw CerealException("Field type changed between class versions: expected '$expectedFieldTypeName'; got '$actualFieldTypeName'")
    }
}

internal fun ByteBuffer.assertComplete() {
    if (0 != remaining()) {
        throw CerealException(
            "Extra bytes remaining after object read from buffer: ${
            slice().array().pretty()
            } @ ${position() - 1}"
        )
    }
}
