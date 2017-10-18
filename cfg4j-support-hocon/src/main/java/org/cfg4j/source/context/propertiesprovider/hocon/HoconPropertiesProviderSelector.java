package org.cfg4j.source.context.propertiesprovider.hocon;

import org.cfg4j.source.context.propertiesprovider.*;


public class HoconPropertiesProviderSelector extends DefaultPropertiesProviderSelector {

  private final PropertiesProvider hoconProvider;

  /**
   * Construct selector.
   *
   * @param propertiesProvider provider used for parsing properties files
   * @param yamlProvider       provider used for parsing Yaml files
   * @param jsonProvider       provider used for parsing JSON files
   * @param hoconProvider      provider used for parsing HOCON files
   */
  public HoconPropertiesProviderSelector(PropertiesProvider propertiesProvider,
                                         PropertiesProvider yamlProvider,
                                         PropertiesProvider jsonProvider,
                                         PropertiesProvider hoconProvider) {
    super(
      propertiesProvider,
      yamlProvider,
      jsonProvider
    );
    this.hoconProvider = hoconProvider;
  }

  /**
   * Construct selector.
   */
  public HoconPropertiesProviderSelector() {
    this(
      new PropertyBasedPropertiesProvider(),
      new YamlBasedPropertiesProvider(),
      new JsonBasedPropertiesProvider(),
      new HoconBasedPropertiesProvider()
    );
  }

  /**
   * Selects {@link PropertiesProvider} to use based on a file extension. For *.yaml files
   * returns {@code yamlProvider}. For any other extension returns {@code propertiesProvider}.
   *
   * @param filename configuration file name
   * @return provider for the give file type
   */
  @Override
  public PropertiesProvider getProvider(String filename) {
    if (filename.endsWith(".conf")) {
      return hoconProvider;
    } else {
      return super.getProvider(filename);
    }
  }
}
