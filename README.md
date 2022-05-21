# RDF2RDF Generating RDF from another RDF file

## Build

Requirements: Java JDK and Maven

```
mvn clean package
```

## Usage

```
java -jar rdf2rdf.jar <input.xml> <output.xml> [config.yaml]
```

Input and output can be any RDF serialization (XML, JSON, Turtle). Input format will be transformed to the output format.

A optional yaml configuration can be included. In this configuration you can add SPARQUL queries dat will construct triples from the input format. The input model will be available in the named graph <urn:input>, the output model will be expected in the model <urn:output>. Please look at the example [config.yaml](config.yaml) for the exact format.
