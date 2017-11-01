# Evaluation of members

TLDR: All variables are evaluated **in the context they are declared in at all times**. The symbols and symbol values in the context may change depending on the state and time. For instance members, **the only varying context symbol is `self`**.

## Instance variables

1. `self`
1. Instance variable values already in `self`
    - Instance variables in ancestor classes
    - Instance variables declared before in the class
1. Own or inherited instance methods
1. Static variables in the current or ancestor class(es)
1. Static methods in the current or ancestor class(es)
1. The context containing the class declaration
    - This applies to ancestor classes too i.e. when processing ancestor class instance variables, they are evaluated in the context of its class's declaration, not the instantiating class's declaration
    
    
```
===== file1.nanoscript

a := 0

class A begin
    a := a + 1 // <a> refers to the global
    b := a + 1 // <a> refers to the instance variable, because
               //  its evaluation has now shadowed the global
    c := self.a + 1 // Since the order of declaration means that
                    // <a> has already been evaluated, this is OK
endclass

setInterval(fn ()
    a = a + 5
endfn, 5000)


===== file2.nanoscript
```

## Constructors

## Instance methods

## Static variables

## Static methods
