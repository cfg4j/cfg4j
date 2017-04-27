package org.cfg4j.source.cached;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class CachedConfigurationSourceTest {

    private static final String ENVIRONMENT = "environment";
    private static final String KEY_1 = "ABC";
    private static final String KEY_1_VALUE_1 = "123";
    private static final String KEY_1_VALUE_2 = "456";
    private static final String KEY_2 = "DEF";
    private static final String KEY_2_VALUE_1 = "789";
    private static final String KEY_2_VALUE_2 = "321";

    private ConfigurationSource mockSource;
    private Environment environment;
    private CachedConfigurationSource cachedSource;

    @Before
    public void before() throws Exception {
        mockSource = Mockito.mock(ConfigurationSource.class);
        environment = new ImmutableEnvironment(ENVIRONMENT);
        cachedSource = new CachedConfigurationSource(mockSource, environment);
    }

    @Test
    public void propertiesAreLoadedAfterInit() throws Exception {
        Properties props = new Properties();
        props.setProperty(KEY_1, KEY_1_VALUE_1);
        when(mockSource.getConfiguration(Matchers.<Environment> anyObject())).thenReturn(props);

        cachedSource.init();

        Properties properties = cachedSource.getConfiguration(environment);
        assertThat(properties.get(KEY_1)).isEqualTo(KEY_1_VALUE_1);

        verify(mockSource).getConfiguration(eq(environment));
    }

    @Test
    public void doNotAccessToDelegateSource() throws Exception {
        Properties props = new Properties();
        props.setProperty(KEY_1, KEY_1_VALUE_1);
        when(mockSource.getConfiguration(Matchers.<Environment> anyObject())).thenReturn(props);
        cachedSource.init();

        reset(mockSource);

        Properties properties = cachedSource.getConfiguration(environment);
        assertThat(properties.get(KEY_1)).isEqualTo(KEY_1_VALUE_1);

        verify(mockSource, never()).getConfiguration(eq(environment));
    }

    @Test
    public void reloadProperties() throws Exception {
        Properties props = new Properties();
        props.setProperty(KEY_1, KEY_1_VALUE_1);
        when(mockSource.getConfiguration(Matchers.<Environment> anyObject())).thenReturn(props);
        cachedSource.init();
        reset(mockSource);

        Properties props2 = new Properties();
        props2.setProperty(KEY_1, KEY_1_VALUE_2);
        when(mockSource.getConfiguration(Matchers.<Environment> anyObject())).thenReturn(props2);

        cachedSource.reload();

        Properties properties = cachedSource.getConfiguration(environment);
        assertThat(properties.getProperty(KEY_1)).isEqualTo(KEY_1_VALUE_2);
    }

    @Test
    public void mergePropertiesWhenReloadProperties() throws Exception {
        Properties props = new Properties();
        props.setProperty(KEY_1, KEY_1_VALUE_1);
        when(mockSource.getConfiguration(Matchers.<Environment> anyObject())).thenReturn(props);
        cachedSource.init();
        reset(mockSource);

        Properties props2 = new Properties();
        props2.setProperty(KEY_2, KEY_2_VALUE_1);
        when(mockSource.getConfiguration(Matchers.<Environment> anyObject())).thenReturn(props2);

        cachedSource.reload();

        Properties properties = cachedSource.getConfiguration(environment);
        assertThat(properties.getProperty(KEY_1)).isEqualTo(KEY_1_VALUE_1);
        assertThat(properties.getProperty(KEY_2)).isEqualTo(KEY_2_VALUE_1);
    }

    @Test
    public void keepOldPropertiesIfReloadException() throws Exception {
        Properties props = new Properties();
        props.setProperty(KEY_1, KEY_1_VALUE_1);
        when(mockSource.getConfiguration(Matchers.<Environment> anyObject())).thenReturn(props);
        cachedSource.init();
        reset(mockSource);


        cachedSource.reload();

        Properties properties = cachedSource.getConfiguration(environment);
        assertThat(properties.getProperty(KEY_1)).isEqualTo(KEY_1_VALUE_1);

    }

}
