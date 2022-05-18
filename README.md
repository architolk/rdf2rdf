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

A optional yaml configuration can be included. In this configuration you can add CONSTRUCT queries dat will construct triples from the input format. Only these constructed triples will be serialized to the output format.

The configuration file can also include INSERT or DELETE statements. These statements will be executed AFTER all construct queries are executed. Please look at the example [config.yaml](config.yaml) for the exact format.
