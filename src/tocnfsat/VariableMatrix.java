package tocnfsat;
/**
 *
 * @author MAZ
 */
import cnfcomponents.CNFBooleanFormula;
import cnfcomponents.BooleanVariable;
import cnfcomponents.BooleanLiteral;
//
final class VariableMatrix {
 
  private final BooleanVariable[][] pool;
  private final int nrows;
  private final int ncols;
  
  VariableMatrix (final int rows, final int cols, final CNFBooleanFormula phi) {
      
    this.nrows = rows;
    this.ncols = cols;
    pool = new BooleanVariable[nrows][ncols];
    
    int index = 0;
    for (int i = 0; i < nrows; ++i)
      for (int j = 0; j < ncols; ++j) {
        
        ++index;
        final String name = Integer.toString(index);
        pool[i][j] = phi.newVariable(name);
        
      }
                
  }
  
  BooleanVariable setVariable (final int i, final int j, final BooleanVariable v) {
    checkIndexes(i, j);
    return pool[i - 1][j - 1] = v;
  }
  
  BooleanVariable getVariable (final int i, final int j) {
    checkIndexes(i, j);    
    return pool[i - 1][j - 1];
  }  
  
  BooleanLiteral getPositiveLiteral (final int i, final int j) {
    checkIndexes(i, j);    
    return pool[i - 1][j - 1].getPositiveLiteral();
  }
  
  BooleanLiteral getNegativeLiteral (final int i, final int j) {
    checkIndexes(i, j);    
    return pool[i - 1][j - 1].getNegativeLiteral();
  }
  
  int rows () { return nrows; }
  int columns () { return ncols; }  
  
  void print () {
    System.out.print("     ");
    for (int i = 1; i <= ncols; ++i)
      System.out.print(" " + (i % 10));
    System.out.println();
    for (int i = 1; i <= nrows; ++i) {
      System.out.printf("%04d: ", i);
      for (int j = 1; j <= ncols; ++j) {
        System.out.print((getVariable(i, j).eval() ? "X" : ".") + " ");
      }
      System.out.println();
    }
    System.out.print("     ");
    for (int i = 1; i <= ncols; ++i)
      System.out.print(" " + (i % 10));
    System.out.println();    
  }
    
  private void checkIndexes (final int i, final int j) {
    if ((i < 1) || (nrows < i))
      throw new IllegalArgumentException("Bad row index i: " + i);
    if ((j < 1) || (ncols < j))
      throw new IllegalArgumentException("Bad column index j: " + j);      
  }
  
  
    
}