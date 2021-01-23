package hm.binkley.labs.cereal

import java.util.ServiceLoader

@Suppress("UNCHECKED_CAST")
internal fun <T : Any, U, R> U.serve(
    typeName: String,
    default: U.() -> R,
    match: U.(Grain<T>) -> R,
): R {
    val grains = ServiceLoader.load(Grain::class.java).filter {
        it.consent(typeName)
    }.map {
        it as Grain<T>
    }

    return when (grains.size) {
        0 -> default()
        1 -> match(grains.first())
        else -> throw Bug("Too many grains: $typeName")
    }
}
