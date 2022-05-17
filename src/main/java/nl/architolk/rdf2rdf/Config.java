package nl.architolk.rdf2rdf;

import java.util.List;

public class Config {
  private String title;
  private String version;
  private List<ConfigStatement> constructs;
  private List<ConfigStatement> updates;

  public String getTitle() {
    return title;
  }

  public String getVersion() {
    return version;
  }

  public List<ConfigStatement> getConstructs() {
    return constructs;
  }

  public List<ConfigStatement> getUpdates() {
    return updates;
  }

}
