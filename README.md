# gml3-jts

![Build Status](https://github.com/PDOK/gml3-jts/actions/workflows/maven.yaml/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/nl.pdok/gml3-jts.svg?maxAge=2592000)

Converts GML to JTS. For now GML 3.1.1.2 and 3.2.1 are supported.

## Note

1. Needs Java 8, doesn't support Java 11+

## Import into Eclipse

1. This project can be imported into Eclipse through the 'default' methodes
   import>Existing maven project>..
1. When the project is loaded it will show error, because it cannot resolve the
   packages org.opengis.*.
1. This is fixed by building the project with maven (mvn clean install, through
   Eclipse or the command line).
1. A target dir is generated with the following path
   ./target/generated-sources/xjc, this dir needs to be added to the buildpath
   (right mouse button > Build Path > Use as Source Folder)
