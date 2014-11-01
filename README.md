# Builds an HTML Tree from given XSD.

You will need:
* Java 5+
* Maven 2+


## Installation and Usage

Install:

	mvn install

Quick Run to convert default `xsd/sample_shiporder.xsd` file:

	mvn -q exec:java

Ouput file `output/sample_shiporder.html`

Run to convert custom xsd file:

	mvn -q exec:java -DrootXSDFile=somefile.xsd

Ouput file `output/somefile.html` 

## HTML Customization

For html customization please see `template.html` in the template folder and corresponding static resources in the html folder. 