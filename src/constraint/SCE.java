package constraint;
/**
 *
 * @author MAZ
 */
import java.util.ArrayList;
import java.util.List;
//
import cnfcomponents.CNFBooleanFormula;
import cnfcomponents.DisjunctiveBooleanClause;
import cnfcomponents.BooleanLiteral;
import cnfcomponents.BooleanVariable;
//
public class SCE implements CardinalityConstraints {

    @Override
    public void atMost(int k, List<BooleanLiteral> literals, CNFBooleanFormula phi) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void atMost1(List<BooleanLiteral> literals, CNFBooleanFormula phi) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void atLeast(int k, List<BooleanLiteral> literals, CNFBooleanFormula phi) {
        final int n = literals.size();
    
    if (k > 0) { // k == 0 es de cumplimiento trivial
        if ((k > n) || (n == 0)) {
          throw new IllegalArgumentException("Insatisfacible");
        }
        if (k == 1) {
            atLeast1(literals, phi);
        } else if ((k > 1) && (k < n)) {
            // Al menos k ovejas blancas == a lo sumo n - k ovejas negras
            List<BooleanLiteral> negras = new ArrayList();
            for(BooleanLiteral l: literals){
                negras.add(l.not());
            }
            atMost(n-k, negras, phi);
        } else if (k == n) {
            // Al menos n de n literales es exigir que se cumplan todos.
            for(int i = 1; i <= n; i++){
                phi.addClause(new DisjunctiveBooleanClause(literals.get(i-1)));
            }
        }
    }
    }

  static private final class RMatrix {

    final BooleanVariable[][] pool;
    final int n;
    final int k;

    RMatrix (final int n, final int k, final CNFBooleanFormula phi) {

      this.n = n;
      this.k = k;
      pool = new BooleanVariable[n][k];

      int index = 0;
      for (int i = 0; i < n; ++i) {
        for (int j = 0; j < k; ++j) {

          ++index;
          final String name = Integer.toString(index);
          pool[i][j] = phi.newVariable(name);

        }
      }

    }

    BooleanVariable setVariable (final int i, final int j, final BooleanVariable v) {
      if ( (i < 1) || (n < i) || (j < 1) || (k < j))
        throw new IllegalArgumentException();
      return pool[i - 1][j - 1] = v;
    }

    BooleanVariable getVariable (final int i, final int j) {
      if ( (i < 1) || (n < i) || (j < 1) || (k < j))
        throw new IllegalArgumentException();
      return pool[i - 1][j - 1];
    }

    BooleanLiteral getPositiveLiteral (final int i, final int j) {
      if ( (i < 1) || (n < i) || (j < 1) || (k < j))
        throw new IllegalArgumentException();
      return pool[i - 1][j - 1].getPositiveLiteral();
    }

    BooleanLiteral getNegativeLiteral (final int i, final int j) {
      if ( (i < 1) || (n < i) || (j < 1) || (k < j))
        throw new IllegalArgumentException();
      return pool[i - 1][j - 1].getNegativeLiteral();
    }

  }

}