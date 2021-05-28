package it.unicam.quasylab.sibilla.langs.pm.sbml;


import it.unicam.quasylab.sibilla.core.models.EvaluationEnvironment;
import it.unicam.quasylab.sibilla.core.models.ParametricValue;
import it.unicam.quasylab.sibilla.core.models.StateSet;
import it.unicam.quasylab.sibilla.core.models.pm.PopulationModelDefinition;
import it.unicam.quasylab.sibilla.core.models.pm.PopulationRule;
import it.unicam.quasylab.sibilla.core.models.pm.PopulationState;
import it.unicam.quasylab.sibilla.core.models.pm.ReactionRule;
import it.unicam.quasylab.sibilla.core.models.pm.util.PopulationRegistry;
import it.unicam.quasylab.sibilla.core.simulator.sampling.Measure;
import it.unicam.quasylab.sibilla.core.simulator.sampling.SimpleMeasure;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sbml.jsbml.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PopulationModelGeneratorSBML {

    private ModelSBML sbmlModel;

    public PopulationModelGeneratorSBML(String path){
        SBMLReader sbmlReader = new SBMLReader();
        SBMLDocument sbmlDocument = null;
        try {
            sbmlDocument = sbmlReader.readSBML(path);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.sbmlModel = (ModelSBML) sbmlDocument.getModel();
    }

    public PopulationModelDefinition getPopulationModelDefinition() throws IOException {

        PopulationRegistry populationRegistry = generatePopulationRegistry();
        EvaluationEnvironment evaluationEnvironment = generateEvaluationEnvironment();
        RuleGeneratorSBML ruleGeneratorSBML = new RuleGeneratorSBML(evaluationEnvironment,populationRegistry);
        ArrayList<PopulationRule> modelRules = ruleGeneratorSBML.getRulesFromReactionList(sbmlModel.getListOfReactions());

        return null;
    }

    /**
     * Method for the generation of the population registry
     * from a smbl model
     *
     * DOUBT : values in the method register represent the initial amount?
     *
     * @return the population registry
     */
    private PopulationRegistry generatePopulationRegistry() {
        PopulationRegistry pr = new PopulationRegistry();
        ListOf<Species> speciesList = sbmlModel.getListOfSpecies();
        for (Species s : speciesList) pr.register(s.getId(), s.getInitialAmount());
        return pr;
    }


    /**
     * Method for the generation of the evaluation environment
     * from a smbl model
     *
     * @return the population registry
     */
    private EvaluationEnvironment generateEvaluationEnvironment() {
        EvaluationEnvironment ee = new EvaluationEnvironment();
        ListOf<org.sbml.jsbml.Parameter> parameterList = sbmlModel.getListOfParameters();
        for (Parameter p : parameterList) ee.register(p.getName(),p.getValue());
        return ee;
    }

}
