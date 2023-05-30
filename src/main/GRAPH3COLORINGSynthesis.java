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
import tocnfsat.GRAPH3COLORINGtoCNFSAT;
import tocnfsat.GRAPH3COLORINGtoCNFSAT.COLORS;
import ugraph.UndirectedGraph;
//
public final class GRAPH3COLORINGSynthesis {
    
  public static void main (final String[] args) {
 
    if (args.length < 2) {
      System.err.println("Uso: <graph: file> <resolution: file> [complement: boolean]");
      return;
    }
    
    final String graphFileName  = args[0];
    final String resolutionFileName = args[1];
    final boolean complement = (args.length > 2) ? Boolean.parseBoolean(args[2]) : false;      
    
    final String path = System.getProperty("user.dir") + File.separator
                                              + "data" + File.separator;    
    final File graphFile = new File(path +  graphFileName);
    final File resolutionFile = new File(path + resolutionFileName);    

    try (final InputStream is1 = getInputStream(graphFile);
         final InputStream is2 = new FileInputStream(resolutionFile)) {
      final UndirectedGraph graph = complement ? new UndirectedGraph(is1).complement()
                                               : new UndirectedGraph(is1);
      final GRAPH3COLORINGtoCNFSAT synthesizer = new GRAPH3COLORINGtoCNFSAT();
      final List<COLORS> colors = synthesizer.synthesize(graph, is2);
      if (!colors.isEmpty()) {
        System.out.println("Asignación de colores a nodos:");
        for (int j = 0; j < colors.size(); ++j) {
          final COLORS color = colors.get(j);
          System.out.println((j + 1) + " <- " + ((color == COLORS.R) ? "rojo" :
                                                ((color == COLORS.G) ? "verde" : "azul")));
        }
      } else
        System.out.println("Grafo no coloreable con 3 colores");
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