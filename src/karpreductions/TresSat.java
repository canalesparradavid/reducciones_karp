/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package karpreductions;

import java.util.List;

import cnfcomponents.BooleanLiteral;
import cnfcomponents.BooleanVariable;
import cnfcomponents.CNFBooleanFormula;
import cnfcomponents.DisjunctiveBooleanClause;
import java.util.ArrayList;


/**
 *
 * @author David
 */
public class TresSat implements KarpReduction<CNFBooleanFormula, CNFBooleanFormula>{
    
    protected CNFBooleanFormula phi;
    
    TresSat() {
        phi = new CNFBooleanFormula();
    }
    
    @Override
    public CNFBooleanFormula transform (CNFBooleanFormula instances) {
        // Extraigo las clausulas a patir de la instancias del problema
        List<DisjunctiveBooleanClause> clauses = instances.getClauses();
        
        // Para cada clausula aplico el 3SAT
        for (DisjunctiveBooleanClause clause : clauses) {
            solve(clause.getLiterals());
        }
        
        return phi;
    }
    
    protected void solve (List<BooleanLiteral> literals) {
        if (literals.size() == 1) {
            // Si la lista contiene un unico literal se desdoblan introduciendo dos nuevas variables
            
            BooleanLiteral L = literals.get(0);
            BooleanLiteral p = phi.newVariable().getPositiveLiteral();
            BooleanLiteral q = phi.newVariable().getPositiveLiteral();
            
            phi.addClause(new DisjunctiveBooleanClause(L, p, q));
            phi.addClause(new DisjunctiveBooleanClause(L,p.not(), q));
            phi.addClause(new DisjunctiveBooleanClause(L, p, q.not()));
            phi.addClause(new DisjunctiveBooleanClause(L, p.not(), q.not()));
        }
        else if (literals.size() == 2) {
            // Si la lista contiene dos literales se desdoblan introduciendo una nueva variable
            
            BooleanLiteral L1 = literals.get(0);
            BooleanLiteral L2 = literals.get(1);
            BooleanLiteral p = phi.newVariable().getPositiveLiteral();
            
            phi.addClause(new DisjunctiveBooleanClause(L1, L2, p));
            phi.addClause(new DisjunctiveBooleanClause(L1, L2, p.not()));
        }
        else if (literals.size() == 3) {
            // Si la lista contiene tres literales no se modifica
            
            phi.addClause(new DisjunctiveBooleanClause(literals));
        }
        else if (literals.size() > 3) {
            // Si la lista tiene mas de tres literales estos de tratan en dos pasos
            
            BooleanLiteral s = EQV(literals.get(0), literals.get(1));
            
            for (int i = 2; i < literals.size(); i++) {
                s = EQV(s, literals.get(i));
            }
        }
    }
    
    private BooleanLiteral EQV(BooleanLiteral p, BooleanLiteral q) {
        /*
            // Esto crea una nueva variable x que evalue (P ∨ Q) siendo P y Q dos literales sucesivos de la lista de entrada
                (NOT x ∨ P ∨ Q)   ---> Esto asegura que si x1 es verdadero, al menos uno de L1 o L2 debe ser verdadero.
                (x ∨ NOT P)       ---> Esto asegura que si L1 es falso, x1 debe ser falso también.
                (x ∨ NOT Q)       ---> Esto asegura que si L2 es falso, x1 debe ser falso también.

            // Esto realiza la doble equivalencia s↔x
                (s ∨ NOT P ∨ NOT Q)
                (NOT s ∨ P ∨ Q)
        */
        
        BooleanLiteral x = phi.newVariable().getPositiveLiteral();
        BooleanLiteral s = phi.newVariable().getPositiveLiteral();

        phi.addClause(new DisjunctiveBooleanClause(x.not(), p, q));         // (NOT x ∨ P ∨ Q)
        phi.addClause(new DisjunctiveBooleanClause(s, p.not(), q.not()));   // (s ∨ NOT P ∨ NOT Q)
        phi.addClause(new DisjunctiveBooleanClause(s.not(), p, q));         // (NOT s ∨ P ∨ Q)

        List<BooleanLiteral> literals = new ArrayList();

        // (x ∨ NOT P)
        literals.add(x);
        literals.add(p.not());
        solve(literals);
        literals.clear();

        // (x ∨ NOT Q)
        literals.add(x);
        literals.add(q.not());
        solve(literals);
        literals.clear();

        return s;
        }
}
