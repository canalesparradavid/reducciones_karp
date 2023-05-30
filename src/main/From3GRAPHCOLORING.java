package main;
/**
 *
 * @author MAZ
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
//
import cnfcomponents.CNFBooleanFormula;
import karpreduction.CNFSATto3SAT;
import karpreduction.CNFSATtoCLIQUE;
import tocnfsat.CLIQUEtoCNFSAT;
import tocnfsat.GRAPH3COLORINGtoCNFSAT;
import ugraph.UndirectedGraph;
//
public final class From3GRAPHCOLORING {

  static public void main (final String[] args) {

    if (args.length < 2) {
      System.err.println("Uso: <graph: file> <cnf-formula: file> [complement: boolean]");
      return;
    }

    final String inputFileName  = args[0];
    final String outputFileName = args[1];
    final boolean complement = (args.length > 2) ? Boolean.parseBoolean(args[2]) : false;

    final String path = System.getProperty("user.dir") + File.separator
                                              + "data" + File.separator;
    final File  inFile = new File(path +  inputFileName);
    final File outFile = new File(path + outputFileName);

    try (final InputStream  is = getInputStream(inFile);
         final OutputStream os = new FileOutputStream(outFile)) {
      // Se parte de una instancia del problema 3-GRAPHCOLORING que
      // se codifica en una fórmula booleana que será satisfacible si y
      // solo si el grafo de la instancia es coloreable en tres colores.
      final UndirectedGraph graph = complement ? new UndirectedGraph(is).complement()
                                               : new UndirectedGraph(is);
      System.out.println("Grafo construido");
      final GRAPH3COLORINGtoCNFSAT encoder1 = new GRAPH3COLORINGtoCNFSAT();
      final CNFBooleanFormula phi = encoder1.buildFormula(graph);
      int n = phi.variables();
      int m = phi.clauses();
      System.out.println("Fórmula de codificación generada (" + n + " variables, " + m + " cláusulas)");
      // Se transforma la fórmula phi en una instancia del problema CLIQUE,
      // de modo que en el grafo generado habrá un clique de tamaño igual
      // al número de cláusulas de phi si y solo si la fórmula phi es satisfacible.
      //
      // Por transitividad, en el grafo generado habrá un clique de tamaño igual
      // al número de cláusulas de phi si y solo si el grafo especificado en los
      // argumentos es coloreable en tres colores.
      final CNFSATtoCLIQUE reductor1 = new CNFSATtoCLIQUE();
      final UndirectedGraph clique = reductor1.transform(phi);
      n = clique.vertices();
      m = clique.edges();
      System.out.println("Grafo con/sin clique generado (" + n + " nodos, " + m + " aristas)");
      // Para comprobación, dado que no se dispone de un algoritmo para encontrar
      // cliques en un grafo, la instancia de CLIQUE se codifica en una nueva
      // fórmula booleana que será satisfacible si y solo en el grafo generado
      // hay un clique de tamaño igual al número de cláusulas de la fórmula phi.
      //
      // Por transitividad, la nueva fórmula generada será satisfacible si y solo
      // si el grafo pasado como argumento es coloreable en 3 colores.
      final CLIQUEtoCNFSAT encoder2 = new CLIQUEtoCNFSAT();
      final CNFSATto3SAT reductor2 = new CNFSATto3SAT();
      final CNFBooleanFormula psi = reductor2.transform(encoder2.buildFormula(phi.clauses(), clique));
      n = psi.variables();
      m = psi.clauses();
      System.out.println("Fórmula final generada (" + n + " variables, " + m + " cláusulas)");
      psi.print(os);
    } catch (final FileNotFoundException ex) {
      System.err.println("Fichero de entrada no encontrado");
    } catch (final IOException ex) {
      System.err.println("Problema de I/O");
    }

  }

  static private InputStream getInputStream (final File inputFile)
          throws FileNotFoundException, IOException {

    final String fileName = inputFile.getName();
    if (fileName.endsWith(".zip")) {

      final FileInputStream fis = new FileInputStream(inputFile);
      final ZipInputStream  zis = new ZipInputStream(fis);

      if (zis.getNextEntry() != null) {
        return new BufferedInputStream(zis);
      } else {
        return null;
      }

    } else {
      return new FileInputStream(inputFile);
    }

  }

}
