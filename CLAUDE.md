# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **PhpStorm/IntelliJ IDEA plugin** that provides IDE support for the [Go! AOP Framework](https://github.com/goaop/framework) — an aspect-oriented programming (AOP) library for PHP. The plugin adds pointcut syntax highlighting, parsing, code completion, navigation, and language injection into PHP Doctrine annotations.

## Build Commands

```bash
# Build the plugin ZIP
./gradlew buildPlugin

# Launch a sandboxed IDE instance with the plugin loaded (primary dev workflow)
./gradlew runIde

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.aopphp.go.SomeTestClass"

# Verify plugin compatibility across IDE versions
./gradlew runPluginVerifier

# Run Qodana static analysis
./gradlew qodanaScan
```

## Architecture

### Dual source roots

The project has two source roots — a legacy Java root and a modern Kotlin root from the IntelliJ Platform Plugin Template:

- `src/com/aopphp/go/` — **all the real plugin code** (Java), organized around the Go! AOP feature set
- `src/main/kotlin/com/github/lisachenko/intellijideagoaopplugin/` — template scaffolding (MyBundle, MyProjectService, MyToolWindowFactory) that is mostly unused

There are also **two** `plugin.xml` files:
- `META-INF/plugin.xml` — the **active** plugin descriptor (id `com.aopphp.go.framework`), lists all extensions
- `src/main/resources/META-INF/plugin.xml` — the template stub (id `com.github.lisachenko.intellijideagoaopplugin`), not the active one

### Custom language pipeline

The plugin defines a custom language called **"Go! AOP Pointcut query"**:

1. `parser/pointcut.bnf` — GrammarKit BNF grammar (source of truth for the language)
2. `parser/PointcutLexer.flex` — JFlex lexer definition
3. `parser/PointcutLexer.java` / `PointcutParser.java` — **generated** files; do not edit directly
4. `PointcutQueryParserDefinition` — wires lexer + parser into IntelliJ's language framework
5. `psi/` — 55+ PSI element types and their `impl/` implementations (generated from the BNF)

When modifying the grammar, regenerate the lexer/parser via GrammarKit (Run > Generate Parser Code in IntelliJ).

### Key extension points

| Component         | Class                                                                 | Purpose                                                               |
|-------------------|-----------------------------------------------------------------------|-----------------------------------------------------------------------|
| File type         | `GoAopFileTypeFactory`                                                | Registers `.goaop` files                                              |
| Language injector | `PointcutQueryLanguageInjector`                                       | Injects pointcut language into `Go\Lang\Annotation\*` PHP annotations |
| Language injector | `PhpDealAssertInjector`                                               | Injects PHP into Php-Deal assertions                                  |
| Completion        | `PointcutCompletionContributor` → `completion/`                       | Keywords, modifiers, Doctrine annotations                             |
| Line markers      | `AdvisedElementsLineMarkerProvider` / `AdvisorLineMarkerProvider`     | Navigation between advice and advised PHP elements                    |
| Indexes           | `AnnotationPointcutExpressionIndex` / `AnnotatedPhpNamedElementIndex` | File-based indexes for fast cross-file lookups                        |
| Annotator         | `DoctrineAnnotator`                                                   | Highlights invalid Doctrine annotation usages in pointcuts            |

### Pointcut engine (`pointcut/`)

Pure Java implementation of AOP pointcut matching — no IDE dependencies. Composite pattern: `AndPointcut`, `OrPointcut`, `NotPointcut` wrap leaf types (`SignaturePointcut`, `AnnotationPointcut`, `AccessPointcut`, `ExecutionPointcut`, `WithinPointcut`). The `PointFilter` hierarchy mirrors this for member-level matching.

### Plugin dependencies

The plugin requires the following another plugin at runtime:
- `com.jetbrains.php` — PHP language support
