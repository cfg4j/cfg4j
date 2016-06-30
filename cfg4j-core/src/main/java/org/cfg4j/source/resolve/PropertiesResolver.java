package org.cfg4j.source.resolve;

import java.util.Map;


/**
 * Property resolver allows to do post-processing properties from ConfigurationSource. See {@link ResolvableConfigurationSource}.
 */
public interface PropertiesResolver {

  Map<String, String> resolve(Map<String, String> sourceProperties);
}
