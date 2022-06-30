# Syntax

## Assertion

`assert <expr>`
`assert <expr> with <msg_expr>`

## Mutability

All variables and object properties are immutable unless explicitly prefixed with `mut`.

## Literals

`yes`, `no`
`1.234`
`````` `ab`  ``````
`````` ```ab ${expr} %{dbgexpr}```  ``````
`````` r```ab```  ``````: No interpolations or escapes.
`````` b```ab```  ``````: UTF-8 bytes.
`````` ~```ab```  ``````: Indented string: the first and last lines must only contain whitespace and are removed, and then each line has n leading whitespace removed, where n is the shortest amount of leading whitespace of all lines.

## OOP

No classes, inheritance, or special `this`. "Methods" are simply lambdas assigned to properties. Compose using `...` operator. If a method is referenced via an object (e.g. `obj.method`), the first argument is bound to is `obj` (e.g. when calling or passing); to avoid, use `:` (e.g. `obj:method`). Internal properties (including methods) should be prefixed with `_`; however, this is mere convention, and no access checking is enforced.

## Comment

`//` and `/*`.

## Defer

## Expression

Everything is an expression. Expressions can result in a value, void, or an error.

Void is rarely accepted. Errors even less so.
Void is not the same as `none`; the latter is a value.

## Special fields

`eq(other)`: used for sets, maps, and equality operators; must result in a boolean.
`cmp(other)`: used for comparison operators; must result in a number.
`hash()`: used for sets and maps; must result in a number.
`size()`: used for size operator; must result in a number.
`dbg()`: used for debugging; must result in a string.
`range(high, highInc, lowExc)`
`plus(other)`
`minus(other)`
`mul(other)`
`div(other)`

## Globals

`print()`
`nan`

## Block

Blocks create a new scope.

Each expression in a block can result in void or a value.

The last expression in a block is the result (value or void).

## Function

`|args| <expr>`.

All code paths must be void, a value, an assertion, or an error. Additionally, a function cannot have both a void and a value code path.

## Case

A `case` expression evaluates each branch in order, and results in the first matching branch, or the original value if no branch matched. If a branch starts with `err`, it matches error values only; all other branches only match non-errors. If the matched branch results in void, the `case` results in void.

## Operators

`and`, `or`, `not`, `!=`, `==`, `>=`, `>`, `<=`, `<`.
`#<op>`: `<op>.size()`.
`%<op>`: `<op>.dbg()`.
`<lhs> + <rhs>`: concatenate strings; add numbers.
`<lhs> is <rhs>`: `<lhs>?.type == <rhs>`.
`<lhs> is not <rhs>`: `<lhs>?.type != <rhs>`.
`<lhs>?.field.a.b.c().d`: `if <lhs> with field { <lhs>.field.a.b.c().d } else { none }`.
`<lhs>?[<rhs>].a.b.c().d`: `if <lhs> has <rhs> { <lhs>[<rhs>].a.b.c().d } else { none }`.
`<lhs> |> <rhs>`: `{ let it = <lhs>; <rhs> }`.
`<lhs> ?> <rhs>`: `{ let it = <lhs>; if <rhs> { it } else { none }`.
`<lhs> |>> <rhs>`: `<rhs>(<lhs>)`.
`<lhs> |?> <rhs>`: `if <rhs>(<lhs>) { <lhs> } else { none }`.
`for <var> in <expr> { break with <expr> }`: `<expr>` if `break`, otherwise `none`.
`for <var> in <expr> { gen <expr> }`: results in an immutable list of `<expr>` values.
`<lhs> !> <type_val>: <rhs>`: `case <lhs> { err it?.type == <type_val> { <rhs> } }`.
`<lhs> !> if <cond>: <rhs>`: `case <lhs> { err <cond> { <rhs> } }`.
`<lhs> !!`: `case <lhs> { err { it } }`.
`@<type_val>{ a = 1; mut b = 2, ...c }`: `@{ type = <type_val>; a = 1; mut b = 2, ...c }`.
`!<obj_expr>`: returns an error value; `<obj_expr>` must be an object.
`<expr>?!`: `if <expr> is none { panic } else { <expr> }`.
`<lhs> ?? <rhs>`: `if <lhs> == none { <rhs> } else { <lhs> }`.
`<lhs> ## <rhs>`: `if #<lhs> == 0 { <rhs> } else { <lhs> }`.

## Error

Error values are hot potatoes: any attempt to use them other than a few specific ways will result in an unhandled error crash. The only allowed usages:

- `!>` as LHS.
- `!!` as LHS.
- Operand of `case` if matched.
