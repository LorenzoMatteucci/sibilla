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

    ArrayList<String> listOfParameterString = new ArrayList<>();
    ArrayList<String> listOfSpeciesString = new ArrayList<>();

    private final Hashtable<String,CompartmentSBML> compartmentTable = new Hashtable<>();

    /**
     * In the constructor all the steps necessary to adapt
     * the model are performed
     */
    public ModelSBML() {
        super();
        populateCompartmentTable();
        populateLists();
        applyCorrectionReactionRules();
    }

    private void populateLists(){
        for (Parameter p: this.getListOfParameters()) {
            listOfParameterString.add(p.getName());
        }
        for (Species s: this.getListOfSpecies()) {
            listOfSpeciesString.add(s.getName());
        }
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
            ASTNode reactionTree = r.getKineticLaw().getMath();
            replaceCompartmentNodes(reactionTree);
            convertTreeToBinary(reactionTree);
            tagNodes(reactionTree);
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
     * Convert a m-ary tree into a binary tree
     * @param parentNode the m-ary tree
     */
    private static void convertTreeToBinary(ASTNode parentNode){
        if(parentNode.getChildCount()>0){
            for (ASTNode child :parentNode.getChildren()) {
                convertTreeToBinary(child);
            }
        }

        if (parentNode.getChildCount()>2){

            ASTNode secondLastChild = parentNode.getChild(parentNode.getChildCount()-2);
            ASTNode lastChild = parentNode.getChild(parentNode.getChildCount()-1);

            ASTNode newNode = new ASTNode();
            newNode.setType(parentNode.getType());

            newNode.addChild(secondLastChild);
            newNode.addChild(lastChild);

            parentNode.removeChild(parentNode.getChildCount()-1);
            parentNode.removeChild(parentNode.getChildCount()-2);

            parentNode.addChild(newNode);

            convertTreeToBinary(parentNode);
        }

    }

    private void tagNodes(ASTNode node){
        if(node.getType().equals(ASTNode.Type.NAME)){
            if(listOfSpeciesString.contains(node.getName())){
                node.setId("Species");
            }
            if(listOfParameterString.contains(node.getName())){
                node.setId("Parameter");
            }
        }
        if (node.getChildCount()>0){
            for (ASTNode child: node.getChildren()) {
                tagNodes(child);
            }
        }
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
