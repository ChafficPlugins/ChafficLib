# Installation

ChafficLib can be included in your project using either Gradle or Maven.

## Requirements

- Java 21 or higher
- Spigot 1.20.x - 1.21.x
- Gradle or Maven build system

## Adding the Dependency

### Gradle (Kotlin DSL)

Add the following to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.chafficplugins:ChafficLib:VERSION")
}
```

### Maven

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.chafficplugins</groupId>
        <artifactId>ChafficLib</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

## Verifying Installation

To verify that ChafficLib is properly installed:

1. Build your project
2. Check that ChafficLib is included in your plugin's dependencies
3. Test loading your plugin on a Spigot server

## Next Steps

- Check out the [Getting Started Guide](getting-started.md)
- View the [API Documentation](../api/overview.md)