package it.unicam.quasylab.sibilla.langs.pm.sbml;

import it.unicam.quasylab.sibilla.core.models.EvaluationEnvironment;
import it.unicam.quasylab.sibilla.core.models.MeasureFunction;
import it.unicam.quasylab.sibilla.core.models.pm.PopulationState;
import it.unicam.quasylab.sibilla.core.models.pm.util.PopulationRegistry;
import it.unicam.quasylab.sibilla.langs.pm.PopulationModelParser;
import org.sbml.jsbml.ASTNode;

import java.util.function.BiFunction;

public class RuleGeneratorSBMLRemake {

    EvaluationEnvironment evaluationEnvironment;
    PopulationRegistry populationRegistry;

    public RuleGeneratorSBMLRemake( EvaluationEnvironment ee, PopulationRegistry pr){
        evaluationEnvironment = ee;
        populationRegistry = pr;
    }

    public MeasureFunction<PopulationState> getRuleFromASTNode(ASTNode tree){
        if(tree.getChildCount()>0){

            ASTNode leftChild = tree.getLeftChild();
            ASTNode rightChild = tree.getRightChild();

            MeasureFunction<PopulationState> leftChildFunction = getRuleFromASTNode(leftChild);
            MeasureFunction<PopulationState> rightChildFunction = getRuleFromASTNode(rightChild);

            BiFunction<Double,Double,Double> operation = getOperator(tree);

            return applyBinary(leftChildFunction,operation,rightChildFunction);
        } else {
            return getValue(tree);
        }
    }

    private MeasureFunction<PopulationState> applyBinary(MeasureFunction<PopulationState> left, BiFunction<Double, Double, Double> op, MeasureFunction<PopulationState> right) {
        return s -> op.apply(left.apply(s),right.apply(s));
    }

    public MeasureFunction<PopulationState> getValue(ASTNode node) {
        if(node.getId().equals("Parameter")){
            double value = evaluationEnvironment.get(node.getName());
            return s -> value;
        } else
        {
            int idx = populationRegistry.indexOf(node.getName());
            return s -> s.getFraction(idx);
        }
    }

    private BiFunction<Double, Double, Double> getOperator(ASTNode node){
        String operationType = node.getType().name();
        switch (operationType) {
            case "PLUS":
                return (x,y) -> x+y;
            case "MINUS":
                return (x,y) -> x-y;
            case "TIMES":
                return (x,y) -> x*y;
            case "DIVIDE":
                return (x,y) -> x/y;
            //case "REAL":
            //    throw new UnsupportedOperationException(operationType +" is not implemented");
            default:
                throw new IllegalArgumentException("Invalid operator: " + operationType);
        }
    }








            /*
        public void compile(ASTNodeCompiler compiler) throws SBMLException {
    ASTNodeValue value;
    switch (getType()) {

        case REAL:

        case INTEGER:

        case POWER:

        case RATIONAL:

        case NAME_TIME:

        case FUNCTION_DELAY:

        case NAME:

        case CONSTANT_PI:

        case CONSTANT_E:

        case CONSTANT_TRUE:

        case CONSTANT_FALSE:

        case NAME_AVOGADRO:

        case FUNCTION_RATE_OF:

        case REAL_E:

        case FUNCTION_LOG:

        case FUNCTION_ABS:

        case FUNCTION_ARCCOS:

        case FUNCTION_ARCCOSH:

        case FUNCTION_ARCCOT:

        case FUNCTION_ARCCOTH:

        case FUNCTION_ARCCSC:

        case FUNCTION_ARCCSCH:

        case FUNCTION_ARCSEC:

        case FUNCTION_ARCSECH:

        case FUNCTION_ARCSIN:

        case FUNCTION_ARCSINH:

        case FUNCTION_ARCTAN:

        case FUNCTION_ARCTANH:

        case FUNCTION_CEILING:

        case FUNCTION_COS:

        case FUNCTION_COSH:

        case FUNCTION_COT:

        case FUNCTION_COTH:

        case FUNCTION_CSC:

        case FUNCTION_CSCH:

        case FUNCTION_EXP:

        case FUNCTION_FACTORIAL:

        case FUNCTION_FLOOR:

        case FUNCTION_LN:

        case FUNCTION_MAX:

        case FUNCTION_MIN:

        case FUNCTION_POWER:

        case FUNCTION_QUOTIENT:

        case FUNCTION_REM:

        case FUNCTION_ROOT:

        case FUNCTION_SECH:

        case FUNCTION_SELECTOR:

        case FUNCTION_SIN:

        case FUNCTION_SINH:

        case FUNCTION_TAN:

        case FUNCTION_TANH:

        case FUNCTION: {
            case FUNCTION_CSYMBOL: {
                case FUNCTION_PIECEWISE:

                case LAMBDA:

                case LOGICAL_AND:
                    value = compiler.and(getChildren());
                    value.setUIFlag(getChildCount() <= 1);
                    break;
                case LOGICAL_XOR:
                    value = compiler.xor(getChildren());
                    value.setUIFlag(getChildCount() <= 1);
                    break;
                case LOGICAL_OR:
                    value = compiler.or(getChildren());
                    value.setUIFlag(getChildCount() <= 1);
                    break;
                case LOGICAL_IMPLIES:
                    value = compiler.implies(getChildren());
                    value.setUIFlag(getChildCount() <= 1);
                    break;
                case LOGICAL_NOT:
                    value = compiler.not(getLeftChild());
                    break;
                case RELATIONAL_EQ:
                    value = compiler.eq(getLeftChild(), getRightChild());
                    break;
                case RELATIONAL_GEQ:
                    value = compiler.geq(getLeftChild(), getRightChild());
                    break;
                case RELATIONAL_GT:
                    value = compiler.gt(getLeftChild(), getRightChild());
                    break;
                case RELATIONAL_NEQ:
                    value = compiler.neq(getLeftChild(), getRightChild());
                    break;
                case RELATIONAL_LEQ:
                    value = compiler.leq(getLeftChild(), getRightChild());
                    break;
                case RELATIONAL_LT:
                    value = compiler.lt(getLeftChild(), getRightChild());
                    break;
                case VECTOR:
                    value = compiler.vector(getChildren());
                    value.setUIFlag(getChildCount() <= 1);
                    break;
                default: // UNKNOWN:
                    value = compiler.unknownValue();
                    break;
            }
            value.setType(getType());
            MathContainer parent = getParentSBMLObject();
            if (parent != null) {
                value.setLevel(parent.getLevel());
                value.setVersion(parent.getVersion());
            }
            return value;
        }
         */






}
