package org.cfg4j.source.context.propertiesprovider.hocon;

import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;


/**
 * Provides default configuration file (i.e. application.properties).
 */
public class HoconConfigFilesProvider implements ConfigFilesProvider {

  @Override
  public Iterable<Path> getConfigFiles() {

    return Collections.singletonList(Paths.get("application.conf"));
  }

  @Override
  public String toString() {
    return "HoconConfigFilesProvider{}";
  }
}
