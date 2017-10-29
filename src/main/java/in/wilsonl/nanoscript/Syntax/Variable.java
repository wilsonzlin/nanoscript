package in.wilsonl.nanoscript.Syntax;


import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.SetOnce;

public class Variable {
    private final SetOnce<Identifier> name = new SetOnce<>();
    private final SetOnce<Expression> initialiser = new SetOnce<>(true); // Can be null (i.e. no initialiser)

    public boolean hasInitialiser() {
        return initialiser.isSet() && initialiser.get() != null;
    }

    public void setName(Identifier name) {
        this.name.set(name);
    }

    public void setInitialiser(Expression initialiser) {
        this.initialiser.set(initialiser);
    }
}
