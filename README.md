# gml3-jts
Converts GML to JTS. For now GML 3.1.1.2 and 3.2.1 are supported.

# Import into Eclipse
1. This project can be imported into Eclipse through the 'default' methodes import>Existing maven project>..
2. When the project is loaded it will show error, because it cannot resolve the packages org.opengis.*.
3. This is fixed by building the the project with maven (mvn clean install, through Eclipse or the command line).
4. A target dir is generated with the following path ./target/generated-sources/xjc, this dir needs to be added to the buildpath (right mouse button > Build Path > Use as Source Folder)
