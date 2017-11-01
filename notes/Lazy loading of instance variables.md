# Lazy loading of instance variables

... is bad because it leads to unexpected results:

```
class A begin
    a := 1
    b := a + 1
    constructor()
        a = 2
        "<b> should be 2, but is actually 3"
        print(b)
    endconstructor
endclass
```
