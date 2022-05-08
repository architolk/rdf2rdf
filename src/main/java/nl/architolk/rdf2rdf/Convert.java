package nl.architolk.rdf2rdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
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

          LOG.info("Configuration: {}, {} statement(s)",config.getTitle(),config.getConfigStatements().size());
        }
        catch (Exception e) {
          LOG.error(e.getMessage(),e);
          return;
        }
      }

      try {
        Model outmodel;
        if ((args.length==3) && (config.getConfigStatements().size()>0)) {
          Model inmodel = RDFDataMgr.loadModel(args[0]);
          outmodel = ModelFactory.createDefaultModel();
          for (ConfigStatement statement : config.getConfigStatements()) {
            LOG.info("- execute: {}",statement.getTitle());
            Query query = QueryFactory.create(statement.getQuery());
            QueryExecution qe = QueryExecutionFactory.create(query,inmodel);
            qe.execConstruct(outmodel);
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
      LOG.info("Usage: archimate2rdf <input.xml> <output.xml> [config.yaml]");
    }
  }
}
