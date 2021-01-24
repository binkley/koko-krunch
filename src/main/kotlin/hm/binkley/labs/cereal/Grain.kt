package hm.binkley.labs.cereal

import java.nio.ByteBuffer
import java.util.ServiceLoader.load
import kotlin.reflect.KClass

interface Grain<T : Any> {
    fun consent(type: KClass<*>): Boolean
    fun absorb(it: T): Prep
    fun extrude(buf: ByteBuffer, len: Int): T
}

internal fun <T : Any, U, R> U.serve(
    type: KClass<*>,
    default: U.() -> R,
    match: Grain<T>.(U) -> R,
) = load(Grain::class.java).filter {
    it.consent(type)
}.map {
    cast<Grain<T>>(it)
}.map {
    it.match(this)
    // size >= 2 forbidden by JDK specifications for ServiceLoader
}.firstOrNull() ?: default()
