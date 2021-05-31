package it.unicam.quasylab.sibilla.langs.pm.sbml;

import it.unicam.quasylab.sibilla.core.models.EvaluationEnvironment;
import it.unicam.quasylab.sibilla.core.models.MeasureFunction;
import it.unicam.quasylab.sibilla.core.models.pm.*;
import it.unicam.quasylab.sibilla.core.models.pm.util.PopulationRegistry;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RuleGeneratorSBML {

    EvaluationEnvironment evaluationEnvironment;
    PopulationRegistry populationRegistry;

    public RuleGeneratorSBML(EvaluationEnvironment ee, PopulationRegistry pr){
        evaluationEnvironment = ee;
        populationRegistry = pr;
    }


    /**
     *
     * Method that return the list of Population Rules
     * from a list of Reaction
     *
     * @param reactionList
     * @return The list of population rules
     */
    public ArrayList<PopulationRule> getRulesFromReactionList(List<Reaction> reactionList){
        ArrayList<PopulationRule> populationRuleList = new ArrayList<>();
        for (Reaction r : reactionList){
            populationRuleList.add(getRuleFromReaction(r));
        }
        return populationRuleList;
    }


    /**
     * Method
     * @param r
     * @return
     */
    public PopulationRule getRuleFromReaction(Reaction r) {
        String ruleName = getRuleName(r);
        Population[] pre = getReactionElement(r.getListOfReactants());
        Population[] post = getReactionElement(r.getListOfProducts());
        MeasureFunction<PopulationState> rateFunction = getRateFunctionFromASTNode(r.createKineticLaw().getMath());
        return new ReactionRule(ruleName,pre,post,(t,s) -> rateFunction.apply(s) );
    }


    private String getRuleName(Reaction r) {
        String name = "";
        ListOf<SpeciesReference> reactantsList = r.getListOfReactants();
        ListOf<SpeciesReference> productsList = r.getListOfProducts();
        for (SpeciesReference reactant : reactantsList) name += reactant.getSpecies() + "_";
        name += name.substring(0, name.length() - 1) + " --> ";
        for (SpeciesReference product : productsList) name += product.getSpecies() + "_";
        return name.substring(0, name.length() - 1);
    }


    /**
     * Method used to obtain a collection of Population
     * in order to obtain a pre and a post Population
     * in a transaction rule
     *
     * @param reactionElements : a list of the reactionElements
     * @return a collection o Population species
     */
    private Population[] getReactionElement(ListOf<SpeciesReference> reactionElements) {
        Population[] speciesInvolved = new Population[reactionElements.size()];
        for (int i = 0; i < reactionElements.size() - 1; i++) {
            speciesInvolved[i] = new Population(populationRegistry.indexOf(reactionElements.getElementName()));
        }
        return speciesInvolved;
    }

    private MeasureFunction<PopulationState> getRateFunctionFromASTNode(ASTNode tree){

        if(tree.getChildCount()>0){
            if (tree.getChildCount()==1){
                ASTNode onlyChild = tree.getChild(0);

                MeasureFunction<PopulationState> onlyChildFunction = getRateFunctionFromASTNode(onlyChild);

                Function<Double,Double> operation = getUnaryOperator(tree);

                return applyUnary(onlyChildFunction, operation);
            }
            if (tree.getChildCount()>1){
                ASTNode leftChild = tree.getLeftChild();
                ASTNode rightChild = tree.getRightChild();

                MeasureFunction<PopulationState> leftChildFunction = getRateFunctionFromASTNode(leftChild);
                MeasureFunction<PopulationState> rightChildFunction = getRateFunctionFromASTNode(rightChild);

                BiFunction<Double,Double,Double> operation = getBinaryOperator(tree);

                return applyBinary(leftChildFunction,operation,rightChildFunction);
            }
        }
        return getValue(tree);
    }

    private MeasureFunction<PopulationState> applyBinary(MeasureFunction<PopulationState> left, BiFunction<Double, Double, Double> op, MeasureFunction<PopulationState> right) {
        return s -> op.apply(left.apply(s),right.apply(s));
    }

    private MeasureFunction<PopulationState> applyUnary(MeasureFunction<PopulationState> onlyChild, Function<Double, Double> op){
        return s->op.apply(onlyChild.apply(s));
    }

    
    private MeasureFunction<PopulationState> getValue(ASTNode node) {
        if(node.getId().equals("Parameter")){
            double value = evaluationEnvironment.get(node.getName());
            return s -> value;
        } else
        {
            int idx = populationRegistry.indexOf(node.getName());
            return s -> s.getFraction(idx);
        }
    }

    private BiFunction<Double, Double, Double> getBinaryOperator(ASTNode node){
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
            case "POWER":
                return (x,y) -> Math.pow(x,y);
            case "FUNCTION_REM":
                return (x,y) -> x%y;
            case "FUNCTION_QUOTIENT":
                return (x,y) -> Math.floor(x/y);
            case "FUNCTION_MAX":
                return (x,y) -> Math.max(x,y);
            case "FUNCTION_MIN":
                return (x,y) -> Math.min(x,y);


            default:
                throw new IllegalArgumentException("Invalid operator: " + operationType);
        }
    }



    private Function<Double,Double> getUnaryOperator(ASTNode node){
        String operationType = node.getType().name();
        switch (operationType){

            //Arithmetic and Algebraic Functions

            case "FUNCTION_ROOT":
                return (x) -> Math.sqrt(x);
            case "FUNCTION_ABS":
                return (x) -> Math.abs(x);
            case "FUNCTION_LN":
                return (x) -> Math.log(x);
            case "FUNCTION_LOG":
                return (x) -> Math.log10(x);
            case "FUNCTION_FLOOR":
                return (x) -> Math.floor(x);
            case "FUNCTION_CEILING":
                return (x) -> Math.ceil(x);
            case "FUNCTION_FACTORIAL":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_EXP":
                return (x) -> Math.exp(x);

            //Trigonometric Functions

            case "FUNCTION_SIN":
                return (x) -> Math.sin(x);
            case "FUNCTION_SINH":
                return (x) -> Math.sinh(x);
            case "FUNCTION_ARCSIN":
                return (x) -> Math.asin(x);
            case "FUNCTION_ARCSINH":
                throw new IllegalArgumentException(operationType + "not implemented");
            case "FUNCTION_COS":
                return (x) -> Math.cos(x);
            case "FUNCTION_COSH":
                return (x) -> Math.cosh(x);
            case "FUNCTION_ARCCOS":
                return (x) -> Math.acos(x);
            case "FUNCTION_ARCCOSH":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_TAN":
                return (x) -> Math.tan(x);
            case "FUNCTION_TANH":
                return (x) -> Math.tanh(x);
            case "FUNCTION_ARCTAN":
                return (x) -> Math.atan(x);
            case "FUNCTION_ARCTANH":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_COT":
                return (x) -> Math.cos(x)/Math.sin(x);
            case "FUNCTION_COTH":
                return (x) -> Math.cosh(x)/Math.sinh(x);
            case "FUNCTION_ARCCOT":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_ARCCOTH":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_SEC":
                return (x) -> 1.0/Math.cos(x);
            case "FUNCTION_SECH":
                return (x) -> 1.0/Math.cosh(x);
            case "FUNCTION_ARCSEC":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_ARCSECH":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_CSC":
                return (x) -> 1.0/Math.sin(x);
            case "FUNCTION_CSCH":
                return (x) -> 1.0/Math.sinh(x);
            case "FUNCTION_ARCCSC":
                throw new UnsupportedOperationException(operationType + "not implemented");
            case "FUNCTION_ARCCSCH":
                throw new UnsupportedOperationException(operationType + "not implemented");
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

        case FUNCTION_POWER:

        case FUNCTION_SELECTOR:

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
