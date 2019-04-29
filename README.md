# nanoscript

A simple to { learn, prototype, scale, extend } language. Currently written in Java.

Working on documentation and tests. Issues and pull requests welcome!

Documentation available at [wilsonl.in/docs/nanoscript](https://wilsonl.in/docs/nanoscript/).

## Purpose

This started off as a small side project to experiment with language design.
Use it for learning, embed it into a project, or check out its syntax.

## Does it work

Yes it does, but it has yet to be benchmarked or optimised.

## Getting started

JDK 1.8+ and Maven 3.5+ are required.

Once installed, run the `compile.sh` script, which will output `nanoscript-VERSION.jar` 
in the same directory as the script, where `VERSION` is the version.

Run it using `java -jar`, and provide the path to a nanoscript file as an argument:

```sh
java -jar nanoscript-VERSION.jar /path/to/nanoscript.ns
```

A [quick start](https://wilsonl.in/docs/nanoscript/1/0/General/Quick-start/) article is available in the documentation.
