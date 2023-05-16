/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package karpreductions;

import java.util.List;

import cnfcomponents.BooleanLiteral;
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
            
            BooleanLiteral s = TDISJUNCTION(literals.get(0), literals.get(1));
            
            for (int i = 2; i < literals.size(); i++) {
                s = TDISJUNCTION(s, literals.get(i));
            }
            
            solve(s);
        }
    }
    
    private void solve (BooleanLiteral a) {
        List<BooleanLiteral> literals = new ArrayList();
        literals.add(a);
        solve(literals);
    }
    
    private void solve (BooleanLiteral a, BooleanLiteral b) {
        List<BooleanLiteral> literals = new ArrayList();
        literals.add(a);
        literals.add(b);
        solve(literals);
    }
    
    private BooleanLiteral TDISJUNCTION(BooleanLiteral p, BooleanLiteral q) {
        /*
            S <-> P v Q
                (S v NOT P)
                (S v NOT Q)
                (NOT S v P v Q)
        */
        
        BooleanLiteral s = phi.newVariable().getPositiveLiteral();

        // (NOT s ∨ P ∨ Q)
        phi.addClause(new DisjunctiveBooleanClause(s.not(), p, q));         
 
        // (s ∨ NOT P)
        solve(s, p.not());

        // (s ∨ NOT Q)
        solve(s, q.not());

        return s;
    }
}
