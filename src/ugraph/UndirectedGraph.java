package ugraph;
/**
 *
 * @author MAZ
 */
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;
//
public final class UndirectedGraph {

  static private final String GRAPH_FORMAT_PATTERN = "%%.*|c";

  private final int m; // Número de aristas
  private final int n; // Número de nodos
  private final List<Integer>[] neighbours; // Correspondencia: nodo -> { nodo }
  private final int[] vertex1; // Correspondencia: arista -> nodo 1
  private final int[] vertex2; // Correspondencia: arista -> nodo 2
  
  public UndirectedGraph (final Map<Integer, List<Integer>> neighbours,
                          final List<Integer> vertex1,
                          final List<Integer> vertex2) {
    
    if (vertex1.size() != vertex2.size())
      throw new IllegalArgumentException("Listas con información de aristas no tienen igual longitud");
    
    this.n = neighbours.size();
    this.m = vertex1.size();
    
    // Nodos y vecinos
    this.neighbours = new ArrayList[n];
    for (int node = 1; node <= n; ++node) {
      if (!neighbours.containsKey(node))
        throw new IllegalArgumentException("Índice de nodo desconocido: " + node);
      this.neighbours[node - 1] = new ArrayList<>(neighbours.get(node));
    }
    
    // Aristas
    this.vertex1 = new int[m];
    this.vertex2 = new int[m];
    for (int j = 0; j < m; ++j) {
      if (!neighbours.containsKey(vertex1.get(j)))
        throw new IllegalArgumentException("Índice de nodo desconocido: " + vertex1.get(j));      
      this.vertex1[j] = vertex1.get(j);
      if (!neighbours.containsKey(vertex2.get(j)))
        throw new IllegalArgumentException("Índice de nodo desconocido: " + vertex2.get(j));       
      this.vertex2[j] = vertex2.get(j);
    }
    
  }    

  public UndirectedGraph (final InputStream is) {

    final Scanner scanner = new Scanner(is);

    try {
      final Pattern pattern = Pattern.compile(GRAPH_FORMAT_PATTERN);
      do {
        scanner.next(pattern);
        scanner.nextLine();
      } while(true);
    } catch (final NoSuchElementException ex) {}

    final String initialToken = scanner.next();

    if (initialToken.compareTo("p") == 0) {

      scanner.next("edge");

      this.n = scanner.nextInt();
      this.m = scanner.nextInt();

    } else {

      final int rows = Integer.parseInt(initialToken); // Número de filas
      final int columns = scanner.nextInt(); // Número de columnas
      n = (rows > columns) ? rows : columns;
      m = scanner.nextInt(); // Número de aristas

    }

    vertex1 = new int[m];
    vertex2 = new int[m];

    neighbours = new List[n];
    for (int i = 1; i <= n; ++i) {
      neighbours[i - 1] = new ArrayList<>();
    }

    if (initialToken.compareTo("p") == 0)
      dataScan(scanner);
    else
      mtxScan(scanner);

  }

  private void dataScan (final Scanner scanner) {

    try {

      if (!scanner.hasNextLine()) {
        System.err.println("Formato de fichero de descripción de grafos incorrecto");
        throw new NoSuchElementException();
      }

      for (int j = 1; scanner.hasNextLine();) {

        final String initialToken = scanner.next();
        if (initialToken.compareTo("e") == 0) {

          final int v1 = scanner.nextInt();
          final int v2 = scanner.nextInt();

          vertex1[j - 1] = v1;
          vertex2[j - 1] = v2;

          if (!neighbours[v1 - 1].contains(v2)) {
            neighbours[v1 - 1].add(v2);
          }

          if (!neighbours[v2 - 1].contains(v1)) {
            neighbours[v2 - 1].add(v1);
          }

          ++j;

        }

        scanner.nextLine();

      }

    } catch (final NumberFormatException ex) {
      System.out.println(ex);
      throw ex;
    }

  }

  private void mtxScan (final Scanner scanner) {

    for (int j = 1; j <= m; ++j) {

      scanner.nextLine();
      final int v1 = scanner.nextInt();
      final int v2 = scanner.nextInt();

      vertex1[j - 1] = v1;
      vertex2[j - 1] = v2;

      if (!neighbours[v1 - 1].contains(v2))
        neighbours[v1 - 1].add(v2);
      if (!neighbours[v2 - 1].contains(v1))
        neighbours[v2 - 1].add(v1);

    }

  }

  public int getVertex1 (final int j) {
    if ((j >= 1) && (j <= m))
      return vertex1[j - 1];
    else
      throw new IllegalArgumentException("Índice de arista inválido");
  }

  public int getVertex2 (final int j) {
    if ((j >= 1) && (j <= m))
      return vertex2[j - 1];
    else
      throw new IllegalArgumentException("Índice de arista inválido");
  }

  public boolean inEdge (final int v, final int j) {
    if ((v < 1) || (v > n))
      throw new IllegalArgumentException("Índice de nodo inválido");
    if ((j < 1) || (j > m))
      throw new IllegalArgumentException("Índice de arista inválido");
    return ((getVertex1(j) == v) || (getVertex2(j) == v));
  }

  public boolean areNeighbours (final int u, final int v) {
    if ((u < 1) || (u > n))
      throw new IllegalArgumentException("Índice de nodo inválido");
    if ((v < 1) || (v > n))
      throw new IllegalArgumentException("Índice de nodo inválido");
    return neighbours[u - 1].stream().anyMatch((k) -> (k == v));
  }

  public int vertices () { return n; }
  public int edges    () { return m; }

  public int degree (final int v) {
    if ((v < 1) || (v > n))
      throw new IllegalArgumentException("Índice de nodo inválido");
    return neighbours[v - 1].size();
  }

  public List<Integer> neighbours (final int v) {
    if ((v < 1) || (v > n))
      throw new IllegalArgumentException("Índice de nodo inválido");
    return neighbours[v - 1];
  }

  void print (final PrintStream os) {
    os.println("p edge " + n + " " + m);
    for (int j = 1; j <= m; ++j) {
      final int v1 = getVertex1(j);
      final int v2 = getVertex2(j);
      os.println("e " + v1 + " " + v2);
    }
  }
  
  public UndirectedGraph complement () {
    
    final int _n = this.vertices();
    final int _m = (n * (n - 1)) / 2 - this.edges();
    
    final Map<Integer, List<Integer>> _neighbours = new HashMap<>();
    for (int j = 1; j <= n; ++j)
      _neighbours.put(j, new ArrayList<>());
    final List<Integer> _vertex1 = new ArrayList<>(_m);
    final List<Integer> _vertex2 = new ArrayList<>(_m);
    
    for (int i = 1; i < n; ++i) {
      
      for (int j = i + 1; j <= n; ++j) {
        
        if (!areNeighbours(i, j)) {
          _vertex1.add(i);
          _vertex2.add(j);
          _neighbours.get(i).add(j);
          _neighbours.get(j).add(i);
        }
        
      }
      
    }
    
    return new UndirectedGraph(_neighbours, _vertex1, _vertex2);
    
  }  

}