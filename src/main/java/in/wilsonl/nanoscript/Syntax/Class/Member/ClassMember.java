package in.wilsonl.nanoscript.Syntax.Class.Member;

import in.wilsonl.nanoscript.Utils.SetOnce;

public abstract class ClassMember {
  private final SetOnce<Boolean> isStatic = new SetOnce<>();

  public Boolean isStatic () {
    return isStatic.get();
  }

  public void isStatic (boolean s) {
    isStatic.set(s);
  }
}
