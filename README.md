[![GitHub license](https://img.shields.io/github/license/nort/config.svg)](https://github.com/nort/config/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/pl.nort/config.svg)]()
[![Travis](https://img.shields.io/travis/nort/config.svg)](https://travis-ci.org/nort/config)

# Overview
Nort Config is a **web services-oriented configuration library**. It's very simple to use yet offers a comprehensive set of features:
* Distributed environment support:
    * Configuration reloading (periodical and push)
    * Re-try on network failures
    * Configuration caching
    * Multiple configuration sources wit fallback mechanism
    * Multi-environment support (e.g. testing, preprod, prod-colo1, prod-colo2)
* Adapters for multiple configuration stores
    * Git repository (with Github as a configuration editor) - read more about this powerful solution in [this article]().
    * Consul 
    * MySQL
    * Files (YAML, Properties, XML)
* Easy yet flexible configuration management:
    * Configuration chaining
    * Multiple source merging
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
  compile group: "pl.nort", name:"config", version: "1.0.0"
}
```

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>pl.nort</groupId>
    <artifactId>config</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

## Quick start
The fastest way to start working with Nort Config is to use a Git repository as configuration store. To do that follow the steps:

1. Fork the [configuration sample repository](https://github.com/nort/config-git-sample-config) (or create your own - it contains just one file).
2. Add your configuration to the *application.properties* file and commit the changes.
3. Use the following code in your application to connect to this source:
```Java
import pl.nort.config.provider.ConfigurationProvider;
import pl.nort.config.provider.ConfigurationProviders;

public class NortConfigPoweredApplication {

  public static void main(String... args) {
    // Change the link below to point to your fork
    ConfigurationProvider configurationProvider = ConfigurationProviders.backedByGit("https://github.com/nort/config-git-sample-config.git");

    String someSetting = configurationProvider.getProperty("some.setting");
    // or
    MyLiveConfigurationPOJO configuration = configurationProvider.bind("my.changing.setting", MyLiveConfigurationPOJO.class);
  }

}
```

## Detailed documentation
Head to [the documentation](https://github.com/pages/nort/config).

# License
Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)

Licensed under the Apache License, Version 2.0. See LICENSE file.
