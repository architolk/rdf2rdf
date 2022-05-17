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

          LOG.info("Configuration: {}, {} constructs(s), {} updates(s)",config.getTitle(),config.getConstructs().size(),config.getUpdates().size());
        }
        catch (Exception e) {
          LOG.error(e.getMessage(),e);
          return;
        }
      }

      try {
        Model outmodel;
        if (args.length==3) {
          Model inmodel = RDFDataMgr.loadModel(args[0]);
          outmodel = ModelFactory.createDefaultModel();
          for (ConfigStatement construct : config.getConstructs()) {
            LOG.info("- construct: {}",construct.getTitle());
            Query query = QueryFactory.create(construct.getQuery());
            QueryExecution qe = QueryExecutionFactory.create(query,inmodel);
            qe.execConstruct(outmodel);
          }
          Dataset dataset = DatasetFactory.create(outmodel);
          try {
            for (ConfigStatement update : config.getUpdates()) {
              LOG.info("- update: {}",update.getTitle());
              UpdateRequest request = UpdateFactory.create(update.getQuery());
              UpdateExecution.dataset(dataset).update(request).execute();
            }
            dataset.commit();
          } finally {
            dataset.end();
          }
        } else {
          outmodel = RDFDataMgr.loadModel(args[0]);
        }
        //RDFDataMgr.write(new FileOutputStream(args[1]),model, RDFFormat.JSONLD_COMPACT_PRETTY);
        RDFDataMgr.write(new FileOutputStream(args[1]),outmodel, RDFLanguages.filenameToLang(args[1],RDFLanguages.JSONLD));
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
