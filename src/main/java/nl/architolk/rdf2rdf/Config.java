package nl.architolk.rdf2rdf;

import java.util.List;
import java.util.Map;

public class Config {
  private String title;
  private String version;
  private List<ConfigStatement> queries;
  private Map<String,String> prefixes;

  public String getTitle() {
    return title;
  }

  public String getVersion() {
    return version;
  }

  public List<ConfigStatement> getQueries() {
    return queries;
  }

  public Map<String,String> getPrefixes() {
    return prefixes;
  }

}
