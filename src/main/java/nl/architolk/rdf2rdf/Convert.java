package nl.architolk.rdf2rdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.update.UpdateExecution;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Convert {

  private static final Logger LOG = LoggerFactory.getLogger(Convert.class);

  private static Config config;

  public static void main(String[] args) {

    if (args.length >= 2) {

      LOG.info("Starting conversion");
      LOG.info("Input file: {}",args[0]);
      LOG.info("Ouput file: {}",args[1]);

      if (args.length==3) {
        LOG.info("Config file: {}",args[2]);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
          config = mapper.readValue(new File(args[2]), Config.class);

          LOG.info("Configuration: {}, {} queries",config.getTitle(),config.getQueries().size());
        }
        catch (Exception e) {
          LOG.error(e.getMessage(),e);
          return;
        }
      }

      try {
        Model outModel;
        if (args.length==3) {
          Model inModel = RDFDataMgr.loadModel(args[0]);
          Dataset dataset = DatasetFactory.create();
          dataset.addNamedModel("urn:input",inModel);
          try {
            for (ConfigStatement query : config.getQueries()) {
              LOG.info("- query: {}",query.getTitle());
              UpdateRequest request = UpdateFactory.create(query.getQuery());
              UpdateExecution.dataset(dataset).update(request).execute();
            }
            dataset.commit();
          } finally {
            dataset.end();
          }
          outModel = dataset.getNamedModel("urn:output");
          outModel.setNsPrefixes(inModel);
          if (config.getPrefixes()!=null) {
            outModel.setNsPrefixes(config.getPrefixes());
          }
        } else {
          outModel = RDFDataMgr.loadModel(args[0]);
        }
        RDFDataMgr.write(new FileOutputStream(args[1]),outModel, RDFLanguages.filenameToLang(args[1],RDFLanguages.JSONLD));
        LOG.info("Done!");
      }
      catch (Exception e) {
        LOG.error(e.getMessage(),e);
      }
    } else {
      LOG.info("Usage: rdf2rdf <input.xml> <output.xml> [config.yaml]");
    }
  }
}
