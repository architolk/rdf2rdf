# RDF2RDF Generating RDF from another RDF file

## Build

Requirements: Java JDK and Maven

```
mvn clean package
```

## Usage

Old style:
```
java -jar rdf2rdf.jar <input.xml> <output.xml> [config.yaml]
```

New style:
```
Usage: rdf2rdf [-c=<configFile>] [-f=<outputExt>] [-g=<graphFolder>]
               [-i=<inputFile>] [-i2=<extraInputFile>] [-o=<outputFile>]
               [-p=<inputPath>] [-s=<shapesFile>] [<params>...]
      [<params>...]
  -c, -config=<configFile>   Config file: <config.yaml> or..
  -f, -format=<outputExt>    Serialization format to use in output
  -g, -graph=<graphFolder>   Graph output folder: <output>
  -i, -input=<inputFile>     Input file: <input.xml> or <input.ttl> or..
      -i2, -input2=<extraInputFile>
                             Extra input file: <input.xml> or <input.ttl> or..
  -o, -output=<outputFile>   Output file: <output.xml> or <output.ttl> or..
  -p, -path=<inputPath>      Input path: <pathname>
  -s, -shapes=<shapesFile>   Shapes file: <shapes.xml> or <shapes.ttl> or..
```

Input and output can be any RDF serialization (XML, JSON, Turtle). Input format will be transformed to the output format.

A optional yaml configuration can be included. In this configuration you can add SPARQUL queries dat will construct triples from the input format. The input model will be available in the named graph `<urn:input>`, the output model will be expected in the named graph `<urn:output>`. Please look at the example [config.yaml](config.yaml) for the exact format.

A shapesfile can be added to validate the output graph against the shapes in the given file. The report will be sent to the console. The output will only be writen to the outputfile if the output graph conforms to the shacl shapes graph.

By using the -g(raph) option, you can write all created graphs to the output, one filename per named graph.

A custom function is available to create UUID uri's from a string value - to make persistent uri's without any reference to some original value, for example:

```
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX f: <java:nl.architolk.rdf2rdf.>
SELECT (f:uuid5(?label) as ?uri)
WHERE {
  ?s rdfs:label ?label
}
```
