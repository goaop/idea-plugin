<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# intellij-idea-goaop-plugin Changelog

## [Unreleased]

Support for [Go! AOP Framework 4.0](https://github.com/goaop/framework/blob/master/UPGRADE-4.0.md).

### Added
- `matchReturningByReference()` pointcut, with completion and highlighting
- Modifier groups: `final public|protected` means final AND (public OR protected)
- `readonly`, `private(set)` and `protected(set)` modifiers for property pointcuts
- Union (`A|B`), intersection (`A&B`) and DNF return-type patterns for methods and functions, `?Foo` nullable marker
- `|` alternatives inside a class name part: `within(App\Cart|Order)`
- Named pointcuts of other aspects (`Aspect\Class->pointcutName`) and inherited `$this->pointcutName` references: navigation, rename, line markers and an error for unknown pointcuts
- `#[DeclareParents]` introductions and `initialization()`/`staticinitialization()` pointcuts navigate to the selected classes, with the introduction gutter icon
- Pointcut errors with migration hints: removed `dynamic()` (with a quick-fix to `execution(... ->__call(*))`) and `cflowbelow()` pointcuts, the removed `?` wildcard, a second group of `|` modifier alternatives, function patterns without a namespace
- Warnings for `access()` pointcuts that Go! AOP never intercepts: static (`Foo::$property`) and readonly properties
- Inspection *Invalid advice method*: advice methods must be public (quick-fix: make public), and a method carries at most one advice attribute
- Inspection *Attribute removed in Go! AOP 4.0*: `#[Aspect]` (quick-fix: remove) and `#[DeclareError]`
- Inspection *Advised method returns by reference*: such methods can not be woven, exclude them with `&& !matchReturningByReference()`
- The plugin declares support for the `goaop/framework` Composer package, so that the IDE can suggest it for projects requiring the framework; the README explains how to mark the plugin as required for a project ([#93](https://github.com/goaop/idea-plugin/issues/93))

### Changed
- Pointcut matching follows the Go! AOP 4.0 semantics: `*` also matches an empty string (`set*` matches `set`), `Foo+` matches `Foo` itself, classes using the trait `Foo` and every interface in the hierarchy, and `@execution`/`@access`/`@within` match subclasses of the attribute
- The pointcut language is injected only into the expression of Go! AOP attributes (`Before`, `After`, `AfterThrowing`, `Around`, `Pointcut`, `DeclareParents`, positional or `expression:` argument) and into the first argument of `PointcutBuilder` calls; other string arguments, like the interface name of `#[DeclareParents]`, are left alone
- `$this->` completion lists only `#[Pointcut]` methods, inherited ones included
- Line markers skip the properties Go! AOP 4.0 never intercepts: static, readonly and already-hooked properties, and class constants
- Go! AOP 4.0 proxies and `*OriginalTrait` traits from the AOP cache are hidden from completion and navigation

### Removed
- The `?` single-character wildcard in name patterns
- The index of attributed elements, which is no longer needed

### Fixed
- `final` in a modifier pattern broke the compiled pointcut
- `execution(...) && !matchInherited()` matched nothing
- `@within(Attr)` also matched a class whose name is a prefix of an attributed class (`Foo` when only `FooBar` had the attribute)
