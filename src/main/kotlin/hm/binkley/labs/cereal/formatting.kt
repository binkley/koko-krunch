package hm.binkley.labs.cereal

internal fun Byte.pretty() = "\\x%02x".format(this)
internal fun ByteArray.pretty() = joinToString(" ", "[", "]") {
    it.pretty()
}
