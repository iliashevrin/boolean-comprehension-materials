package formula;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import metrics.LiteralCount;

public class Formula implements Serializable {
	
	private static final long serialVersionUID = 531437675627199292L;
	public static final String AND = "&";
	public static final String OR = "|";
	public static final String IFF = "<->";
	public static final String IMPLIES = "->";
	public static final String NOT = "!";

	private boolean negated;
	private String literal;
	private List<Formula> elements = new ArrayList<>();
	private String op;
	private boolean isOne = false;
	private boolean isZero = false;
	
	public Formula(boolean negated, String literal) {
		this.negated = negated;
		this.literal = literal;
	}
	
	public Formula(boolean negated, List<Formula> elements, String op) {
		this.negated = negated;
		this.elements = elements;
		this.op = op;
	}
	
	public Formula(Formula other) {
		populate(other);
	}

	public Formula negate() {
		Formula negatedInstance = new Formula(this);
		negatedInstance.negated = !this.negated;
		return negatedInstance;
	}
	
	private void populate(Formula other) {
		this.negated = other.negated;
		this.literal = other.literal;
		this.elements = new ArrayList<>(other.elements);
		this.op = other.op;
	}
	
	public void mutateNegation(Random r) {
		if (r.nextBoolean()) {
			this.negated = !this.negated;
		}
		this.elements.forEach(e -> e.mutateNegation(r));
	}
	
	public void mutateOperator(Random r) {
		if (r.nextBoolean()) {
			if (this.op != null && isAndOr()) {
				this.op = getDualOp();
			} else if (this.op != null && this.op == IMPLIES) {
				Formula temp = this.elements.get(0);
				this.elements.set(0, this.elements.get(1));
				this.elements.set(1, temp);
			}
		}
		this.elements.forEach(e -> e.mutateOperator(r));
	}

	
	public Formula simplify() {
		
		if (this.elements.size() == 0) {
			return this;
		}
		
		if (this.elements.size() == 1) {
			Formula singleElement = this.elements.get(0).simplify();
			return this.negated ? singleElement.negate() : singleElement;
		}
		
		List<Formula> newElements = new ArrayList<>();
				
		for (Formula formula : this.elements.stream().distinct().map(f -> f.simplify()).toList()) {
			if (this.op.equals(formula.op) && !formula.negated && this.isAndOr()) {
				newElements.addAll(formula.getElements());
			} else {
				newElements.add(formula);
			}
		}

		if (isAndOr()) {
			newElements = new ArrayList<>(newElements.stream().distinct().toList());
			
			if (newElements.size() == 1) {
				return this.negated ? newElements.get(0).negate() : new Formula(newElements.get(0));
			}
			
			
			boolean hasOne = newElements.stream().anyMatch(e -> e.isOne);
			boolean hasZero = newElements.stream().anyMatch(e -> e.isZero);
			
			List<Formula> positive = newElements.stream().filter(e -> !e.isNegated()).toList();
			List<Formula> negative = newElements.stream().filter(e -> e.isNegated()).toList();
			List<Formula> union = positive.stream().filter(e -> negative.contains(e.negate())).toList();
			
			
			if (AND.equals(this.op)) {
				if (hasZero || !union.isEmpty()) {
					this.isZero = true;
					this.isOne = false;
				} else if (hasOne) {
					newElements = new ArrayList<>(newElements.stream().filter(e -> !e.isOne).toList());
				}
			} else if (OR.equals(this.op)) {
				if (hasOne || !union.isEmpty()) {
					this.isZero = false;
					this.isOne = true;
				} else if (hasZero) {
					newElements = new ArrayList<>(newElements.stream().filter(e -> !e.isZero).toList());
				}	
			}
			
		}
		
		return new Formula(this.negated, newElements, this.op);
	}


