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

### Source roots

- `src/main/java/com/aopphp/go/` — the GrammarKit grammar, the generated lexer/parser/PSI, and small Java PSI helpers (`psiutil/`)
- `src/main/kotlin/com/aopphp/go/` — all hand-written plugin code (Kotlin)
- `src/main/resources/META-INF/plugin.xml` — the plugin descriptor (id `com.aopphp.go.framework`), lists all extensions
- `src/main/resources/inspectionDescriptions/` — one HTML description per inspection short name

### Custom language pipeline

The plugin defines a custom language called **"Go! AOP Pointcut query"**, following the `PointcutGrammar`/`PointcutLexer`
of the Go! AOP framework (`src/Aop/Pointcut/` in goaop/framework, currently 4.0):

1. `parser/pointcut.bnf` — GrammarKit BNF grammar (source of truth for the language)
2. `parser/PointcutLexer.flex` — JFlex lexer definition
3. `parser/PointcutLexer.java` / `PointcutParser.java` and `psi/` — **generated** files; do not edit directly
4. `PointcutQueryParserDefinition` — wires lexer + parser into IntelliJ's language framework

After changing the grammar, regenerate the lexer/parser (PointcutPsiUtil must be compiled first; the configuration-cache
error printed at the end is harmless, the files are generated):

```bash
./gradlew compileJava compileKotlin regenerateParser generateLexer
```

`dynamic(...)` and `cflowbelow(...)` were removed in Go! AOP 4.0 but are still parsed, so that `PointcutAnnotator`
can explain the migration.

### Key extension points

| Component         | Class                                                             | Purpose                                                                       |
|-------------------|-------------------------------------------------------------------|-------------------------------------------------------------------------------|
| File type         | `GoAopFileType`                                                   | Registers `.goaop` files                                                      |
| Language injector | `PointcutQueryLanguageInjector`                                   | Injects pointcut language into the expression of `Go\Lang\Attribute\*` attributes and `PointcutBuilder` calls (`GoAopAttributes.isPointcutExpression`) |
| Language injector | `PhpDealAssertInjector`                                           | Injects PHP into Php-Deal assertions                                          |
| Completion        | `PointcutCompletionContributor` → `completion/`                   | Keywords, modifiers, classes, members, attributes, `$this->` pointcuts        |
| Line markers      | `AdvisedElementsLineMarkerProvider` / `AdvisorLineMarkerProvider` | Navigation between advices/introductions and advised PHP elements             |
| Index             | `AttributePointcutExpressionIndex`                                | Compiled pointcut of every aspect member (Java-serialized: bump `getVersion()` when pointcut classes change) |
| Annotators        | `AttributeAnnotator` / `PointcutAnnotator`                        | Attribute class checks; Go! AOP 4.0 errors and migration hints in pointcuts   |
| Inspections       | `inspection/`                                                     | PHP-side aspect rules: public advices, one advice per method, removed attributes, advised by-reference methods |
| References        | `reference/`                                                      | Navigation, Find Usages and Rename for classes, members and named pointcuts   |

### Pointcut engine (`pointcut/`)

Kotlin implementation of the framework's pointcut matching over PHP PSI. `PointcutCompiler` turns the PSI into a tree
of composites (`AndPointcut`, `OrPointcut`, `NotPointcut`) over leaf pointcuts (`SignaturePointcut`, `AttributePointcut`,
`TruePointcut`, `MatchInheritedPointcut`, `MatchReturningByReferencePointcut`, `PointcutReferencePointcut`,
`IntroductionPointcut`); the `PointFilter` hierarchy (`ModifierMatcherFilter`, `ReturnTypeFilter`,
`InheritanceClassFilter`...) matches classes and modifiers. Keep the semantics in line with the framework classes of the
same name. Members are matched in the class that declares them; `PointcutAdvisor` runs the matching for line markers.

### Plugin dependencies

The plugin requires the following another plugin at runtime:
- `com.jetbrains.php` — PHP language support
