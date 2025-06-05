grammar BooleanFormula;

@header {
package language;
}

formula:
	(negated='!')? 
	(
		literal=CONST | 
		( '(' elements+=formula (op=( '|' | '&' | '->' | '<->' ) elements+=formula)+ ')' )
	);

CONST:
  	'^'?('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;
  
 WS: [ \t]+ -> skip ;