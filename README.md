# Go! AOP Framework — PhpStorm Plugin

> First-class Aspect-Oriented Programming support for PHP, right inside your IDE.

[![GitHub release](https://img.shields.io/github/release/goaop/idea-plugin.svg)](https://github.com/goaop/idea-plugin/releases/latest)
[![License](https://img.shields.io/github/license/goaop/idea-plugin.svg)](https://github.com/goaop/idea-plugin)
[![Build](https://github.com/goaop/intellij-idea/workflows/Build/badge.svg)](https://github.com/goaop/intellij-idea/actions)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.aopphp.go.framework.svg)](https://plugins.jetbrains.com/plugin/com.aopphp.go.framework)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.aopphp.go.framework.svg)](https://plugins.jetbrains.com/plugin/com.aopphp.go.framework)

<!-- Plugin description -->
## Go! AOP Framework Plugin

Bring the full power of **Aspect-Oriented Programming** to your PHP workflow. This plugin integrates the [Go! AOP Framework](https://github.com/goaop/framework) deeply into PhpStorm and IntelliJ IDEA with a dedicated language engine for pointcut expressions — so your cross-cutting concerns get the same IDE love as the rest of your code.

### What's inside

- **Pointcut Language Engine** - A complete custom language implementation for Go! AOP pointcut expressions — with a full lexer, parser, and PSI tree. Write pointcuts with confidence: syntax errors are caught instantly, before you ever run your application.

 - **Smart Code Completion** - Auto-complete pointcut keywords, visibility modifiers, PHP class names, method names, and PHP attributes exactly where they are valid. The completion popup triggers automatically as you type inside pointcut expressions.

 - **Syntax Highlighting & Color Customization** - Pointcut expressions are highlighted with dedicated token colors. Adjust every color to match your theme via **Settings > Editor > Color Scheme > Go! AOP Pointcut**.

 - **Line Markers & Bi-directional Navigation** - Jump from any **advice method** directly to the PHP elements it intercepts and from any **advised class or method** back to all matching advices. Gutter icons make the AOP wiring visible at a glance — no more hunting through aspect classes.

 - **Language Injection** - Pointcut expressions inside `Go\Lang\Attribute\*` PHP attributes are automatically recognized as the pointcut language, giving you full IDE support without leaving your PHP file.

 - **Attribute Validation** - Invalid PHP attributes used in `@access`, `@execution`, and `@within` pointcuts are flagged inline with actionable error messages — catch mistakes at edit time, not at runtime.

 - **Find Usages & Rename Refactoring** - PHP class names and member names referenced inside pointcut expressions participate in **Find Usages** and **Rename** refactoring. Rename a class or method and all pointcut references update automatically.

 - **File-based Indexes** - Lightning-fast cross-file lookups power navigation and line markers even in large codebases, with a small performance penalty on project indexing.

 - **Standalone `.goaop` File Support** - Work with pointcut expressions in standalone `.goaop` files with full language support — useful for documentation, snippets, and tooling integration.
<!-- Plugin description end -->

---

## Installation

**From the JetBrains Marketplace (recommended)**

<kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > search **"Go! AOP"** > <kbd>Install</kbd>

Or install directly: [plugins.jetbrains.com/plugin/7785](https://plugins.jetbrains.com/plugin/7785)

**Manually**

Download the [latest release](https://github.com/goaop/intellij-idea-goaop-plugin/releases/latest) and install via:

<kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>⚙</kbd> > <kbd>Install Plugin from Disk…</kbd>

## Development

### Build & Run

```bash
# Launch a sandboxed IDE with the plugin loaded
./gradlew runIde

# Build the distributable ZIP
./gradlew buildPlugin

# Verify compatibility across IDE versions
./gradlew runPluginVerifier
```

### Testing

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.aopphp.go.SomeTestClass"

# Run tests with code coverage
./gradlew test koverXmlReport
```

Coverage report: `build/reports/kover/report.xml`

### Test Structure

| Package       | Description                                                               |
|---------------|---------------------------------------------------------------------------|
| `parser/`     | Lexer and parser tests (`LexerTestCase` / `ParsingTestCase`)              |
| `pointcut/`   | AOP pointcut matching logic (And/Or/Not, Signature, Attribute…)           |
| `util/`       | Utility class tests (`PhpClassUtil`, `PluginUtil`, `AttributeTargetUtil`) |
| `completion/` | IDE integration tests for keyword and modifier completion                 |
| `injector/`   | IDE integration tests for language injection into PHP attributes          |

Test fixtures (`.goaop` files) live in `src/test/testdata/parser/`.

---

Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
