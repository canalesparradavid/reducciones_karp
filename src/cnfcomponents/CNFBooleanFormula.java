package cnfcomponents;
/**
 *
 * @author MAZ
 */
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public final class CNFBooleanFormula {

  private final List<DisjunctiveBooleanClause> clauses;
  private final Map<Integer, BooleanVariable> variables;
  private int nextIndex;
  
  /**
   * Constructor para fórmulas en formato DIMACS
   * @param stream: stream cuyo contenido es la descripción formato DIMACS
   * de la fórmula a construir.
  **/
  public CNFBooleanFormula (final InputStream stream) {
    
    try (final Scanner scanner = new Scanner(stream)) {

      scanner.next("p");
      scanner.next("cnf");
      
      final int n = scanner.nextInt();
      final int m = scanner.nextInt();

      //
      clauses   = new LinkedList<>();
      variables = new LinkedHashMap<>();
      nextIndex = 1;
      
      // Creación de variables
      for (int j = 0; j < n; ++j) {
        final BooleanVariable v = new BooleanVariable(nextIndex, "");
        variables.put(nextIndex, v);
        ++nextIndex;
      }

      // Construcción de cláusulas
      final List<BooleanLiteral> literals = new ArrayList<>();
      for (int j = 0; j < m; ++j) {

        scanner.nextLine();

        // Lee literales que forman cláusula y añade cláusula a fórmula.
        int token;
        do {
          token = scanner.nextInt();
          if (token > 0) {
            literals.add(getVariable(token).getPositiveLiteral());
          } else if (token < 0) {
            literals.add(getVariable(-token).getNegativeLiteral());
          }
        } while (token != 0);

        clauses.add(new DisjunctiveBooleanClause(literals));
        literals.clear();

      }
    
    }
    
  }  

  /**
   * Constructor básico; 
   **/
  public CNFBooleanFormula () {
    clauses   = new LinkedList<>();
    variables = new LinkedHashMap<>();
    nextIndex = 1;
  }

  public void addClause (final DisjunctiveBooleanClause x) {
    clauses.add(x);
  }

  public BooleanVariable newVariable (final String name) {
    final BooleanVariable v = new BooleanVariable(nextIndex, name);
    variables.put(nextIndex, v);
    ++nextIndex;
    return v;
  }

  public BooleanVariable newVariable () {
    return newVariable("");
  }

  public BooleanVariable getVariable (final int index) {
    return variables.get(index);
  }

  public boolean variableExists (final int index) {
    return variables.containsKey(index);
  }

  //
  // Escribe la codificación en formato DIMACS de la fórmula sobre el stream
  //
  public void print (final OutputStream os) throws IOException {

    try (final PrintWriter dos = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)))) {

      final String declaration = "p cnf " + variables.size() + " " + clauses.size();
      dos.println(declaration);

      clauses.forEach((clause) -> {
        dos.println(clause.print());
      });

    }

  }

  //
  // Escribe la codificación en formato DIMACS de la fórmula sobre el stream
  //
  public void printWithLabels (final OutputStream os) throws IOException {

    try (final PrintWriter dos = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)))) {

      final String declaration = "p cnf " + variables.size() + " " + clauses.size();
      dos.println(declaration);

      clauses.forEach((clause) -> {
        dos.println(clause.printWithLabels());
      });

    }

  }
  
  public Collection<BooleanVariable> getVariables () { return variables.values(); }
  public List<DisjunctiveBooleanClause> getClauses () { return clauses; }
  
  public int variables () { return variables.size(); }
  public int clauses   () { return clauses.size(); }    

}