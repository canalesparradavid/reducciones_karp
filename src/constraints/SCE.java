package constraints;
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
import constraints.CardinalityConstraints;
//
public class SCE implements CardinalityConstraints {
    
  @Override
  public void atMost (final int k, final List<BooleanLiteral> literals, final CNFBooleanFormula phi) {
    
    final int n = literals.size();
    
    if (n > 0) { // A lo sumo k de 0 literales es de cumplimiento trivial
      if (k == 0) {
        // A lo sumo 0 ovejas blancas == al menos n ovejas negras
        for(int i = 1; i <= n; i++){
            phi.addClause(new DisjunctiveBooleanClause(literals.get(i-1).not()));
        }
      } else if (k == 1) {
        atMost1(literals, phi);
      } else if ((k > 1) && (k < (n - 1))) { 
        final RMatrix R = new RMatrix(n - 1, k, phi);
     
        // Codificación del artículo
        // Clausula 1
        for(int i = 1; i <= (n-1); i++){
            BooleanLiteral l = literals.get(i-1);
            BooleanLiteral r = R.getPositiveLiteral(i, 1);
            phi.addClause(new DisjunctiveBooleanClause(l.not(), r));
        }
        // Clausula 2
        for(int j = 2; j <= k; j++){
            BooleanLiteral r = R.getPositiveLiteral(1, j);
            phi.addClause(new DisjunctiveBooleanClause(r.not()));
        }
        // Clausula 3
        for(int i = 2; i <= (n-1); i++){
            for(int j = 1; j <= k; j++){
                BooleanLiteral r1 = R.getPositiveLiteral(i-1, j);
                BooleanLiteral r2 = R.getPositiveLiteral(i, j);
                
                phi.addClause(new DisjunctiveBooleanClause(r1.not(), r2));
            }
        }
        // Clausula 4
        for(int i = 2; i <= (n-1); i++){
            for(int j = 2; j <= k; j++){
                BooleanLiteral l = literals.get(i-1);
                BooleanLiteral r1 = R.getPositiveLiteral(i-1, j-1);
                BooleanLiteral r2 = R.getPositiveLiteral(i, j);
                
                phi.addClause(new DisjunctiveBooleanClause(l.not(), r1.not(), r2));
            }
        }
        // Clausula 5
        for(int i = 2; i <= n; i++){
            BooleanLiteral l = literals.get(i-1);
            BooleanLiteral r = R.getPositiveLiteral(i-1, k);
            
            phi.addClause(new DisjunctiveBooleanClause(l.not(), r.not()));
        }
        
      } else if (k == (n - 1)) {
        
        // A lo sumo n - 1 ovejas blancas == al menos 1 oveja negra
        for(int i = 1; i <= n; i++){
            List<BooleanLiteral> negras = new ArrayList();
            for(BooleanLiteral l: literals){
                negras.add(l.not());
            }
            atLeast(1, negras, phi);
        }

      } else if (k >= n) {
        
        // Cuando k >= n, se cumple trivialmente a lo sumo k de un total de n.
        // Se queda vacío.
        
      }
      
    }

  }

  @Override
  public void atMost1 (final List<BooleanLiteral> literals, final CNFBooleanFormula phi) {
      
    final int n = literals.size();
    
    if (n > 0) {
      
      final RMatrix R = new RMatrix(n - 1, 1, phi);

      // Condiciones simplificadas del artículo; en
      // concreto, desaparecen las cláusulas (2) y (4).
    // Codificación del artículo
        // Clausula 1
        for(int i = 1; i <= (n-1); i++){
            BooleanLiteral l = literals.get(i-1);
            BooleanLiteral r = R.getPositiveLiteral(i, 1);
            phi.addClause(new DisjunctiveBooleanClause(l.not(), r));
        }
        // Clausula 3
        for(int i = 2; i <= (n-1); i++){
            BooleanLiteral r1 = R.getPositiveLiteral(i-1, 1);
            BooleanLiteral r2 = R.getPositiveLiteral(i, 1);

            phi.addClause(new DisjunctiveBooleanClause(r1.not(), r2));
        }
        // Clausula 5
        for(int i = 2; i <= n; i++){
            BooleanLiteral l = literals.get(i-1);
            BooleanLiteral r = R.getPositiveLiteral(i-1, 1);
            
            phi.addClause(new DisjunctiveBooleanClause(l.not(), r.not()));
        }
    }

  }
    
  @Override
  public void atLeast (final int k, final List<BooleanLiteral> literals, final CNFBooleanFormula phi) {
 
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
