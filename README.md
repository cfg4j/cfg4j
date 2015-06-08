[![GitHub license](https://img.shields.io/github/license/cfg4j/cfg4j.svg)](https://github.com/cfg4j/cfg4j/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/org.cfg4j/cfg4j.svg)](http://search.maven.org/#search|ga|1|org.cfg4j.cfg4j)
[![Travis](https://img.shields.io/travis/cfg4j/cfg4j.svg)](https://travis-ci.org/cfg4j/cfg4j)

# Overview
**cfg4j** ("Configuration for Java") is a **web service-oriented configuration library for Java**. It's very simple to use yet offers a comprehensive set of features:
* Distributed environment support:
    * Runtime configuration reload (periodical, push and custom)
    * Caching
    * Support for multi-tenant configuration sources (e.g. keep configuration for all your environments [test, preprod, prod] in one store)
    * Handle network failures (e.g. re-try, fallback to another source)
* Adapters for multiple configuration stores
    * Git repository - read more about this powerful solution in [this article]().
    * Consul (WIP)
    * ZooKeeper (WIP)
    * MySQL (WIP)
    * Files (YAML, Properties, XML)
* Easy yet flexible configuration management:
    * Merge configurations from different sources
    * Validation
    * POJO configuration objects binding
* Modern design
    * Well documented
    * Heavily tested
    * Dependency Injection-friendly

# Usage

## Setting up dependency
### Gradle
```groovy
dependencies {
  compile group: "org.cfg4j", name:"cfg4j", version: "3.0.0"
}
```

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>org.cfg4j</groupId>
    <artifactId>cfg4j</artifactId>
    <version>3.0.0</version>
  </dependency>
</dependencies>
```

## Detailed documentation
Head to [the documentation](http://cfg4j.org).

## Quick start
The fastest way to start working with cfg4j is to use a Git repository as a configuration store. To do that follow the steps:

1. Fork the [configuration sample repository](https://github.com/cfg4j/cfg4j-git-sample-config) (or create your own - it contains just one file).
2. Add your configuration to the *application.properties* file and commit the changes.
3. Use the following code in your application to connect to this source:
```Java
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviders;

public class Cfg4jPoweredApplication {

  public static void main(String... args) {
    // Change the link below to point to your fork
    ConfigurationProvider configurationProvider = ConfigurationProviders.backedByGit("https://github.com/cfg4j/cfg4j-git-sample-config");

    // Access config directly
    Integer someSetting = configurationProvider.getProperty("some.setting", Integer.class);
    
    // Wide-range of collections
    List<Boolean> otherSetting = configurationProvider.getProperty("some.setting", new GenericType<List<Boolean>>() {});
    
    // You can also define a configuration object and bind it (it will auto update when configuration changes)
    public interface ConfigPojo {
        Integer someSetting();
        List<Boolean> otherSetting();
    }
    
    ConfigPojo config = configurationProvider.bind("", ConfigPojo.class);
  }

}
```
# License
Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)

Licensed under the Apache License, Version 2.0. See LICENSE file.
