# Getting Started with ChafficLib

ChafficLib is a modern Spigot library designed to streamline plugin development. This guide will help you get started with using ChafficLib in your projects.

## Requirements

- Java 21 or higher
- Spigot 1.20.x - 1.21.x
- Gradle or Maven build system

## Installation

### Gradle (Kotlin DSL)
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

## Basic Usage

More examples and detailed API documentation will be added as features are implemented.

## Next Steps

- Check out our [API Reference](../api/overview.md)
- View examples in the [GitHub repository](https://github.com/chafficplugins/ChafficLib)
- Join our [Discord](https://discord.gg/RPZBhB4rna) for support