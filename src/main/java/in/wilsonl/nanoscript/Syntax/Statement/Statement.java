package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Utils.Position;

public abstract class Statement {
  private final Position position;

  protected Statement (Position position) {
    this.position = position;
  }

  public Position getPosition () {
    return position;
  }
}
