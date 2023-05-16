package cnfcomponents;
/**
 *
 * @author MAZ
 */
import java.util.List;
import java.util.ArrayList;
//
public final class DisjunctiveBooleanClause {

  private final List<BooleanLiteral> literals;

  public DisjunctiveBooleanClause (final BooleanLiteral literal) {
    this.literals = new ArrayList<>(1);
    this.literals.add(literal);
  }

  public DisjunctiveBooleanClause (final BooleanLiteral literal1,
                                   final BooleanLiteral literal2) {
    this.literals = new ArrayList<>(2);
    this.literals.add(literal1);
    this.literals.add(literal2);
  }

  public DisjunctiveBooleanClause (final BooleanLiteral literal1,
                                   final BooleanLiteral literal2,
                                   final BooleanLiteral literal3) {
    this.literals = new ArrayList<>(3);
    this.literals.add(literal1);
    this.literals.add(literal2);
    this.literals.add(literal3);
  }
  
  public DisjunctiveBooleanClause (final List<BooleanLiteral> literals) {
    this.literals = new ArrayList<>(literals.size());
    literals.forEach((literal) -> {
      this.literals.add(literal);
    });
  }
  
  public DisjunctiveBooleanClause (final DisjunctiveBooleanClause c) {
    this(c.literals);
  }   

  String print () {

    String output = "";

    output = literals.stream()
            .map((literal) -> Integer.toString(literal.getIndex()) + " ")
            .reduce(output, String::concat);
    output += "0";

    return output;

  }

  String printWithLabels () {

    String output = "";

    output = literals.stream()
            .map((literal) -> Integer.toString(literal.getIndex()) + " (" + literal.getLabel() + ") ")
            .reduce(output, String::concat);
    output += "0";

    return output;

  }
  
  public List<BooleanLiteral> getLiterals () {
      return this.literals;
  }

}