PhpStorm / Go! AOP Framework integration plugin
-----------------

Provides Go! AOP Framework support for PhpStorm IDE such as pointcut highlighting, analysis, referencing and much more, allowing native feeling of AOP in project.

[![GitHub release](https://img.shields.io/github/release/goaop/idea-plugin.svg)](https://github.com/goaop/idea-plugin/releases/latest)
[![Minimum IDEA Version](http://img.shields.io/badge/IDEA-131-8892BF.svg)](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html)
[![License](https://img.shields.io/github/license/goaop/idea-plugin.svg)](https://github.com/goaop/idea-plugin)

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