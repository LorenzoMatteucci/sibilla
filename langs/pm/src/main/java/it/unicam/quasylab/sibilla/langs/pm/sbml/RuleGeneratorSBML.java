package it.unicam.quasylab.sibilla.langs.pm.sbml;

import it.unicam.quasylab.sibilla.core.models.EvaluationEnvironment;
import it.unicam.quasylab.sibilla.core.models.pm.*;
import it.unicam.quasylab.sibilla.core.models.pm.util.PopulationRegistry;
import it.unicam.quasylab.sibilla.langs.pm.ExpressionEvaluator;
import org.apache.commons.math3.random.RandomGenerator;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class RuleGeneratorSBML {

    private PopulationRegistry registry;
    private EvaluationEnvironment evaluationEnvironment;

    //private ExpressionEvaluator expressionEvaluator;
    private HashMap<String, Double> context;

    private List<PopulationRule> rules;

    private String ruleName;

    ListOf<Reaction> reactionList;


    public RuleGeneratorSBML(PopulationRegistry registry, EvaluationEnvironment evaluationEnvironment) {
        this.registry = registry;
        this.evaluationEnvironment = evaluationEnvironment;
    }

    public List<PopulationRule> getRulesFromReactionList(ListOf<Reaction> reactionList){
        ArrayList<PopulationRule> populationRuleList= new ArrayList<>();
        for (Reaction r : reactionList) {
            populationRuleList.add(getRuleFromReaction(r));
        }
        return populationRuleList;
    }

    public List<PopulationRule> getRulesFromRulesList(){

        //  TO DO ?

        return null;
    }


    /*
            PopulationRule rule_S_G_A = new ReactionRule(
                "S->A",
                new Population[] { new Population(S), new Population(A)} ,
                new Population[] { new Population(G), new Population(A)},
                (t,s) -> (1-PROB_ASINT)*s.getOccupancy(S)* PROB_TRANSMISSION*LAMBDA_MEET *(s.getOccupancy(A)/N));

     */




    /*
    	public ReactionRule(String name, Population[] reactants, Population[] products, RatePopulationFunction rateFunction) {
		this(name,null,reactants,products,rateFunction);
	}
     */

    private PopulationRule getRuleFromReaction(Reaction r){
        String name = getRuleName(r);
        Population[] pre = getReactionElement(r.getListOfReactants());
        Population[] post = getReactionElement(r.getListOfProducts());
        //Function<?,?> rate = getRateFunction();
        //ReactionRule reactionRule = new ReactionRule();
        RatePopulationFunction rateFunction = null;
        return new ReactionRule(name,pre,post,rateFunction);
    }

    private String getRuleName(Reaction r){
        String name = "";
        ListOf<SpeciesReference> reactantsList = r.getListOfReactants();
        ListOf<SpeciesReference> productsList = r.getListOfProducts();
        for (SpeciesReference reactant : reactantsList) name += reactant.getSpecies() +"_";
        name += name.substring(0, name.length() - 1) + " --> ";
        for (SpeciesReference product : productsList) name += product.getSpecies() +"_";
        return name.substring(0, name.length() - 1);
    }


    /**
     *
     * Method used to obtain a collection of Population
     * in order to obtain a pre and a post Population
     * in a transaction rule
     *
     * @param reactionElements : a list of the reactionElements
     * @return a collection o Population species
     */
    private Population[] getReactionElement(ListOf<SpeciesReference> reactionElements){
        Population[] speciesInvolved = new Population[reactionElements.size()];
        for (int i = 0; i < reactionElements.size()-1; i++) {
            speciesInvolved[i] = new Population(registry.indexOf(reactionElements.getElementName()));
        }
        return speciesInvolved;
    }


    private Function<Population,Population> getRateFunction(){
        return null;
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