	public boolean isNegated() {
		return negated;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	public List<Formula> getElements() {
		return this.elements;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
	
	public boolean isAndOr() {
		return AND.equals(op) || OR.equals(op);
	}
	
	public String getDualOp() {
		if (AND.equals(op)) return OR;
		if (OR.equals(op)) return AND;
		throw new IllegalArgumentException();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(new HashSet<>(elements), literal, negated, op);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Formula other = (Formula) obj;
		
		return this.toString().equals(other.toString());
//		return elements.equals(other.elements) && Objects.equals(literal, other.literal)
//				&& negated == other.negated && Objects.equals(op, other.op);
	}
	
	public boolean semanticEquals(Formula other) {
		
		return this.compare(other, new BiPredicate<Boolean, Boolean>() {
			
			@Override
			public boolean test(Boolean thisRes, Boolean otherRes) {
				return !thisRes.equals(otherRes);
			}
		});
		
	}
	
	public boolean semanticSubset(Formula other) {
		
		return this.compare(other, new BiPredicate<Boolean, Boolean>() {
			
			@Override
			public boolean test(Boolean thisRes, Boolean otherRes) {
				return thisRes && !otherRes;
			}
		});
		
	}
	
	private boolean compare(Formula other, BiPredicate<Boolean, Boolean> stopPredicate) {
		
		LiteralCount thisLiteralCount = new LiteralCount();
		thisLiteralCount.count(this);
		
		LiteralCount otherLiteralCount = new LiteralCount();
		otherLiteralCount.count(other);
		
		thisLiteralCount.literals.addAll(otherLiteralCount.literals);
		
		List<String> literals = new ArrayList<>(thisLiteralCount.literals);
			
		for (int assgn = 0; assgn < Math.pow(2, literals.size()); assgn++) {
			
			Map<String, Boolean> assgnMap = new HashMap<>();

	        for (int i = literals.size() - 1; i >= 0; i--) {
	             assgnMap.put(literals.get(i), (assgn & (1 << i)) != 0);
	        }

	        Boolean thisRes = this.evaluate(assgnMap);
	        Boolean otherRes = other.evaluate(assgnMap);
	        
	        if (stopPredicate.test(thisRes, otherRes)) {
	        	return false;
	        }
		}
		
		return true;
	}
	

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		if (this.negated) {
			sb.append("!");
		}
		if (this.literal != null) {
			sb.append(literal);
		} else {
			
			List<String> toStringList;
			if (this.op.equals(AND) || this.op.equals(OR)) {
				toStringList = elements.stream().sorted(new Comparator<Formula>() {

					@Override
					public int compare(Formula arg0, Formula arg1) {
						return arg0.toLexicographicComparisonString().compareTo(arg1.toLexicographicComparisonString());
					}
				}).map(e -> e.toString()).toList();
			} else {
				toStringList = elements.stream().map(e -> e.toString()).toList();
			}
			
			sb.append("(");
			sb.append(String.join(this.op, toStringList));
			sb.append(")");
		}
		return sb.toString();
	}
	
	public double[] getSAT(int literalsNum, int choose) {
		
		double[] sat = new double[] {0.0, 0.0, 0.0};
		
		List<String> literals = new ArrayList<>();
		for (int i = 0; i < literalsNum; i++) {
			literals.add(String.valueOf((char)(i + 65)));
		}
		
		List<List<String>> results = new ArrayList<>();
		List<String> combinations = new ArrayList<>();
		generateCombinations(literals, choose, 0, combinations, results);
		
		// Result is a list of literals
		for (List<String> result : results) {
			
			for (int assgn = 0; assgn < Math.pow(2, choose); assgn++) {
				
				Map<String, Boolean> assgnMap = new HashMap<>();

		        for (int i = choose - 1; i >= 0; i--) {
		             assgnMap.put(result.get(i), (assgn & (1 << i)) != 0);
		        }

		        Boolean res = evaluate(assgnMap);
		        
		        if (res == null) sat[2]++;
		        else if (res) sat[0]++;
		        else sat[1]++;
			}
		}
		
		sat[0] /= results.size();
		sat[1] /= results.size();
		sat[2] /= results.size();
		
		return sat;
	}
	
	public static void generateCombinations(List<String> literals, int k, int startIndex, List<String> combination, List<List<String>> result) {
        if (k == 0) {
            result.add(new ArrayList<>(combination));
            return;
        }

        for (int i = startIndex; i <= literals.size() - k; i++) {
            combination.add(literals.get(i));
            generateCombinations(literals, k - 1, i + 1, combination, result);
            combination.remove(combination.size() - 1);
        }
    }
	
	public Boolean evaluate(Map<String, Boolean> assignment) {
		
		Boolean res = null;
		
		if (this.literal != null) {
			res = assignment.get(this.literal);
		}
		
		else if (this.op.equals(AND)) {
			if (this.elements.stream().anyMatch(e -> {Boolean b = e.evaluate(assignment); return b != null && b == false;})) {
				res = false;
			} else if (this.elements.stream().allMatch(e -> {Boolean b = e.evaluate(assignment); return b != null && b == true;})) {
				res = true;
			}
		}
		
		else if (this.op.equals(OR)) {
			if (this.elements.stream().anyMatch(e -> {Boolean b = e.evaluate(assignment); return b != null && b == true;})) {
				res = true;
			} else if (this.elements.stream().allMatch(e -> {Boolean b = e.evaluate(assignment); return b != null && b == false;})) {
				res = false;
			}
		}
		
		else if (this.op.equals(IFF)) {
			Boolean b0 = this.elements.get(0).evaluate(assignment);
			Boolean b1 = this.elements.get(1).evaluate(assignment);
			if ((b0 != null && b0 == true && b1 != null && b1 == true) ||
					(b0 != null && b0 == false && b1 != null && b1 == false)) {
				
				res = true;
				
			} else if ((b0 != null && b0 == true && b1 != null && b1 == false) ||
					(b0 != null && b0 == false && b1 != null && b1 == true)) {
				
				res = false;
			}
		}
		
		else if (this.op.equals(IMPLIES)) {
			Boolean b0 = this.elements.get(0).evaluate(assignment);
			Boolean b1 = this.elements.get(1).evaluate(assignment);
			if ((b0 != null && b0 == false) || (b1 != null && b1 == true)) {
				res = true;
			} else if (b0 != null && b0 == true && b1 != null && b1 == false) {
				res = false;
			}
		}
		
		else {
			throw new IllegalArgumentException();
		}
		
		if (this.negated && res != null) {
			return !res;
		} else {
			return res;
		}
	}
	
	public String serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(this);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
	
	public static Formula fromString(String str) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(str);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Formula o = (Formula) ois.readObject();
		ois.close();
		return o;
	}
	
	private String toLexicographicComparisonString() {
		if (this.literal != null) {
			return this.literal;
		} else {
			return this.elements
					.stream()
					.map(e -> e.toLexicographicComparisonString())
					.sorted()
					.collect(Collectors.joining());

		}
	}
	
}
