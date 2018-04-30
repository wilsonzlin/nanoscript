package in.wilsonl.nanoscript.Syntax;


import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.SetOnce;

public class Variable {
  private final SetOnce<Identifier> name = new SetOnce<>();
  private final SetOnce<Expression> initialiser = new SetOnce<>(); // Must exist

  public Identifier getName () {
    return name.get();
  }

  public void setName (Identifier name) {
    this.name.set(name);
  }

  public Expression getInitialiser () {
    return initialiser.get();
  }

  public void setInitialiser (Expression initialiser) {
    this.initialiser.set(initialiser);
  }
}
