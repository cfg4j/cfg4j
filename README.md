[![Twitter Follow](https://img.shields.io/twitter/follow/norbert_potocki.svg?style=social)](https://twitter.com/norbert_potocki)
[![Documentation](https://img.shields.io/badge/documentation-www.cfg4j.org-blue.svg)](http://cfg4j.org)
[![Examples](https://img.shields.io/badge/demo-here-blue.svg)](https://github.com/cfg4j/cfg4j-sample-apps)
[![Maven Central](https://img.shields.io/maven-central/v/org.cfg4j/cfg4j-core.svg)](http://search.maven.org/#search|ga|1|org.cfg4j.cfg4j-core)
[![JavaDoc](https://img.shields.io/badge/javadoc-html-blue.svg)](http://www.javadoc.io/doc/org.cfg4j/cfg4j-core)
[![GitHub license](https://img.shields.io/github/license/cfg4j/cfg4j.svg)](https://github.com/cfg4j/cfg4j/blob/master/LICENSE)
[![Travis](https://img.shields.io/travis/cfg4j/cfg4j/master.svg)](https://travis-ci.org/cfg4j/cfg4j)

# Overview
<img height="140px" align="right" src="https://github.com/cfg4j/cfg4j.github.io/blob/master/images/cfg4j.png">

**cfg4j** ("configuration for Java") is a **configuration library for Java distributed apps** (and more).

#### Features:
* Open source
* **Easy to use**
* **Auto-reloads** configuration
* **Powerful** configuration mechanisms (interface **binding**, **multi-source** support with **fallback** strategy, **merging**, ...)
* **Distributed-environment friendly** ( **caching**, support for **multiple environments** [test, preprod, prod], ...)
* Reads configuration from: **[Consul](https://github.com/cfg4j/cfg4j-sample-apps/tree/master/consul-bind),
 [Git repos (YAML and/or properties)](https://github.com/cfg4j/cfg4j-sample-apps/tree/master/git-bind),
 [Files](https://github.com/cfg4j/cfg4j-sample-apps/tree/master/files-bind),
 [Classpath](https://github.com/cfg4j/cfg4j-sample-apps/tree/master/classpath-bind), ...**
* Modern design
  * Seamless integration with DI containers: **[Spring](https://spring.io/)**, **[Guice](https://github.com/google/guice)** and others
  * Exposes performance metrics by integration with **[Metrics library](http://metrics.dropwizard.io)**
  * Extensible (see the list of **plugins** below)
  * Heavily tested (**99% code coverage**)
  * Well [documented](http://cfg4j.org)
  * Java 8+ required

# Usage
Read [an article about configuration management using cfg4j](http://potocki.io/post/141230472743/configuration-management-for-distributed-systems).

## Detailed documentation
Head to [the documentation](http://cfg4j.org).

## Sample apps
Explore the code of the [sample apps](https://github.com/cfg4j/cfg4j-sample-apps).


## Quick start
### Setting up dependency
#### Gradle
```groovy
dependencies {
  compile group: "org.cfg4j", name:"cfg4j-core", version: "4.4.1"
  
  // For Consul integration
  compile group: "org.cfg4j", name:"cfg4j-consul", version: "4.4.1"
  
  // For git integration
  compile group: "org.cfg4j", name:"cfg4j-git", version: "4.4.1"
}
```

#### Maven
```xml
<dependencies>
  <dependency>
    <groupId>org.cfg4j</groupId>
    <artifactId>cfg4j-core</artifactId>
    <version>4.4.1</version>
  </dependency>
  <!-- For Consul integration -->
  <dependency> 
    <groupId>org.cfg4j</groupId>
    <artifactId>cfg4j-consul</artifactId>
    <version>4.4.1</version>
  </dependency>
  <!-- For git integration -->
  <dependency>
    <groupId>org.cfg4j</groupId>
    <artifactId>cfg4j-git</artifactId>
    <version>4.4.1</version>
  </dependency>
</dependencies>
```

### Usage
The fastest way to start working with cfg4j is to use a Git repository as a configuration store. To do that follow the steps:

* Use the following code in your application to connect to sample configuration source:
```java
public class Cfg4jPoweredApplication {

  // Change this interface to whatever you want
  public interface SampleConfig {
    Integer birthYear();
    List<String> friends();
    URL homepage();
    Map<String, Character> grades();
  }

  public static void main(String... args) {
    ConfigurationSource source = new GitConfigurationSourceBuilder()
      .withRepositoryURI("https://github.com/cfg4j/cfg4j-git-sample-config.git")
      .build();
      
    ConfigurationProvider provider = new ConfigurationProviderBuilder()
      .withConfigurationSource(source)
      .build();
    
    SampleConfig config = configurationProvider.bind("reksio", SampleConfig.class);
    
    // Use it!
    System.out.println(config.homepage());
  }

}
```

* Optional steps
    1. Fork the [configuration sample repository](https://github.com/cfg4j/cfg4j-git-sample-config).
    2. Add your configuration to the "*application.properties*" file and commit the changes.
    3. Update the code above to point to your fork.
    
# License
Licensed under the Apache License, Version 2.0. See LICENSE file.
