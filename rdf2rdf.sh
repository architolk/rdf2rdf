java -jar target/rdf2rdf-*-jar-with-dependencies.jar -i data/input.* -o data/output.ttl
java -jar target/rdf2rdf-*-jar-with-dependencies.jar data/input.* data/output.json
java -jar target/rdf2rdf-*-jar-with-dependencies.jar data/input.* data/output.rdf.xml
java -jar target/rdf2rdf-*-jar-with-dependencies.jar -i data/input.* -o data/output-plain.rdf.xml -f RDFXML_PLAIN
