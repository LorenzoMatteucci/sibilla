package it.unicam.quasylab.sibilla.langs.pm.sbml;

import it.unicam.quasylab.sibilla.core.models.pm.PopulationRule;
import it.unicam.quasylab.sibilla.core.models.pm.util.PopulationRegistry;
import it.unicam.quasylab.sibilla.langs.pm.ExpressionEvaluator;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class RuleGeneratorSBML {

    private PopulationRegistry registry;
    private ExpressionEvaluator expressionEvaluator;
    private HashMap<String, Double> context;

    private List<PopulationRule> rules;

    private String ruleName;

    ListOf<Reaction> reactionList;


    public Function<Double,Double> prova(){
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
