package org.cfg4j.source.cached;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.reload.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedConfigurationSource implements ConfigurationSource, Reloadable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedConfigurationSource.class);

    private final ConfigurationSource delegate;
    private final AtomicReference<Properties> properties;
    private final ThreadLocal<Properties> wrapper;
    private final Environment environment;

    public CachedConfigurationSource(ConfigurationSource delegate, Environment environment) {
        this.delegate = delegate;
        this.environment = environment;
        this.properties = new AtomicReference<>(new Properties()); // never empty, no NPE
        this.wrapper = new ThreadLocal<>();
    }

    @Override
    public void reload() {
        try {
            Properties props = new Properties();
            props.putAll(properties.get());
            Properties newProps = delegate.getConfiguration(this.environment);
            props.putAll(newProps);
            properties.set(props);
            LOGGER.debug("Reload properties : {}", properties);
        } catch (Exception e) {
            LOGGER.error("Error reloading properties from delegate source : keep old properties", e);
        }
    }

    @Override
    public Properties getConfiguration(Environment environment) {
        // Get always same properties in same thread
        Properties props = wrapper.get();
        if (props == null) {
            props = properties.get();
            wrapper.set(props);
        }
        return props;
    }

    @Override
    public void init() {
        delegate.init();
        Properties props = delegate.getConfiguration(environment);
        properties.set(props);
    }

}
