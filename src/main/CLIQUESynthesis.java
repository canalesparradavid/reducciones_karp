package main;
/**
 *
 * @author MAZ
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;
//
import tocnfsat.CLIQUEtoCNFSAT;
import ugraph.UndirectedGraph;
//
public final class CLIQUESynthesis {
    
  public static void main (final String[] args) {
 
    if (args.length < 3) {
      System.err.println("Uso: <k: int> <graph: file> <resolution: file> [complement: boolean]");
      return;
    }    
    
    final int k = Integer.parseInt(args[0]);
    final String graphFileName  = args[1];
    final String resolutionFileName = args[2];
    final boolean complement = (args.length > 3) ? Boolean.parseBoolean(args[3]) : false;      
    
    final String path = System.getProperty("user.dir") + File.separator
                                              + "data" + File.separator;    
    final File graphFile = new File(path +  graphFileName);
    final File resolutionFile = new File(path + resolutionFileName);    

    try (final InputStream is1 = getInputStream(graphFile);
         final InputStream is2 = new FileInputStream(resolutionFile)) {
      final UndirectedGraph graph = complement ? new UndirectedGraph(is1).complement()
                                               : new UndirectedGraph(is1);
      final CLIQUEtoCNFSAT synthesizer = new CLIQUEtoCNFSAT();
      final List<Integer> clique = synthesizer.synthesize(k, graph, is2);
      if (!clique.isEmpty()) {
        System.out.println("Clique de tamaño " + clique.size() + ":");
        clique.forEach((x) -> {
          System.out.println(x);
        });
      } else
        System.out.println("Grafo sin clique de tamaño " + k);
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