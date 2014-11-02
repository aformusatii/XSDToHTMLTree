# Builds an HTML Tree from given XSD.

![XSD to HTML tree](https://raw.githubusercontent.com/aformusatii/XSDToHTMLTree/master/html/images/converter.png)

You will need:
* Java 5+
* Maven 2+


## Installation and Usage

Install:

	mvn install

Quick run to convert default `xsd/sample_shiporder.xsd` file:

	mvn -q exec:java

Ouput file `output/sample_shiporder.html`

Run to convert custom xsd file (you can use absolute path to xsd as well):

	mvn -q exec:java -DrootXSDFile=xsd/somefile.xsd

Ouput file `output/somefile.html` 

## Output HTML Customization

For html customization please see `template.html` in the template folder and corresponding static resources in the html folder. 