<a href="LICENSE.md">
<img src="./images/public-domain.png" alt="Public Domain" align="right" 
width="5%"/>
</a>

# Koko Krunch

Demonstration serialization with Kotlin and JDK reflection

<img src="./images/koko-krunch.jpg" alt="Koko Krunch" align="right"
width="20%"/>

[![build](https://github.com/binkley/koko-krunch/workflows/build/badge.svg)](https://github.com/binkley/koko-krunch/actions)
[![issues](https://img.shields.io/github/issues/binkley/koko-krunch.svg)](https://github.com/binkley/koko-krunch/issues/)
[![Public Domain](https://img.shields.io/badge/license-Public%20Domain-blue.svg)](http://unlicense.org/)

* [Build](#build)
* [Reading](#reading)

## Build

Try [`./run`](./run) for a demonstration.

The build is vanilla [Maven](pom.xml), with [Batect](https://batect.dev)
offered as a means to reproduce locally what CI does.

```
# With Maven
$ ./mvnw clean verify
$ ./run
# With Batect
$ ./batect build
$ ./batect run
```

## Reading

* [_Serialization and deserialization in Java: explaining the Java deserialize
  vulnerability_](https://snyk.io/blog/serialization-and-deserialization-in-java/)
