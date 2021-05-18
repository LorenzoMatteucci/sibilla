package it.unicam.quasylab.sibilla.langs.pm.sbml;

import it.unicam.quasylab.sibilla.core.models.EvaluationEnvironment;
import it.unicam.quasylab.sibilla.core.models.pm.*;
import it.unicam.quasylab.sibilla.core.models.pm.util.PopulationRegistry;
import it.unicam.quasylab.sibilla.langs.pm.ExpressionEvaluator;
import org.apache.commons.math3.random.RandomGenerator;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RuleGeneratorSBML {

    private PopulationRegistry registry;
    private EvaluationEnvironment evaluationEnvironment;

    //private ExpressionEvaluator expressionEvaluator;
    //private HashMap<String, Double> context;

    private List<PopulationRule> rules;

    private String ruleName;

    ListOf<Reaction> reactionList;


    public RuleGeneratorSBML(PopulationRegistry registry, EvaluationEnvironment evaluationEnvironment) {
        this.registry = registry;
        this.evaluationEnvironment = evaluationEnvironment;
    }

    public List<PopulationRule> getRulesFromReactionList(ListOf<Reaction> reactionList) {
        ArrayList<PopulationRule> populationRuleList = new ArrayList<>();
        for (Reaction r : reactionList) {
            populationRuleList.add(getRuleFromReaction(r));
        }
        return populationRuleList;
    }

    public List<PopulationRule> getRulesFromRulesList() {

        //  TO DO ?

        return null;
    }


    public List<PopulationRule> getRulesFromEventList() {
        return null;
    }
    /*
            PopulationRule rule_S_G_A = new ReactionRule(
                "S->A",
                new Population[] { new Population(S), new Population(A)} ,
                new Population[] { new Population(G), new Population(A)},
                (t,s) -> (1-PROB_ASINT)*s.getOccupancy(S)* PROB_TRANSMISSION*LAMBDA_MEET *(s.getOccupancy(A)/N));

     */


    private PopulationRule getRuleFromReaction(Reaction r) {
        String name = getRuleName(r);
        Population[] pre = getReactionElement(r.getListOfReactants());
        Population[] post = getReactionElement(r.getListOfProducts());
        RatePopulationFunction rateFunction = getRatePopulationFunction(r);
        return new ReactionRule(name, pre, post, rateFunction);
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

    public double getValue(String s){
        evaluationEnvironment.get(s);
        //registry.indexOf()
        return 0.0;
    }

    public double getRateFromASTNode(ASTNode parentNode){

        if(parentNode.getChildCount()>0){

        }else{

        }
        return 0.0;
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
            speciesInvolved[i] = new Population(registry.indexOf(reactionElements.getElementName()));
        }
        return speciesInvolved;
    }


    private RatePopulationFunction getRatePopulationFunction(Reaction r) {

        return null;
    }


    class TreeVisitor {
        private ASTNode treeJSBML;
        private List<BiFunction<Double, Double, Double>> functionList;

        public TreeVisitor(ASTNode treeJSBML) {
            this.treeJSBML = treeJSBML;
        }

        public double getRateFromASTNode(ASTNode parentNode){
            return 0.0;
        }

        private void visitTree(ASTNode nodeParent) {

            // if there are no children it means the nodeParent
            // is a leaf (a type NAME --> a variable)

            // no child means no operation to do

            if (nodeParent.getChildCount() > 0) {
                List<ASTNode> nodeChildren = nodeParent.getChildren();
                ASTNode.Type nodeType = nodeParent.getType();
                for (ASTNode nodeChild : nodeChildren) {
                    if (nodeChild.getType().equals(ASTNode.Type.NAME)){
                        visitTree(nodeChild);
                    }else{

                    }
                }
                BiFunction<Double, Double, Double> nodeFunction = getNodeFunction(nodeChildren, nodeType);
                functionList.add(nodeFunction);

            }
        }

        private BiFunction<Double, Double, Double> getNodeFunction(List<ASTNode> nodeChildren, ASTNode.Type operation) {
            return null;
        }


        public List<BiFunction<Double, Double, Double>> getFunctionList() {
            BiFunction<Double, Double, Double> fun = (x, y) -> x + y;
            BiFunction<Double, Double, Double> fun1 = (uffapuzzetta, pippoCaccolino) -> uffapuzzetta + pippoCaccolino;

            double g = fun.apply(fun.apply(4.0, 5.0), 4.0);

            return functionList;
        }
    }
    /*
    @Override
    public Boolean visitRule_body(PopulationModelParser.Rule_bodyContext ctx) {
        Predicate<PopulationState> guard = expressionEvaluator.evalStatePredicate(ctx.guard);
        MeasureFunction<PopulationState> rateFunction = expressionEvaluator.evalStateFunction(ctx.rate);
        Population[] pre = expressionEvaluator.evalPopulationPattern(ctx.pre);
        Population[] post = expressionEvaluator.evalPopulationPattern(ctx.post);
        rules.add(new ReactionRule(ruleName+context.toString(),guard,pre,post,(t,s) -> rateFunction.apply(s)));
        return true;
    }
     */


}
