package nl.architolk.rdf2rdf;

import java.util.List;

public class Config {
  private String title;
  private String version;
  private List<ConfigStatement> queries;

  public String getTitle() {
    return title;
  }

  public String getVersion() {
    return version;
  }

  public List<ConfigStatement> getQueries() {
    return queries;
  }

}
