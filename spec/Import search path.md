# Import search path

When importing from `x`:

1. If `x` is a valid module name: a directory called `__ns-modules__` with a module called `x`, in the current or any ancestor directory, with closer directories taking priority
1. If `x` is a valid module name: a module installed to the local user's shared nanoscript modules directory
1. If `x` is a valid module name: a module installed to the global system shared nanoscript modules directory
1. If `x` starts with a path separator: a nanoscript source file located at the absolute path `x`
1. If `x` does not start with a path separator: a nanoscript source file located at the relative path `x`
1. If `x` does not start with a path separator: a nanoscript source file located at a path `x` relative to any path in the `NANOSCRIPT_IMPORT_PATH` environment variable, with paths separated by either a `;` or `:` regardless of platform/environment

## Cyclic imports

Imports are resolved to absolute paths, including when loading a module, and the paths are tracked to ensure that there are no cyclic imports.

It's still possible to load duplicate code if they're located at different paths.
