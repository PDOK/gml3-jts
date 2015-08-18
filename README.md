# gml311-jts
Converts GML 3.1.1 to JTS. Converts JTS to GML 3.1.1

# Import into Eclipse
This project can be imported into Eclipse through the 'default' methodes import>Existing maven project>..
When the project is loaded it will show error, because it cannot resolve the packages org.opengis.*.
This is fixed by building the the project with maven (mvn clean install, through Eclipse or the command line).
A target dir is generated with the following path ./target/generated-sources/xjc, this dir needs to be added to the buildpath (right mouse button > Build Path > Use as Source Folder)
