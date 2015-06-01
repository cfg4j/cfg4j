[![GitHub license](https://img.shields.io/github/license/nort/config.svg)](https://github.com/nort/config/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/pl.nort/config.svg)]()
[![Travis](https://img.shields.io/travis/nort/config.svg)](https://travis-ci.org/nort/config)

# Overview
Nort Config is a **web services-oriented configuration library**. It's very simple to use yet offers a comprehensive set of features:
* Distributed environment support:
    * Configuration reloading (periodical and push)
    * Re-try on network failures
    * Configuration caching
    * Multiple configuration sources
* Adapters for multiple configuration stores
    * Git repository (with Github as a configuration editor) - read more about this powerful solution in [this article]().
    * Consul 
    * MySQL
    * Files (YAML, Properties, XML)
* Easy yet flexible configuration management:
    * Configuration chaining
    * Multiple source merging
    * Validation
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

# License
Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)

Licensed under the Apache License, Version 2.0. See LICENSE file.
