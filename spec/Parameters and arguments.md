# Parameters and arguments

## Parameters

### Order of arguments consumption

1. Required parameters before variable length or first optional parameter (whichever is first), left to right
1. Required parameters after variable length or last optional parameter (whichever is last), right to left
1. Optional parameters from first up to last or variable length (whichever is last), left to right
1. If there is a variable length parameter, optional parameters from last down to one after variable length, right to left
1. Remaining to the variable length parameter; if there is no variable length parameter, the remaining arguments must be optional

### Variable length parameters

Parameters can be made variable length by placing the ellipsis operator before the name.

### Optional parameters

Parameters can be made optional by specifying the `optional` modifier before the name or ellipsis (if it is a variable length parameter).

When an optional parameter is not matched up with an argument (see [Order of arguments consumption](#Order of arguments consumption)), its value is set to its default value; normally an `ArgumentsError` is thrown.

### Default value

Optional parameters can have a default value. Parameters with a declared default value but no optional modifier will cause a `SyntaxError`.

All optional parameters have an implicit default value of `null`. To explicitly declare one, declare a value expression after the name with an initialiser operator between them. The following lambda has one parameter, `arg_1`, that is marked optional and has a default value of `500`:

```nanoscript
fn (optional arg_1 := 500) endfn
```

Explicitly-declared default values are expressions, and are evaluated every time the function is called.


## Arguments

### Optional arguments

Arguments can be marked as optional by specifying the `optional` modifier before the value expression.

Normally, when an argument is not matched up with a parameter (see [Order of arguments consumption](#Order of arguments consumption)), an `ArgumentsError` is thrown; however, if it is optional, the value is simply discarded.
