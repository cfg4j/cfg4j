[![GitHub license](https://img.shields.io/github/license/cfg4j/cfg4j.svg)](https://github.com/cfg4j/cfg4j/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/org.cfg4j/cfg4j.svg)](http://search.maven.org/#search|ga|1|org.cfg4j.cfg4j)
[![Travis](https://img.shields.io/travis/cfg4j/cfg4j.svg)](https://travis-ci.org/cfg4j/cfg4j)

# Overview
**cfg4j** ("Configuration for Java") is a **distributed apps-oriented configuration library for Java**. It's very simple to use
 yet offers a comprehensive set of features:
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

## Sample apps
Explore the code of the [sample apps](https://github.com/cfg4j/cfg4j-sample-apps).

## Detailed documentation
Head to [the documentation](http://cfg4j.org).

## Quick start
### Setting up dependency
#### Gradle
```groovy
dependencies {
  compile group: "org.cfg4j", name:"cfg4j", version: "3.0.0"
}
```

#### Maven
```xml
<dependencies>
  <dependency>
    <groupId>org.cfg4j</groupId>
    <artifactId>cfg4j</artifactId>
    <version>3.0.0</version>
  </dependency>
</dependencies>
```

### Usage
The fastest way to start working with cfg4j is to use a Git repository as a configuration store. To do that follow the steps:

* Use the following code in your application to connect to sample configuration source:
```java
public class Cfg4jPoweredApplication {

  // Change this interface to whatever you want
  public interface MyConfigInterface {
      Integer someSetting();
      List<Boolean> otherSetting();
  }

  public static void main(String... args) {
    ConfigurationProvider configurationProvider =
        ConfigurationProviders.backedByGit("https://github.com/cfg4j/cfg4j-git-sample-config.git");
    
    MyConfigInterface config = configurationProvider.bind("", MyConfigInterface.class);
    
    // Use it!
    System.out.println(config.someSetting);
  }

}
```

* Optional steps
    1. Fork the [configuration sample repository](https://github.com/cfg4j/cfg4j-git-sample-config).
    2. Add your configuration to the "*application.properties*" file and commit the changes.
    3. Update the code above to point to your fork.
    
# License
Licensed under the Apache License, Version 2.0. See LICENSE file.
