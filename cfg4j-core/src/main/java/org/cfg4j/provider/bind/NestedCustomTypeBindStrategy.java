package org.cfg4j.provider.bind;

import org.cfg4j.provider.ConfigurationProvider;

import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

public class NestedCustomTypeBindStrategy extends PrefixBasedBindStrategy{

  private final String namespace;

  public NestedCustomTypeBindStrategy(String namespace) {
    requireNonNull(namespace);
    this.namespace = namespace;
  }

  public boolean canApply(Method method) {
    Package aPackage = method.getReturnType().getPackage();
    return aPackage !=null && aPackage.getName().startsWith(namespace);
  }

  public Object apply(Method method, String prefix, ConfigurationProvider configurationProvider) {
    return configurationProvider.bind(buildPrefix(prefix, method.getName()), method.getReturnType());
  }

}
