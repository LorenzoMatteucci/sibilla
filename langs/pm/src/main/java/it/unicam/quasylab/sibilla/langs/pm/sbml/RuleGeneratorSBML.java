package it.unicam.quasylab.sibilla.langs.pm.sbml;

import it.unicam.quasylab.sibilla.core.models.EvaluationEnvironment;
import it.unicam.quasylab.sibilla.core.models.MeasureFunction;
import it.unicam.quasylab.sibilla.core.models.pm.*;
import it.unicam.quasylab.sibilla.core.models.pm.util.PopulationRegistry;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;

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

        /*
                if(tree.getChildCount() == 1){
            return null;
        }
        else
         */
        if(tree.getChildCount()>0){
            ASTNode leftChild = tree.getLeftChild();
            ASTNode rightChild = tree.getRightChild();

            MeasureFunction<PopulationState> leftChildFunction = getRateFunctionFromASTNode(leftChild);
            MeasureFunction<PopulationState> rightChildFunction = getRateFunctionFromASTNode(rightChild);

            BiFunction<Double,Double,Double> operation = getOperator(tree);

            return applyBinary(leftChildFunction,operation,rightChildFunction);
        } else {
            return getValue(tree);
        }
    }

    private MeasureFunction<PopulationState> applyBinary(MeasureFunction<PopulationState> left, BiFunction<Double, Double, Double> op, MeasureFunction<PopulationState> right) {
        return s -> op.apply(left.apply(s),right.apply(s));
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
    private Function<Double,Double> getOperator(ASTNode node){

        // TO DO
        return null;
    }
 */








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
