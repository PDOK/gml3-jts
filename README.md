# gml3-jts

[![Build Status](https://github.com/PDOK/gml3-jts/actions/workflows/maven.yaml/badge.svg)](https://github.com/PDOK/gml3-jts/actions)
[![Maven Central](https://img.shields.io/maven-central/v/nl.pdok/gml3-jts.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/nl.pdok/gml3-jts)

Converts GML to JTS. For now GML 3.1.1.2 and 3.2.1 are supported.

## TL;DR

```mvn
<dependency>
  <groupId>nl.pdok</groupId>
  <artifactId>gml3-jts</artifactId>
  <version>30.0.0</version>
</dependency>
```

## Notes

1. Needs Java 17, doesn't support Java 8, 11
1. Make sure a compatible maven version is used, >= 3.8.x

| Version | XML Binding          |
| ------- | -------------------- |
| 17.1.0  | **javax**.xml.bind   |
| 30.0.0+ | **jakarta**.xml.bind |

## Test

```sh
mvn test
```

## Build

```sh
mvn clean install
```
