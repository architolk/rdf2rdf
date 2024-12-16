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

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.lang.Runnable;
import java.util.List;

@Command(name = "rdf2rdf")
public class Convert implements Runnable{

  private static final Logger LOG = LoggerFactory.getLogger(Convert.class);

  private static Config config;

  @Option(names={"-i","-input"},description="Input file: <input.xml> or <input.ttl> or..")
  private String inputFile;
  @Option(names={"-o","-output"},description="Output file: <output.xml> or <output.ttl> or..")
  private String outputFile;
  @Option(names={"-c","-config"},description="Config file: <config.yaml> or..")
  private String configFile;
  @Option(names={"-i2","-input2"},description="Extra input file: <input.xml> or <input.ttl> or..")
  private String extraInputFile;
  @Option(names={"-f","-format"},description="Serialization format to use in output")
  private String outputExt;

  @Parameters
  private List params;

  @Override
  public void run() {
    if ((inputFile!=null) && (outputFile!=null)) {
      //New way of doing things, using real CLI parameters
      startConverting();
    } else {
      //Original parameter structure
      LOG.warn("Using deprecated way of stating parameters");
      if (params.size() >= 2) {
        inputFile = (String)params.get(0);
        outputFile = (String)params.get(1);
        if (params.size()>=3) {
          configFile = (String)params.get(2);
        }
        if (params.size()==4) {
          extraInputFile = (String)params.get(3);
        }
        startConverting();
      } else {
        LOG.info("Usage: rdf2rdf <input.xml> <output.xml> [config.yaml]");
      }
    }
  }

  static private RDFFormat getFormat(String outputExt) {
    RDFFormat format = null;
    if (outputExt!=null) {
      switch (outputExt) {
        case "RDFXML_PLAIN": format=RDFFormat.RDFXML_PLAIN; break;
      }
    }
    return format;
  }

  private void startConverting() {

    LOG.info("Starting conversion");
    if (extraInputFile!=null) {
      //Bit hacky, but extra input file at the end
      LOG.info("Input files: {},{}",inputFile,extraInputFile);
    } else {
      LOG.info("Input file: {}",inputFile);
    }
    LOG.info("Ouput file: {}",outputFile);

    if (configFile!=null) {
      LOG.info("Config file: {}",configFile);
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      try {
        config = mapper.readValue(new File(configFile), Config.class);

        LOG.info("Configuration: {}, {} queries",config.getTitle(),config.getQueries().size());
      }
      catch (Exception e) {
        LOG.error(e.getMessage(),e);
        return;
      }
    }

    try {
      Model outModel;
      if (configFile!=null) {
        Model inModel = RDFDataMgr.loadModel(inputFile);
        if (extraInputFile!=null) {
          inModel.add(RDFDataMgr.loadModel(extraInputFile));
        }
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
        outModel = RDFDataMgr.loadModel(inputFile);
        if (extraInputFile!=null) {
          outModel.add(RDFDataMgr.loadModel(extraInputFile));
        }
      }
      RDFFormat outputFormat = getFormat(outputExt);
      if (outputFormat==null) {
        RDFDataMgr.write(new FileOutputStream(outputFile),outModel, RDFLanguages.filenameToLang(outputFile,RDFLanguages.JSONLD));
      } else {
        RDFDataMgr.write(new FileOutputStream(outputFile),outModel, outputFormat);
      }
      LOG.info("Done!");
    }
    catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
  }

}
