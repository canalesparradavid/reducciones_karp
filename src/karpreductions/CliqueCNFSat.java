/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package karpreductions;

import cnfcomponents.BooleanLiteral;
import cnfcomponents.CNFBooleanFormula;
import cnfcomponents.DisjunctiveBooleanClause;
import constraints.SCE;
import java.util.ArrayList;
import java.util.List;
import ugraph.UndirectedGraph;

/**
 *
 * @author David
 */
public class CliqueCNFSat implements KarpReduction<UndirectedGraph, CNFBooleanFormula>{
    protected int k;
    protected int n;
    protected SCE constraints;
    protected CNFBooleanFormula phi;
    
    CliqueCNFSat(int k) {
        this.k = k;
        n = 0;
        constraints = new SCE();
        phi = new CNFBooleanFormula();
    }
    
     @Override
    /*
     * FUNCION: transform
     * ENTRADA: Una grafo no dirigido
     * SALIDA: Una formula booleana
    */
    public CNFBooleanFormula transform (UndirectedGraph G) {
        n = G.vertices();
        
        // Creo la matriz X que alamcena si el vertice pertenece al Clique o no
        List<BooleanLiteral> X = getX(G);
        
        // El clique está formado por exactamente k nodos
        constraints.exactly(k, X, phi);
        
        // Para cada par de nodos si no forman un arista añado (NOT xi v NOT xj)
        for (int i = 1; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (!G.areNeighbours(i, j)) {
                    BooleanLiteral x1 = X.get(i - 1);
                    BooleanLiteral x2 = X.get(j - 1);
                    
                    phi.addClause(new DisjunctiveBooleanClause(x1.not(), x2.not()));
                }
            }
        }
        
        return phi;
    }
    
    /*
     * FUNCION: getX
     * ENTRADA: Un grafo no dirigido
     * SALIDA: Un vector con las aristas de un clique de k componentes
     */
    private List<BooleanLiteral> getClique(UndirectedGraph G) {
        List<BooleanLiteral> clique = new ArrayList();
        List<Integer> R = new ArrayList();  // Clique
        List<Integer> P = new ArrayList();  // Candidates
        List<Integer> X = new ArrayList();  // Excluded

        
        // Inicializo la lista
        for (int i = 1; i <= n; i++) {
            clique.add(phi.newVariable().getNegativeLiteral());
            P.add(i);
        }
        
        // Calculo el clique
        BronKerbosch(R, P, X, G);
        
        // Almaceno los vertices del clique
        for (int v : R) {
            clique.set(v, clique.get(v).not());
        }
        
        return clique;
    }
    
    private boolean BronKerbosch(List<Integer> R, List<Integer> P, List<Integer> X, UndirectedGraph G) {
        if (P.isEmpty() && X.isEmpty()) {
            R = new ArrayList();
            return true;
        }
        
        List<Integer> Raux = new ArrayList(R);
        List<Integer> Paux = new ArrayList(P);
        List<Integer> Xaux = new ArrayList(X);
        for (Integer v : P) {
            Raux.add(v);
            boolean found = 
                BronKerbosch(
                    Raux,
                    intersection(Paux, G.neighbours(v)),
                    intersection(Xaux, G.neighbours(v)),
                    G
                );
            Paux.remove(v);
            Xaux.add(v);
            
            if (found) {
                R = new ArrayList(Raux);
                return true;
            }
        }
        
        return false;
    }
    
    private <T> List<T> intersection(List<T> a, List<T> b) {
        List<T> c = new ArrayList(a);
        
        for (T element: b) {
            if (!c.contains(element)) {
                c.remove(element);
            }
        }
        
        return c;
    }
}
