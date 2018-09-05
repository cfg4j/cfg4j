package org.cfg4j.source.cached;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentCachedConfigurationSourceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentCachedConfigurationSourceTest.class);

    private static final String[] TEST_CONFIGURATIONS_FILES = { "/threadSafetyTest/application-1.properties",
                                                                "/threadSafetyTest/application-2.properties",
                                                                "/threadSafetyTest/application-3.properties",
                                                                "/threadSafetyTest/application-4.properties",
                                                                "/threadSafetyTest/application-5.properties",
                                                                "/threadSafetyTest/application-6.properties" };

    private PropX propX;

    private AtomicInteger errorCounter;
    private AtomicInteger changeCounter;

    private CountDownLatch startLatch;
    private CountDownLatch endLatch;

    private Random random;

    private void check() {
        try {
            startLatch.await();
            if (random.nextLong() % 5 == 0L) {
                new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        setNewConfiguration();
                        
                    }
                }).start();
            }
            String error = "";
            int factor = propX.prop1();
            int value = propX.prop2();
            StringBuilder builder = new StringBuilder(Thread.currentThread().getName()).append("\t->\t").append("factor ").append(factor);
            Thread.sleep(WAIT_TIME); // simulate long operation
            builder.append("\t2 * ").append(factor).append(" = ").append(value);
            if (value != 2 * factor) {
                errorCounter.incrementAndGet();
                builder.append("<<<");
                error = "\t ==> ERROR";
            }
            Thread.sleep(WAIT_TIME);
            value = propX.prop3();
            builder.append("\t3 * ").append(factor).append(" = ").append(value);
            if (value != 3 * factor) {
                errorCounter.incrementAndGet();
                builder.append("<<<");
                error = "\t ==> ERROR";
            }
            Thread.sleep(WAIT_TIME);
            value = propX.prop4();
            builder.append("\t4 * ").append(factor).append(" = ").append(value);
            if (value != 4 * factor) {
                errorCounter.incrementAndGet();
                builder.append("<<<");
                error = "\t ==> ERROR";
            }
            Thread.sleep(WAIT_TIME);
            value = propX.prop5();
            builder.append("\t5 * ").append(factor).append(" = ").append(value);
            if (value != 5 * factor) {
                errorCounter.incrementAndGet();
                builder.append("<<<");
                error = "\t ==> ERROR";
            }
            if ( !error.isEmpty()) {
                LOGGER.info(builder.append(error).toString());
            }
        } catch (Exception e) {
            LOGGER.error("Error checking", e);
            errorCounter.incrementAndGet();
        } finally {
            endLatch.countDown();
        }
    }

    private static final int THREAD_COUNT = 20_000;
    private static final int WAIT_TIME = 100;
    private String tmpDir;
    private Object copyLock = new Object();
    private ImmutableEnvironment environment;
    private CachedConfigurationSource cachedSource;

    private Integer currentConfiguration;

    @Before
    public void before() throws Exception {
        tmpDir = System.getProperty("java.io.tmpdir");

        random = new Random();
        errorCounter = new AtomicInteger(0);
        startLatch = new CountDownLatch(1);
        endLatch = new CountDownLatch(THREAD_COUNT);
        changeCounter = new AtomicInteger(0);
        currentConfiguration = 0;

        setNewConfiguration();

        FilesConfigurationSource defaultConfigurationSource = new FilesConfigurationSource();
        environment = new ImmutableEnvironment(tmpDir);
        cachedSource = new CachedConfigurationSource(defaultConfigurationSource, environment);
    }
    
    // We have to get coherent properties with concurrent access
    @Test
    public void concurrentTest() throws Exception {
        ConfigurationProvider provider =
                                       new ConfigurationProviderBuilder().withConfigurationSource(cachedSource)
                                                                         .withEnvironment(environment)
                                                                         .withReloadStrategy(new PeriodicalReloadStrategy(10, TimeUnit.MILLISECONDS))
                                                                         .build();
        propX = provider.bind("", PropX.class);

        for (long i = 0; i < THREAD_COUNT; i++ ) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    check();
                }
            }).start();
        }
        startLatch.countDown();
        endLatch.await();
        LOGGER.info("Configuration changes : " + changeCounter.get());
        LOGGER.info("Errors                : " + errorCounter.get());
        assertThat(errorCounter.get(), is(0));
    }

    private void setNewConfiguration() {
        currentConfiguration++ ;
        String newConfigurationFile = TEST_CONFIGURATIONS_FILES[currentConfiguration % 6];
        InputStream inputStream = getClass().getResourceAsStream(newConfigurationFile);
        String configurationFile = tmpDir + "/application.properties";
        // Avoid to copy file by many threads in same time
        synchronized (copyLock) {
            try {
                Files.copy(inputStream, Paths.get(configurationFile), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        changeCounter.incrementAndGet();
    }

}
