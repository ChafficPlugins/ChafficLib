# ChafficLib

[![Build Status](https://github.com/chafficplugins/ChafficLib/actions/workflows/build.yml/badge.svg)](https://github.com/chafficplugins/ChafficLib/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/chafficplugins/ChafficLib/branch/main/graph/badge.svg)](https://codecov.io/gh/chafficplugins/ChafficLib)
[![License](https://img.shields.io/github/license/chafficplugins/ChafficLib)](LICENSE)
[![Release](https://img.shields.io/github/v/release/chafficplugins/ChafficLib)](https://github.com/chafficplugins/ChafficLib/releases)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20--1.21-brightgreen.svg)](https://www.spigotmc.org/resources/chafficlib.XXXXX/)
[![Documentation](https://img.shields.io/badge/documentation-gitbook-blue.svg)](https://chafficplugins.gitbook.io/chafficlib/)

A modern, comprehensive Spigot library designed to streamline plugin development while maintaining high standards for documentation, testing, and compatibility.

## Features

- 🎮 Intuitive GUI System
- ⚙️ Type-safe Configuration
- 💾 Database Abstraction
- 🛠️ Item Builder System
- ✨ Effects & Particles
- 📝 Event DSL
- 🎯 Command Framework
- 📊 Statistics System

## Getting Started

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

## Documentation

Visit our [GitBook documentation](https://chafficplugins.gitbook.io/chafficlib/) for:
- Getting Started Guide
- API Reference
- Examples & Tutorials
- Best Practices
- Migration Guides

## Version Support

We follow [Semantic Versioning](https://semver.org/). Check [VERSIONS.md](VERSIONS.md) for:
- Supported Minecraft versions
- LTS releases
- End-of-life dates
- Migration guides

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Requirements for Pull Requests
- 80% code coverage minimum
- Complete documentation for public APIs
- Passing CI checks
- Following our [coding standards](CONTRIBUTING.md)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 🌐 [Website](https://felixbeinssen.net)
- 📚 [Documentation](https://chafficplugins.gitbook.io/chafficlib/)
- 💬 [Discord](https://discord.gg/XXXXX)
- 🐛 [Issue Tracker](https://github.com/chafficplugins/ChafficLib/issues)