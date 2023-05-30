package tocnfsat;
/**
 *
 * @author MAZ
 */
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//
import cnfcomponents.BooleanLiteral;
import cnfcomponents.BooleanVariable;
import cnfcomponents.CNFBooleanFormula;
import cnfcomponents.DisjunctiveBooleanClause;
import constraint.SCE;
import ugraph.UndirectedGraph;
//
public final class GRAPH3COLORINGtoCNFSAT {
  
  static public enum COLORS { R, G, B }
  
  public CNFBooleanFormula buildFormula (final UndirectedGraph G) {
    
    final CNFBooleanFormula phi = new CNFBooleanFormula();
        
    // X_j,1 sirve para indicar el coloreado en rojo del nodo j, donde 1 <= j <= n:
    // * El literal positivo de X_j,1 indica que el nodo j tiene asignado el color rojo.
    // * El literal negativo de X_j,1 indica que el nodo j no tiene asignado el color rojo.
    // X_j,2 sirve para indicar el coloreado en verde del nodo j.
    // X_j,3 sirve para indicar el coloreado en azul del nodo j.
    final int n = G.vertices();
    final VariableMatrix X = new VariableMatrix(n, 3, phi);
    
    // Cada nodo debe tener asignado un único color.
    
    // Si dos nodos están conectados, no pueden coincidir en color.
    
    return phi;
    
  }
  
  public List<COLORS> synthesize (final UndirectedGraph G, final InputStream is) {
    
    final List<COLORS> coloring = new ArrayList<>();

    final CNFBooleanFormula phi = new CNFBooleanFormula();
     
    final int n = G.vertices();
    final VariableMatrix X = new VariableMatrix(n, 3, phi);
    
    final Scanner scanner = new Scanner(is);

    final String resolutionResult = scanner.nextLine();
    if (resolutionResult.compareTo("s SATISFIABLE") == 0) {

      while (scanner.hasNextLine()) {

        final String initialToken = scanner.next();
        if (initialToken.compareTo("v") == 0) {
          while (scanner.hasNextInt()) {
            final int index = scanner.nextInt();
            final boolean isPositive = index > 0;
            if (phi.variableExists(isPositive ? index : -index)) {
              final BooleanVariable x = phi.getVariable(isPositive ? index : -index);
              x.set(isPositive);
            }
          }
        }
        scanner.nextLine();
      }

      if (verify(G, X)) {
        for (int j = 1; j <= n; ++j) {
          coloring.add(X.getVariable(j, 1).eval() ? COLORS.R :
                       (X.getVariable(j, 2).eval() ? COLORS.G : COLORS.B));
        }
      } else
        throw new IllegalArgumentException("Error en codificación de la fórmula");

    } else
      System.out.println("Síntesis imposible");
    
    return coloring;

  }
  
  private boolean verify (final UndirectedGraph G, final VariableMatrix X) {
    
    final int n = G.vertices();
    
    for (int u = 1; u < n; ++u) {
      
      for (int v = u + 1; v <= n; ++v) {
        if (G.areNeighbours(u, v)) {
          // Los nodos u y v son vecinos y tienen asignado el mismo color.
          if ((X.getVariable(u, 1).eval() && X.getVariable(v, 1).eval()) ||
              (X.getVariable(u, 2).eval() && X.getVariable(v, 2).eval()) ||
              (X.getVariable(u, 3).eval() && X.getVariable(v, 3).eval()))
            return false;
        }
      }
      
    }
    
    return true;
    
  }
  
}
