package it.unicam.quasylab.sibilla.langs.pm.sbml;

import org.sbml.jsbml.*;

import java.util.ArrayList;
import java.util.Hashtable;

public class ModelSBML extends Model {

    private Hashtable<String,CompartmentSBML> compartmentTable = new Hashtable<>();

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


        // TO DO


        /*
        if(this.getChildCount() > 0){
            for (ASTNode childNode: node.getChildren()) {
                replaceCompartmentNodes(childNode);
            }
        }else{

        }
         */
    }

    /**
     * A class that extends the Compartment class
     * adding a list of species that belong to
     * a specific species.
     * this was done to simplify the management of
     * compartments in the modeling
     */
    class CompartmentSBML extends Compartment {

        private ArrayList<Species> species = new ArrayList<>();

        public void addSpecies(Species s){
            species.add(s);
        }

        public ArrayList<Species> getSpeciesList(){
            return species;
        }
    }
}
