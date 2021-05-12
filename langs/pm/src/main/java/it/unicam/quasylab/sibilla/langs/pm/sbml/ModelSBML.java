package it.unicam.quasylab.sibilla.langs.pm.sbml;

import org.sbml.jsbml.*;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * An extension of the the Model class from jsbml library
 * This class is created in order to adapt the jsbml model
 * for the parsing into a Sibilla model
 */
public class ModelSBML extends Model {

    private final Hashtable<String,CompartmentSBML> compartmentTable = new Hashtable<>();

    /**
     * In the constructor all the steps necessary to adapt
     * the model are performed
     */
    public ModelSBML() {
        super();
        populateCompartmentTable();
        applyCorrectionReactionRules();
    }

    /**
     * A method to populate a table that map compartments
     * with the species that belong to them.
     * This will be useful to manage compartments during
     * the process of Population model generation in Sibilla.
     */
    private void populateCompartmentTable(){
        for (Species s: this.getListOfSpecies()) {
            String compartmentID = s.getCompartmentInstance().getId();
            if(compartmentTable.containsKey(compartmentID)){
                CompartmentSBML compartment = compartmentTable.get(compartmentID);
                compartment.addSpecies(s);
            }else{
                CompartmentSBML compartment = new CompartmentSBML();
                compartment.addSpecies(s);
                compartmentTable.put(compartmentID,compartment);
            }
        }
    }

    /**
     * this method check all the reaction rules to check
     * if this rule needs a correction
     */
    private void applyCorrectionReactionRules(){
        for (Reaction r:this.getListOfReactions()) {
            replaceCompartmentNodes(r.getKineticLaw().getMath());
        }
    }

    /**
     * This method will convert any node that contains a compartment
     * in to a ATSNode that sum up all the species belonging to the
     * specific compartment
     */
    private void replaceCompartmentNodes(ASTNode node){
        if(this.getChildCount() > 0){
            for (ASTNode childNode: node.getChildren()) {
                if(childNode.getType().equals(ASTNode.Type.NAME)){
                    String childNodeName = childNode.getName();
                    if(compartmentTable.containsKey(childNodeName)){
                        CompartmentSBML compartment = compartmentTable.get(childNodeName);
                        ArrayList<Species> species = compartment.getSpeciesList();
                        int childToReplaceIndex = getIndex(childNode);
                        ASTNode newChild = sumNode(species);
                        node.replaceChild(childToReplaceIndex,newChild);
                    }
                }else{
                    replaceCompartmentNodes(childNode);
                }
            }
        }
    }

    /**
     * Method that create a node for a species
     *
     * @param species a single species
     * @return ASTNode that represent a species
     */
    private ASTNode speciesNode(Species species){
        ASTNode newNode = new ASTNode();
        newNode.setType(ASTNode.Type.NAME);
        newNode.setName(species.getName());
        return newNode;
    }

    /**
     * A compartment represent a sum of species contained in it
     * This is a plus type node with all the species contained
     * in the compartment as children of the node
     *
     * @param species a list of species
     * @return ASTNode that represent a summation of species
     */
    private ASTNode sumNode(ArrayList<Species> species){
        ASTNode newNode = new ASTNode();
        newNode.setType(ASTNode.Type.PLUS);
        for (Species s: species) {
            ASTNode speciesNode = speciesNode(s);
            newNode.addChild(speciesNode);
        }
        return newNode;
    }

    /**
     * A class that extends the Compartment class
     * adding a list of species that belong to
     * a specific species.
     * this was done to simplify the management of
     * compartments in the modeling
     */
    static class CompartmentSBML extends Compartment {
        private final ArrayList<Species> species = new ArrayList<>();
        public void addSpecies(Species s){
            species.add(s);
        }
        public ArrayList<Species> getSpeciesList(){
            return species;
        }
    }
}
