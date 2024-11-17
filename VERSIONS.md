# Version Support & Release Strategy

ChafficLib follows semantic versioning (MAJOR.MINOR.PATCH) and provides Long Term Support (LTS) for selected versions.

## Current Versions

| Version | Release Date | Support Status | Minecraft Versions | End of Life |
|---------|--------------|----------------|-------------------|-------------|
| 1.0.x   | 2025-Q2      | LTS            | 1.20.x - 1.21.x  | 2026-Q1     |
| 0.0.x   | 2025-Q1      | Development    | 1.20.x - 1.21.x  | N/A         |

## Release Types

### Long Term Support (LTS)
- Released every 12 months
- Security updates for 12 months
- Bug fixes for 12 months
- No breaking changes
- Recommended for production use

### Regular Releases
- Released every 3 months
- Security updates for 3 months
- Bug fixes for 3 months
- May contain breaking changes
- Recommended for development

### Development Releases
- Released as needed
- No guaranteed support
- May contain breaking changes
- Not recommended for production

## Upgrade Guide

### 0.0.x to 1.0.0
- Initial release
- No migration needed

## End of Life Policy
- LTS versions receive security updates for 12 months
- Regular releases receive security updates for 3 months
- End of Life (EOL) dates are firm
- Users should upgrade before EOL date
- No support after EOL

## Version Matrix

### Minecraft Version Support

| ChafficLib Version | MC 1.20.x | MC 1.21.x |
|-------------------|------------|------------|
| 1.0.x            | ✅         | ✅         |
| 0.0.x            | ✅         | ✅         |

### Java Version Support

| ChafficLib Version | Java 17 | Java 21 |
|-------------------|---------|---------|
| 1.0.x            | ❌      | ✅      |
| 0.0.x            | ❌      | ✅      |

## Release Schedule 2024

| Version | Type     | Release Date | EOL Date   |
|---------|----------|--------------|------------|
| 1.0.0   | LTS      | 2024-Q1     | 2025-Q1   |
| 1.1.0   | Regular  | 2024-Q2     | 2024-Q3   |
| 1.2.0   | Regular  | 2024-Q3     | 2024-Q4   |
| 1.3.0   | Regular  | 2024-Q4     | 2025-Q1   |
| 2.0.0   | LTS      | 2025-Q1     | 2026-Q1   |