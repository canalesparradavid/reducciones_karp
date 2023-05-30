package karpreduction;
/**
 *
 * @author MAZ
 */
import cnfcomponents.BooleanLiteral;
import cnfcomponents.CNFBooleanFormula;
import cnfcomponents.DisjunctiveBooleanClause;
import java.util.ArrayList;
import java.util.List;

public final class CNFSATto3SAT implements KarpReduction<CNFBooleanFormula, CNFBooleanFormula> {

    protected CNFBooleanFormula newphi;
    
    @Override
    public CNFBooleanFormula transform (final CNFBooleanFormula phi) {
        newphi = new CNFBooleanFormula();
      
        // Extraigo las clausulas a patir de la instancias del problema
        List<DisjunctiveBooleanClause> clauses = phi.getClauses();

        // Para cada clausula aplico el 3SAT
        for (DisjunctiveBooleanClause clause : clauses) {
            solve(clause.getLiterals());
        }

        return newphi;
    }
    
    /*
       * ACCION: solve
       * ENTRDA: Una lista de literales
       * MODIFICA: Añade a la formula booleana una serie de clausulas de 3 literales
    */
    protected void solve (List<BooleanLiteral> literals) {
        if (literals.size() == 1) {
            // Si la lista contiene un unico literal se desdoblan introduciendo dos nuevas variables
            
            BooleanLiteral L = literals.get(0);
            BooleanLiteral p = newphi.newVariable().getPositiveLiteral();
            BooleanLiteral q = newphi.newVariable().getPositiveLiteral();
            
            newphi.addClause(new DisjunctiveBooleanClause(L, p, q));
            newphi.addClause(new DisjunctiveBooleanClause(L,p.not(), q));
            newphi.addClause(new DisjunctiveBooleanClause(L, p, q.not()));
            newphi.addClause(new DisjunctiveBooleanClause(L, p.not(), q.not()));
        }
        else if (literals.size() == 2) {
            // Si la lista contiene dos literales se desdoblan introduciendo una nueva variable
            
            BooleanLiteral L1 = literals.get(0);
            BooleanLiteral L2 = literals.get(1);
            BooleanLiteral p = newphi.newVariable().getPositiveLiteral();
            
            newphi.addClause(new DisjunctiveBooleanClause(L1, L2, p));
            newphi.addClause(new DisjunctiveBooleanClause(L1, L2, p.not()));
        }
        else if (literals.size() == 3) {
            // Si la lista contiene tres literales no se modifica
            
            newphi.addClause(new DisjunctiveBooleanClause(literals));
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
    
    /*
     * ACCION: solve
     * ENTRDA: Un literal
     * MODIFICA: Añade a la formula booleana una serie de clausulas de 3 literales
    */
    private void solve (BooleanLiteral a) {
        List<BooleanLiteral> literals = new ArrayList();
        
        literals.add(a);
        
        solve(literals);
    }
    
    /*
     * ACCION: solve
     * ENTRDA: Dos literales
     * MODIFICA: Añade a la formula booleana una serie de clausulas de 3 literales
    */
    private void solve (BooleanLiteral a, BooleanLiteral b) {
        List<BooleanLiteral> literals = new ArrayList();
        
        literals.add(a);
        literals.add(b);
        
        solve(literals);
    }
    
    /*
     * ACCION: TDISJUNCTION
     * ENTRDA: Dos literales
     * MODIFICA: Añade una serie de clausulas de 3 literales a la formula booleana
     * DEVUELVE: Una nueva variable s
    */
    private BooleanLiteral TDISJUNCTION(BooleanLiteral p, BooleanLiteral q) {
        /*
            S <-> P v Q
                (S v NOT P)
                (S v NOT Q)
                (NOT S v P v Q)
        */
        
        BooleanLiteral s = newphi.newVariable().getPositiveLiteral();

        // (NOT s ∨ P ∨ Q)
        newphi.addClause(new DisjunctiveBooleanClause(s.not(), p, q));         
 
        // (s ∨ NOT P)
        solve(s, p.not());

        // (s ∨ NOT Q)
        solve(s, q.not());

        return s;
    }
}
