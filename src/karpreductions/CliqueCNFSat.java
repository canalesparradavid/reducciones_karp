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
        List<BooleanLiteral> X = new ArrayList();
        for (int i = 1; i <= n; i++) {
            X.add(phi.newVariable().getNegativeLiteral());
        }
        
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
}
