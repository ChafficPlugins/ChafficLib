# Contributing to ChafficLib

Thank you for your interest in contributing to ChafficLib! This document outlines our standards and processes for contributing.

## Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Branch Strategy](#branch-strategy)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Documentation Requirements](#documentation-requirements)

## Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Help others learn and grow
- Report unacceptable behavior to maintainers

## Branch Strategy

### Branch Naming Convention

```
type/issue-id-short-description
```

#### Types
- `feature/` - New features
- `bugfix/` - Bug fixes
- `hotfix/` - Critical fixes for production
- `release/` - Release preparation
- `docs/` - Documentation updates
- `test/` - Test additions or modifications
- `refactor/` - Code refactoring
- `chore/` - Maintenance tasks

#### Examples
- `feature/GUI-123-add-pagination`
- `bugfix/DB-456-fix-connection-leak`
- `docs/DOC-789-update-api-docs`

### Branch Hierarchy
```
main
  └── develop
      ├── feature/...
      ├── bugfix/...
      └── release/...
```

- `main` - Production code
- `develop` - Integration branch
- Feature/bugfix branches branch off from `develop`
- Hotfixes branch off from `main`

## Commit Messages

### Format
```
type(scope): short description

[optional body]

[optional footer]
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code changes that neither fix bugs nor add features
- `perf`: Performance improvements
- `test`: Adding or modifying tests
- `chore`: Maintenance tasks

### Scope
Specifies the component affected:
- `gui`
- `db`
- `config`
- `events`
- `cmd`
- `effects`
- `items`
- `stats`

### Examples
```
feat(gui): add inventory pagination system

- Implemented PageableInventory class
- Added navigation buttons
- Created ItemSlotCalculator utility

Closes #123
```

```
fix(db): resolve connection pool memory leak

Connection wasn't properly closed in transaction rollback.
Added finally block to ensure proper cleanup.

Fixes #456
```

## Pull Request Process

1. Create feature/bugfix branch from `develop`
2. Make changes following coding standards
3. Ensure tests pass and coverage meets 80%
4. Update documentation
5. Submit PR to `develop`
6. Wait for review
7. Address feedback
8. Merge after approval

### PR Title Format
```
type(scope): short description (#issue-number)
```

Example: `feat(gui): implement inventory pagination (#123)`

## Coding Standards

### Kotlin Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use Kotlin idioms (e.g., `when`, `let`, `apply`)
- Prefer immutability (`val` over `var`)

### Package Structure
```
de.chafficplugins.chafficlib/
├── gui/
├── config/
├── database/
├── items/
├── effects/
├── events/
├── commands/
└── util/
```

### Class Structure
```kotlin
class MyClass {
    // Constants
    companion object {
        private const val CONSTANT = "value"
    }

    // Properties
    private val property: Type

    // Constructors
    init {
        // Initialization
    }

    // Public methods
    fun publicMethod() {
        // Implementation
    }

    // Private methods
    private fun helperMethod() {
        // Implementation
    }
}
```

### Naming Conventions
- Classes: PascalCase (`InventoryBuilder`)
- Functions: camelCase (`createInventory`)
- Properties: camelCase (`playerData`)
- Constants: SCREAMING_SNAKE_CASE (`MAX_INVENTORY_SIZE`)
- Files: PascalCase matching class name (`InventoryBuilder.kt`)

### Method Length
- Methods should be 30 lines or less
- Break down complex methods into smaller ones
- Use meaningful method names

### Documentation
- Use KDoc for all public APIs
- Include code examples for complex features
- Document assumptions and edge cases

## Testing Guidelines

### Test Structure
```kotlin
class MyClassTest {
    // Setup
    @BeforeEach
    fun setup() {
        // Test setup
    }

    // Tests grouped by functionality
    @Nested
    inner class FeatureTests {
        @Test
        fun `should do something when condition`() {
            // Test implementation
        }
    }
}
```

### Test Naming
Format: `should_expectedBehavior_when_condition`

Example: `should_throwException_when_inventoryIsFull`

### Coverage Requirements
- Minimum 70% code coverage
- 100% coverage for critical components
- Test all edge cases
- Include both positive and negative tests

## Documentation Requirements

### API Documentation
- Every public API must have KDoc
- Include:
    - Function purpose
    - Parameters
    - Return values
    - Exceptions
    - Example usage

### Example
```kotlin
/**
 * Creates a paginated inventory with navigation buttons.
 *
 * @param title The inventory title
 * @param size The size of each page (must be multiple of 9)
 * @param items The items to display
 * @throws IllegalArgumentException if size is not multiple of 9
 * @return The created inventory
 *
 * @example
 * val inventory = createPaginatedInventory(
 *     title = "My Items",
 *     size = 54,
 *     items = itemList
 * )
 * ```
*/
fun createPaginatedInventory(
title: String,
size: Int,
items: List<ItemStack>
): Inventory
```

### Changelog
- Maintain CHANGELOG.md
- Follow [Keep a Changelog](https://keepachangelog.com/)
- Update for each release

## Release Process

1. Create release branch (`release/vX.Y.Z`)
2. Update version numbers
3. Update CHANGELOG.md
4. Create PR to `main`
5. After merge, tag release
6. Merge back to `develop`