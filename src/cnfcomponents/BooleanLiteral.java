package cnfcomponents;
/**
 *
 * @author MAZ
 */
public final class BooleanLiteral {

  private final BooleanVariable x;
  private final boolean state;

  BooleanLiteral (final BooleanVariable x, final boolean state) {
    this.x = x;
    this.state = state;
  }
  
  public BooleanVariable getVariable () {
    return x;
  }  

  public int getIndex () {
    return (state) ? x.getIndex() : -x.getIndex();
  }

  public String getLabel () {
    return x.getLabel();
  }

  public BooleanLiteral not () {
    return (state) ? x.getNegativeLiteral() : x.getPositiveLiteral();
  }

}