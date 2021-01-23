<a href="LICENSE.md">
<img src="./images/public-domain.png" alt="Public Domain" align="right" 
width="5%"/>
</a>

# Koko Krunch

Demonstrate simple serialization with Kotlin and JDK reflection.

<img src="./images/koko-krunch.jpg" alt="Koko Krunch" align="right"
width="20%"/>

[![build](https://github.com/binkley/koko-krunch/workflows/build/badge.svg)](https://github.com/binkley/koko-krunch/actions)
[![issues](https://img.shields.io/github/issues/binkley/koko-krunch.svg)](https://github.com/binkley/koko-krunch/issues/)
[![Public Domain](https://img.shields.io/badge/license-Public%20Domain-blue.svg)](http://unlicense.org/)

* [Build and try](#build-and-try)
* [Format](#format)
* [API](#api)
* [TODO](#todo)
* [Reading](#reading)

## Build and try

Try [`./run`](./run) for a demonstration.

The build is vanilla [Maven](pom.xml), with [Batect](https://batect.dev)
offered as a means to reproduce locally what CI does.

```
# With Maven directly
$ ./mvnw clean verify
$ ./run
# With Batect building with Maven
$ ./batect build
$ ./batect run
```

---

## Format

Koko Krunch lays out serialized byte buffers thusly:

1. All fixed-size data is kept as big-endian, eg, a 16-bit short integer is
   stored as MSB followed by LSB.
2. All data is kept contiguously: there is no padding between data unless
   explicitly stated.
3. A buffer is a sequence of records, and is terminated with a 0 byte past the
   last record (in addition to any 0 bytes from records themselves).
4. A record is a triple:
    1. A 4-byte (32-bit integer) count of the payload length.
    2. A variable-length byte sequence payload specific to the data type.
    3. An additional, terminating 0 byte.
5. In the case of serializing a `null` value:
    1. A 4-byte (32-bit integer) count of the payload length _set to -1_.
    2. A 0-length byte sequence payload.
    3. An additional, terminating 0 byte.
6. The records are in sequence of:
    1. A metadata header.  **TODO**
    2. A string data record of the FQN class name of the serialized object.
    3. a 4-byte (32-bit integer) count of the number of subsequent records.
    4. A sequence of records for each non-static, non-transient field.
7. A field sequence is a triple or duple:
    1. A record for the field name.
    2. A record for the FQN field type name.
    3. A record for the field value.
8. When serializing a non-primitive or non-null field value, the payload is
   itself serialized recursively as a complete buffer.

---

## API

### Serializing

- [`fun Any.write(): ByteArray`](./src/main/kotlin/hm/binkley/labs/cereal/KokoKrunch.kt)

## Deserializing

- [`inline fun <reified T> ByteArray.read(): T`](./src/main/kotlin/hm/binkley/labs/cereal/KokoKrunch.kt)
- [`fun <T> ByteArray.read(clazz: Class<T>): T`](./src/main/kotlin/hm/binkley/labs/cereal/KokoKrunch.kt)

---

## TODO

* Use something like `metainf-services` annotation processor to generate the
  `ServiceLoader` files. It is a Java annotation processor. Kapt maven plugin
  seems broken (creates duplicate generated source roots):
  ```
  [INFO] --- kotlin-maven-plugin:1.4.21:compile (compile-kotlin) @ koko-krunch ---
  [WARNING] Duplicate source root: /Users/boxley/src/kt/koko-krunch/target/generated-sources/kapt/compile
  [WARNING] Duplicate source root: /Users/boxley/src/kt/koko-krunch/target/generated-sources/kaptKotlin/compile
  [ERROR] warnings found and -Werror specified
  ```

---

## Reading

* [_Serialization and deserialization in Java: explaining the Java deserialize
  vulnerability_](https://snyk.io/blog/serialization-and-deserialization-in-java/)
