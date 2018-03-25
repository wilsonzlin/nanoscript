# nanoscript

A simple to { learn, prototype, scale, extend } language. Right now written in Java.

```nanoscript
create nanoscript as @{
  learn as `simple`,
  prototype as `simple`,
  scale as `simple`,
  extend as `simple`,
}
```

Documentation available at [wilsonl.in/docs/nanoscript](https://wilsonl.in/docs/nanoscript/).

## Purpose

This was just a small side project to try out parts of [ooml-lang](https://github.com/lerouche/ooml-lang). Use it for learning, embed it into your project, or be inspired (or disappointed) by its syntax.

## Does it work

Yes it does, but don't expect amazing performance (it might have, but I have neither benchmarked nor optimised).

## Getting started

Right now, it's a Maven project, so you'll need JDK 1.8+ and Maven 3.5+.
Once installed, just run the `compile.sh` script, which will output `nanoscript-VERSION.jar`.
Run it using `java -jar`, and provide the path to a nanoscript file as an argument:

```sh
java -jar nanoscript-VERSION.jar /path/to/nanoscript.ns
```

A [quick start](https://wilsonl.in/docs/nanoscript/1/0/General/Quick-start/) article is available in the documentation.
