# TXCore

### Table of Contents
- [Description](#description)
- [Installation](#installation)
- [Usage](#usage)

### Description
- This library contains all the core functionalities and utilities for all the other TX libraries.
- It provides a singleton configuration class `TXConfig` that holds the application name, version, and other configurations.

### Installation
You can install the package with maven by adding the following dependency to your `pom.xml`:

```xml
<dependency>
	<groupId>dev.wassim-tahraoui</groupId>
	<artifactId>tx-core</artifactId>
	<version>1.0.3</version>
</dependency>
```

### Usage
You can use the library in your Java code by importing TXConfig and using it as a singleton.<br>For example:
```java
import com.tahraoui.txcore.TXConfig;

private  void someMethod() {
	var config = TXConfig.getInstance(); // Gets or creates the singleton instance of TXConfig
	
	System.out.println(config.getAppName()); // prints the application name
	System.out.println(config.getAppProjectName()); // prints the lowercase hyphenated application name
	System.out.println(config.getAppVersion()); // prints the application version
	// ...
}
```

The properties of `TXConfig` can be set using `application.properties` file in your `resources` directory.
Here is a table of the available properties:

| Property              | Default Value   | Description                                    |
|-----------------------|-----------------|------------------------------------------------|
| app.name              | (none)          | The name of the application.                   |
| app.version           | (none)          | The version of the application.                |
| app.author            | (none)          | The author of the application.                 |
| tx-core.debug.enabled | true            | Logging activity status.                       |
| tx-core.debug.level   | DEBUG           | Logging level (`DEBUG`, `INFO`, `WARN`, etc.). |

_*Properties with `(none)` as default value are required and must be set._

**Example:**
```properties
app.name=MyApp
app.version=1.0.0
app.author=John Doe
tx-core.debug.enabled=true
tx-core.debug.level=DEBUG
```