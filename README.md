PhpStorm / Go! AOP Framework integration plugin
-----------------

Provides Go! AOP Framework support for PhpStorm IDE such as pointcut highlighting, analysis, referencing and much more, allowing native feeling of AOP in project.

[![GitHub release](https://img.shields.io/github/release/goaop/idea-plugin.svg)](https://github.com/goaop/idea-plugin/releases/latest)
[![Minimum IDEA Version](http://img.shields.io/badge/IDEA-131-8892BF.svg)](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html)
[![License](https://img.shields.io/github/license/goaop/idea-plugin.svg)](https://github.com/goaop/idea-plugin)

![Build](https://github.com/goaop/intellij-idea-goaop-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

Installation
---------------

Open "Settings > "Plugins", select "Browse Repositories" and type "Go! AOP" or install [plugin](https://plugins.jetbrains.com/plugin/7785) directly.
Install it and [PHP Annotations Plugin](http://plugins.jetbrains.com/plugin/7320), then restart your IDE

Features
----------
  - Go! AOP pointcut syntax highlighting and parsing
  - Analysis of pointcuts and insertion of line markers for navigation to the concrete advice ![IDEA Pointcut analysis](https://raw.githubusercontent.com/goaop/idea-plugin/master/images/advise-navigation.gif)
  - Highlighting of incorrect doctrine annotations for @access, @execution and @within pointcuts
  - Completion of doctrine annotations for @access, @execution and @within pointcuts ![Doctrine annotation completion](https://raw.githubusercontent.com/goaop/idea-plugin/master/images/doctrine-completion.gif)
  - Completion of visibility modifiers and pointcut keywords
  - Automatic injection of pointcut language into the Go\Lang\Annotation\* annotations
  - Color settings adjustment for pointcut expressions
  - Navigation from advices to the advised elements ![Navigate to advised elements](https://raw.githubusercontent.com/goaop/idea-plugin/master/images/navigate-to-advised.gif)

Additional features
-------------------

  - PHP Language injection into the [Php-Deal Design-by-Contract framework](https://github.com/lisachenko/php-deal)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `PLUGIN_ID` in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "intellij-idea-goaop-plugin"</kbd> >
  <kbd>Install</kbd>
  
- Manually:

  Download the [latest release](https://github.com/goaop/intellij-idea-goaop-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
