grammar CalcGrammar;

@header{
    package at.tugraz.ist.catroid.formulaeditor;
    import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
    import at.tugraz.ist.catroid.formulaeditor.Operators;
    import java.util.Queue;
    import java.util.concurrent.LinkedBlockingQueue;
}
@lexer::header{package at.tugraz.ist.catroid.formulaeditor;}

@lexer::members {

    private int lexerError = -1;
    
    @Override
    public void reportError(RecognitionException e){
        if(lexerError != -1)
            return;

        lexerError = e.charPositionInLine;
        throw new RuntimeException(e);
    }

    public int getLexerError() {
        return lexerError;
        }
}

@parser::members{

    private int parserErrorCount = 0;
    private List<String> parserErrorMessages = null;
    public List<String> testString = new ArrayList<String>();

    private Queue<FormulaElement> formulaStack = new LinkedBlockingQueue<FormulaElement>();
    private FormulaElement currentFormulaElement;

    private int errorCharacterPosition = -1;
    private CalcGrammarLexer lexer;
    private String formulaString = "";

    @Override
    public void reportError(RecognitionException e){
        
        if(errorCharacterPosition != -1)
            return;

        errorCharacterPosition = e.charPositionInLine;

        if(errorCharacterPosition == -1)
            errorCharacterPosition = formulaString.length()-1;
        
        throw new RuntimeException(e);
    }

    public int getErrorCharacterPosition() {
        return errorCharacterPosition;
    }

    public static CalcGrammarParser getFormulaParser(String formulaString)
    {
        CharStream cs = new ANTLRStringStream(formulaString);
	CalcGrammarLexer lexer = new CalcGrammarLexer(cs);
	CommonTokenStream tokens = new CommonTokenStream(lexer);
        CalcGrammarParser parser= new CalcGrammarParser(tokens);
        parser.setLexer(lexer);
        parser.formulaString = formulaString;
	return parser;
    }

    public void setLexer(CalcGrammarLexer lexer){
        this.lexer = lexer;
    }
    

    public FormulaElement parseFormula()
    {
        FormulaElement parsedFormula = null;
        try {
            parsedFormula = formula();
        }
        catch(RuntimeException re){
            if(lexer.getLexerError() != -1){
                errorCharacterPosition = lexer.getLexerError();
                return null;
            }
            if(errorCharacterPosition != -1){
                return null;
            }
        }
        catch(RecognitionException re){
            return null;
        }
        catch(StackOverflowError soe){
            errorCharacterPosition = -2;
            return null;
        }

        return parsedFormula;
    }

    private FormulaElement findLowerPriorityOperatorElement(Operators currentOp, FormulaElement curElem)
    {
        FormulaElement returnElem = curElem.getParent();
        FormulaElement notNullElem = curElem;
        boolean goon = true;
        
        while(goon)
        {
            if(returnElem == null){
                goon=false;
                returnElem = notNullElem;
            } 
            else{
                Operators parentOp  = Operators.getOperatorByValue(returnElem.getValue());
                int compareOp = parentOp.compareOperatorTo(currentOp);
                if(compareOp < 0){
                    goon = false;
                    returnElem = notNullElem;
                }
                else{
                    notNullElem = returnElem;
                    returnElem = returnElem.getParent();
                }  
            }
        }
        return returnElem;
    }


    public void handleOperator(String operator, FormulaElement curElem, FormulaElement newElem)
    {
//        System.out.println("handleOperator: operator="+operator + " curElem="+curElem.getValue() + " newElem="+newElem.getValue());

        if(curElem.getParent() == null)
        {
            new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, curElem, newElem);
//            System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
            return;
        }

        Operators parentOp  = Operators.getOperatorByValue(curElem.getParent().getValue());
        Operators currentOp = Operators.getOperatorByValue(operator);
        
        int compareOp = parentOp.compareOperatorTo(currentOp);

        if(compareOp >= 0)
        {
            FormulaElement newLeftChild =  findLowerPriorityOperatorElement(currentOp,curElem);
//            System.out.println("findLowerPriorityOperatorElement: " + newLeftChild.getValue());
            FormulaElement newParent =  newLeftChild.getParent();
            
            if(newParent != null)
            {
                newLeftChild.replaceWithSubElement(operator,newElem);
            }
            else
            {
                new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, newLeftChild, newElem);
            }
        }
        else
        {
            curElem.replaceWithSubElement(operator, newElem);
        }

//        System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
    }

    public String internalCommaSeperatedDouble(String value)
    {
        return value.replace(',', '.');
    }

}



