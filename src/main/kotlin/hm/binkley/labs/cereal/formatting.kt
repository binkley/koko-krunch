package hm.binkley.labs.cereal

internal fun Byte?.pretty() =
    if (null == this) null.toString() else "\\x%02x".format(this)

internal fun ByteArray.pretty() = joinToString(" ", "[", "]") {
    it.pretty()
}
