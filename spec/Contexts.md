# Contexts

## Global/Chunk

### Get symbol

1. Global variables
1. Classes (as classes can't be nested or first-class)
1. Imports

### Set symbol

1. Global variables

### Apply access

N/A

## Class from static member

`from static member` means from a static variable's initialiser or a static method.

When mentioning `members` in the lists below, methods are searched before variables.

When mentioning `ancestor` in the lists below, the order of traversal is depth-first, left-to-right. For example, if `A` inherits from `B, H`, `B` inherits from `C, F`, `C` inherits from `D, E`, `F` inherits from `G`, and `H` inherits from `I, J`, the order would be `ABCDEFGHIJ`.

```nanoscript
class J
endclass

class I
endclass

class H : I + J
endclass

class G
endclass

class F : G
endclass

class E
endclass

class D
endclass

class C : D + E
endclass

class B : C + F
endclass

class A : B + H
endclass
```

### Get symbol

1. &lt;Callable closure> (if method)
1. Own static members
1. Ancestor static members
1. &lt;Global/Chunk>

### Set symbol

1. &lt;Callable closure> (if method)
1. Own static members
1. Ancestor static members
1. &lt;Global/Chunk>

### Apply access

In the form `SomeClass.staticMember`.

1. Own static members

### Apply assignment

In the form `SomeClass.staticVariable`.

1. Own static variables

## Class from instance member or constructor

When mentioning `ancestor` in the lists below, ancestor instance variables are inherited, and are copied to become own instance variables.
Changing one does not affect other instances.

### Get symbol

1. `self`
1. &lt;Callable closure> (if method/constructor)
1. Own instance members
1. Ancestor instance members
1. Own static members
1. Ancestor static members
1. &lt;Global/Chunk>

### Set symbol

To create a new instance variable, use `self.<name>`

1. &lt;Callable closure> (if method/constructor)
1. Own instance members
1. Ancestor instance members
1. Own static members
1. Ancestor static members
1. &lt;Global/Chunk>

### Apply access

In the form `someInstance.member`.

1. Own instance members
1. Ancestor instance members

### Apply assignment

1. Own or inherited ancestor instance variables
1. New own instance variables

## Callable closure

Lambdas, methods, constructors.

### Get symbol

1. Own variables
1. Parameter arguments
1. Parent context

### Set symbol

1. Own variables
1. Parameter arguments
1. Parent context

### Apply access

N/A

## Scoped block

`if`, `while`, `repeat`, `try`, `for`.

### Get symbol

1. Own variables
1. Parent context

### Set symbol

1. Own variables
1. Parent context

### Apply access

N/A