/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/


formula returns [FormulaElement formulaTree]: 
    term_list 
        {
            $formulaTree = $term_list.termListTree;
        }
    EOF;

term_list returns [FormulaElement termListTree] : 
    {
        FormulaElement curElem;
    }
    (firstTermTree=term 
        {
            $termListTree = $firstTermTree.termTree;
            curElem = $termListTree;
        }
    ) 
    (operator loopTermTree=term 
        {
            handleOperator($operator.operatorString,curElem,$loopTermTree.termTree);
            curElem=$loopTermTree.termTree;
            $termListTree = curElem.getRoot();
        })* ;

term returns [FormulaElement termTree] : 
    {
        $termTree = new FormulaElement(FormulaElement.ElementType.VALUE, null, null); 
        FormulaElement curElem = $termTree;
    }
    (MINUS {
            curElem = new FormulaElement(FormulaElement.ElementType.VALUE, null, $termTree, null, null);
            $termTree.replaceElement(FormulaElement.ElementType.OPERATOR, $MINUS.getText(), null, curElem);
         }

    )? 
    (NUMBER
        {
            String number = internalCommaSeperatedDouble($NUMBER.getText());
            curElem.replaceElement(FormulaElement.ElementType.VALUE, number);
        }
    | '(' term_list ')'
        {
            curElem.replaceElement(FormulaElement.ElementType.BRACKET,null,null,$term_list.termListTree);
        }
    | variableOrFunction 
        {
            curElem.replaceElement($variableOrFunction.variableOrFunctionTree);
        }
    );

variableOrFunction returns [FormulaElement variableOrFunctionTree]:
      CONSTANT
            {
                $variableOrFunctionTree = new FormulaElement(FormulaElement.ElementType.CONSTANT, $CONSTANT.getText(), null, null, null);
            }
      
      | ID  
            {
                FormulaElement leftChild  = null;
                FormulaElement rightChild = null;
            }
            '(' (leftChildTree=term_list 
                    {
                        leftChild = $leftChildTree.termListTree;
                    }
                    (',' rightChildTree=term_list
                        {
                            rightChild = $rightChildTree.termListTree;
                        }
                    )?
                ) ')'
            {
                $variableOrFunctionTree = new FormulaElement(FormulaElement.ElementType.FUNCTION, $ID.getText(), null, leftChild, rightChild);
            }

        | SENSOR
            {
                $variableOrFunctionTree = new FormulaElement(FormulaElement.ElementType.SENSOR, $SENSOR.getText(), null, null, null);
            }
        | UPID
            {
                $variableOrFunctionTree = new FormulaElement(FormulaElement.ElementType.VARIABLE, $UPID.getText(), null, null, null);
            }
;

operator returns [String operatorString]: 
    MULOP 
        {
            $operatorString = $MULOP.getText();
        }
    | PLUS
        {
            $operatorString = $PLUS.getText();
        }    
    | MINUS
        {
           $operatorString = $MINUS.getText();
        };


/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

/*ignored things*/ 
WS  : (' ' | '\r' | '\t' | '\u000C' | '\n') {$channel=HIDDEN;}
    ;
//COMMENT
//    : '/*' .* '*/' {$channel=HIDDEN;}
//    ;

/*Constants*/
CONSTANT : 'pi'|'e';

/*operators*/
fragment LT : '<';
fragment GT : '>';
RELOP    : (LT|(LT'=')|GT|(GT'=')|('='));
MULOP    : '*'|'/'|'%'|'^';
PLUS     : '+';
MINUS    : '-';
OR       : '|';
NOT      : '!';
LAND     : '&';

/*Numerical literals*/
NUMBER    : DECINT ;
fragment DECINT : (DIGIT)+ ('.' (DIGIT)+)? ;
fragment DIGIT    : '0'..'9';

/*IDENTIFIER*/
ID        :   LETTER(LETTER)*;
SENSOR    :   'X_ACCELERATION_'|'Y_ACCELERATION_'|'Z_ACCELERATION_'|'AZIMUTH_ORIENTATION_'|'PITCH_ORIENTATION_'|'ROLL_ORIENTATION_'|'COSTUME_X_'|'COSTUME_Y_'|'COSTUME_GHOSTEFFECT_'|'COSTUME_BRIGHTNESS_'|'COSTUME_SIZE_'|'COSTUME_ROTATION_'|
				'COSTUME_LAYER_';
UPID      :   UPPERCASE(LETTER)*;
fragment LETTER    : ('a'..'z');
fragment UPPERCASE : ('A'..'Z');

