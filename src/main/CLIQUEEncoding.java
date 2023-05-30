package main;
/**
 *
 * @author MAZ
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
//
import cnfcomponents.CNFBooleanFormula;
import tocnfsat.CLIQUEtoCNFSAT;
import ugraph.UndirectedGraph;
//
public final class CLIQUEEncoding {
    
  public static void main (final String[] args) {
 
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
      final UndirectedGraph graph = complement ? new UndirectedGraph(is).complement()
                                               : new UndirectedGraph(is);
      final CLIQUEtoCNFSAT encoder = new CLIQUEtoCNFSAT();
      final CNFBooleanFormula phi = encoder.buildFormula(k, graph);
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