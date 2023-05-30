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
import karpreduction.CNFSATto3GRAPHCOLORING;
import karpreduction.CNFSATto3SAT;
import tocnfsat.CLIQUEtoCNFSAT;
import tocnfsat.GRAPH3COLORINGtoCNFSAT;
import ugraph.UndirectedGraph;
//
public final class FromCLIQUE {

  static public void main (final String[] args) {

    if (args.length < 3) {
      System.err.println("Uso: <k: int> <graph: file> <cnf-formula: file> [complement: boolean]");
      return;
    }

    final int k = Integer.parseInt(args[0]);
    final String inputFileName  = args[1];
    final String outputFileName = args[2];
    final boolean complement = (args.length > 3) ? Boolean.parseBoolean(args[3]) : false;     

    final String path = System.getProperty("user.dir") + File.separator
                                              + "data" + File.separator;
    final File  inFile = new File(path +  inputFileName);
    final File outFile = new File(path + outputFileName);

    try (final InputStream  is = getInputStream(inFile);
         final OutputStream os = new FileOutputStream(outFile)) {
      // Se parte de una instancia del problema CLIQUE que se codifica
      // en una fórmula booleana que será satisfacible si y solo
      // si en el grafo hay un clique de tamaño k.
      final UndirectedGraph graph = complement ? new UndirectedGraph(is).complement()
                                               : new UndirectedGraph(is);
      System.out.println("Grafo construido");
      final CLIQUEtoCNFSAT encoder1 = new CLIQUEtoCNFSAT();
      final CNFBooleanFormula phi = encoder1.buildFormula(k, graph);
      int n = phi.variables();
      int m = phi.clauses();
      System.out.println("Fórmula de codificación generada (" + n + " variables, " + m + " cláusulas)");
      // Se transforma la fórmula phi en una instancia del problema 3-GRAPH-COLORING,
      // de modo que el grafo generado será coloreable en 3 colores si y solo si
      // si la fórmula phi es satisfacible.
      //
      // Por transitividad, el grafo generado será coloreable en tres colores si
      // y solo si en el grafo pasado como argumento existe un clique de tamaño k.
      final CNFSATto3GRAPHCOLORING reductor1 = new CNFSATto3GRAPHCOLORING();
      final UndirectedGraph _graph = reductor1.transform(phi);
      n = _graph.vertices();
      m = _graph.edges();
      System.out.println("Grafo coloreable generado (" + n + " nodos, " + m + " aristas)");
      // Para comprobación, dado que no se dispone de un algoritmo para colorear
      // grafos, la instancia de 3-GRAPH-COLORING se codifica en una nueva
      // fórmula booleana que será satisfacible si y solo el grafo generado
      // es coloreable en tres colores.
      //
      // Por transitividad, la nueva fórmula generada es satisfacible si y solo
      // si en el grafo especificado en los argumentos hay un clique de tamaño k.
      final GRAPH3COLORINGtoCNFSAT encoder2 = new GRAPH3COLORINGtoCNFSAT();
      final CNFSATto3SAT reductor2 = new CNFSATto3SAT();
      final CNFBooleanFormula psi = reductor2.transform(encoder2.buildFormula(_graph));
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
