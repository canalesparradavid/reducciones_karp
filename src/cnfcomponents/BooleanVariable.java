package cnfcomponents;
/**
 *
 * @author MAZ
 */
public final class BooleanVariable {

  private final String label; // Utilizado solo a afectos de depuración
  private final int index;
  private final BooleanLiteral positive;
  private final BooleanLiteral negative;
  private boolean value;

  BooleanVariable (final int index, final String label) {
    this.label = label;
    this.index = index;
    this.value = false;
    this.positive = new BooleanLiteral(this, true);
    this.negative = new BooleanLiteral(this, false);
  }

  BooleanVariable (final int index) { this(index, ""); }

  public int getIndex () { return index; }
  public String getLabel () { return label; }

  public BooleanLiteral getPositiveLiteral () { return positive; }
  public BooleanLiteral getNegativeLiteral () { return negative; }

  public void set (final boolean value) { this.value = value; }
  public boolean eval () { return value; }

}