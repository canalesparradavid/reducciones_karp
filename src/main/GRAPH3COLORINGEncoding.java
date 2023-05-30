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
import tocnfsat.GRAPH3COLORINGtoCNFSAT;
import ugraph.UndirectedGraph;
//
public final class GRAPH3COLORINGEncoding {

  public static void main (final String[] args) {
 
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
      final UndirectedGraph graph = complement ? new UndirectedGraph(is).complement()
                                               : new UndirectedGraph(is);
      final GRAPH3COLORINGtoCNFSAT encoder = new GRAPH3COLORINGtoCNFSAT();
      final CNFBooleanFormula phi = encoder.buildFormula(graph);
      phi.print(os);
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
