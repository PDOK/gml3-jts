# gml3-jts

[![Build Status](https://github.com/PDOK/gml3-jts/actions/workflows/maven.yaml/badge.svg)](https://github.com/PDOK/gml3-jts/actions)
[![Maven Central](https://img.shields.io/maven-central/v/nl.pdok/gml3-jts.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22nl.pdok%22%20AND%20a:%22gml3-jts%22)

Converts GML to JTS. For now GML 3.1.1.2 and 3.2.1 are supported.

## TL;DR

```mvn
<dependency>
  <groupId>nl.pdok</groupId>
  <artifactId>gml3-jts</artifactId>
  <version>17.1.0</version>
</dependency>
```

## Note

1. Needs Java 17, doesn't support Java 8, 11
1. Make sure a compatible maven version is used, >= 3.8.x

## Test

```sh
mvn test
```

## Build

```sh
mvn clean install
```
