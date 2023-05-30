package constraint;
/**
 *
 * @author MAZ
 */
import java.util.List;
//
import cnfcomponents.BooleanLiteral;
import cnfcomponents.CNFBooleanFormula;
import cnfcomponents.DisjunctiveBooleanClause;
//
public interface CardinalityConstraints {

  public void atMost (final int k,
                      final List<BooleanLiteral> literals,
                      final CNFBooleanFormula phi);
  
  public void atMost1 (final List<BooleanLiteral> literals,
                       final CNFBooleanFormula phi);
  
  public void atLeast (final int k,
                       final List<BooleanLiteral> literals,
                       final CNFBooleanFormula phi);
  
  default public void atLeast1 (final List<BooleanLiteral> literals,
                                final CNFBooleanFormula phi) {
    phi.addClause(new DisjunctiveBooleanClause(literals));
  }
  
  default public void exactly (final int k,
                               final List<BooleanLiteral> literals,
                               final CNFBooleanFormula phi) {
    if (k != 1) {
      atMost(k, literals, phi);
      atLeast(k, literals, phi);
    } else
      exactly1(literals, phi);
  }
  
  default public void exactly1 (final List<BooleanLiteral> literals,
                                final CNFBooleanFormula phi) {
    atLeast1(literals, phi);
    atMost1(literals, phi);
  }

}