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
public final class CLIQUEtoCNFSAT { 
  
    /*
     * FUNCION: buildFormula
     * ENTRADA: Una grafo no dirigido y un entero k
     * SALIDA: Una formula booleana
    */
    public CNFBooleanFormula buildFormula (final int k, final UndirectedGraph G) {
        final CNFBooleanFormula phi = new CNFBooleanFormula();
        final SCE constraints = new SCE();

        // X_j sirve para indicar la pertencia al clique del nodo con índice j,
        // donde 1 <= j <= n:
        // * El literal positivo de X_j indica pertenencia al clique.
        // * El literal negativo de X_j indica no pertenencia al clique.
        final int n = G.vertices();
        final VariableMatrix X = new VariableMatrix(n, 1, phi);

        // Exactamente (o al menos) k literales del conjunto {X_1, ..., X_n} se deben
        // evaluar a true.
        List<BooleanLiteral> XLiterals = new ArrayList();
        for (int i = 1; i <= n; i++) {
            XLiterals.add(X.getPositiveLiteral(i, 1));
        }
        constraints.atLeast(k, XLiterals, phi);

        // Si dos nodos no están conectados, ambos no pueden pertenecer al clique.
        for (int i = 1; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (!G.areNeighbours(i, j)) {
                    BooleanLiteral x1 = X.getPositiveLiteral(i, 1);
                    BooleanLiteral x2 = X.getPositiveLiteral(j, 1);

                    phi.addClause(new DisjunctiveBooleanClause(x1.not(), x2.not()));
                }
            }
        }

        return phi;
  }
  
  public List<Integer> synthesize (final int k, final UndirectedGraph G, final InputStream is) {
    
    final List<Integer> clique = new ArrayList<>();

    final CNFBooleanFormula phi = new CNFBooleanFormula();
    final int n = G.vertices();
    final VariableMatrix X = new VariableMatrix(n, 1, phi);
    
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
          if (X.getVariable(j, 1).eval())
            clique.add(j);
        }
        // Si hay menos de k variables que se evalúen a true, no hay un clique de
        // tamaño k (probablemente haya algún problema en la construcción de la fórmula).
        if (clique.size() < k)
          clique.clear();
      } else
        throw new IllegalArgumentException("Error en codificación de la fórmula");

    } else
      System.out.println("Síntesis imposible");
    
    return clique;

  }
  
  private boolean verify (final UndirectedGraph G, final VariableMatrix X) {
    
    final int n = G.vertices();
    
    for (int u = 1; u < n; ++u) {
      
      if (X.getVariable(u, 1).eval()) {
        
        for (int v = u + 1; v <= n; ++v) {
          if (X.getVariable(v, 1).eval() && !G.areNeighbours(u, v)) {
            // Los nodos u y v pertenecen al clique y no son vecinos.
            return false;
          }
        }
        
      }  
      
    }
    
    return true;
    
  }
  
}
