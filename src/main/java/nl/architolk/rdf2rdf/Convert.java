package nl.architolk.rdf2rdf;

import java.io.FileOutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Convert {

  private static final Logger LOG = LoggerFactory.getLogger(Convert.class);

  public static void main(String[] args) {

    if (args.length == 2) {

      LOG.info("Starting conversion");
      LOG.info("Input file: {}",args[0]);
      LOG.info("Ouput file: {}",args[1]);

      try {
        Model model = RDFDataMgr.loadModel(args[0]);
        //RDFDataMgr.write(new FileOutputStream(args[1]),model, RDFFormat.JSONLD_COMPACT_PRETTY);
        RDFDataMgr.write(new FileOutputStream(args[1]),model, RDFLanguages.filenameToLang(args[1],RDFLanguages.JSONLD));
        LOG.info("Done!");
      }
      catch (Exception e) {
        LOG.error(e.getMessage(),e);
      }
    } else {
      LOG.info("Usage: archimate2rdf <input.xml> <output.xml>");
    }
  }
}
