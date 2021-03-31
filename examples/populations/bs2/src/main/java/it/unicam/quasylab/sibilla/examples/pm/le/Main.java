/**
 * 
 */
package quasylab.sibilla.examples.pm.le;

import quasylab.sibilla.core.simulator.SimulationEnvironment;
import quasylab.sibilla.core.simulator.pm.PopulationFactory;
import quasylab.sibilla.core.simulator.pm.PopulationModel;
import quasylab.sibilla.core.simulator.pm.PopulationRule;
import quasylab.sibilla.core.simulator.pm.ReactionRule;
import quasylab.sibilla.core.simulator.pm.ReactionRule.Specie;
import quasylab.sibilla.core.simulator.sampling.SamplingCollection;
import quasylab.sibilla.core.simulator.sampling.StatisticSampling;

import java.io.FileNotFoundException;
import java.io.PrintStream;

;

/**
 * @author loreti
 *
 */
public class Main {

	public final static int C = 0;
	public final static int S0 = 1;
	public final static int S1 = 2;
	public final static int F = 3;
	public final static int L = 4;
	
	public final static int INIT_C = 100;	
	public final static int INIT_S0 = 0;
	public final static int INIT_S1 = 0;
	public final static int INIT_F = 0;
	public final static int INIT_L = 0;
	public final static double N = INIT_C+INIT_F+INIT_L;
	
	public final static double SELECT_RATE = 1.0;
	public final static double COM_RATE = 1.0;
	public final static double WAITING_RATE = 20.0;
	public final static double MEET_PROB = 0.1;
	
	public final static int SAMPLINGS = 100;
	public final static double DEADLINE = 600;
	private static final int REPLICA = 1000;
	
	
	public static void main(String[] argv) throws FileNotFoundException {

		PopulationRule rule_C_S0 = new ReactionRule(
				"C->S0", 
				new Specie[] { new Specie(C) } , 
				new Specie[] { new Specie(S0) },  
				s -> s.getOccupancy(C)*0.5*SELECT_RATE); 

		PopulationRule rule_C_S1 = new ReactionRule( 
				"C->S1", 
				new Specie[] { new Specie(C) } , 
				new Specie[] { new Specie(S1) },  
				s -> s.getOccupancy(C)*0.5*SELECT_RATE); 

		PopulationRule rule_S0_S1 = new ReactionRule( 
				"S0*S1->F*C", 
				new Specie[] { new Specie(S0) , new Specie(S1) } , 
				new Specie[] { new Specie(F) , new Specie(C) } , 
				s -> s.getOccupancy(C)*0.5*SELECT_RATE); 

		PopulationRule rule_S0_S0 = new ReactionRule( 
				"S0*S1->F*C", 
				new Specie[] { new Specie(S0,2)} , 
				new Specie[] { new Specie(C,2)} , 
				s -> s.getOccupancy(C)*0.5*SELECT_RATE); 


		PopulationRule rule_S1_S1 = new ReactionRule( 
				"S0*S1->F*C", 
				new Specie[] { new Specie(S1,2) } , 
				new Specie[] { new Specie(C,2) } , 
				s -> s.getOccupancy(C)*0.5*SELECT_RATE); 

		PopulationRule rule_S0_F = new ReactionRule( 
				"S0 ->F", 
				new Specie[] { new Specie(S0) } , 
				new Specie[] { new Specie(F) } , 
				s -> s.getOccupancy(S0)*WAITING_RATE); 

		PopulationRule rule_S1_F = new ReactionRule( 
				"S0 ->F", 
				new Specie[] { new Specie(S1) } , 
				new Specie[] { new Specie(F) } , 
				s -> s.getOccupancy(S1)*WAITING_RATE); 

		PopulationRule rule_F_C = new ReactionRule( 
				"F*F -> C*C", 
				new Specie[] { new Specie(F) } , 
				new Specie[] { new Specie(C) } , 
				s -> s.getOccupancy(F)*COM_RATE); 
		
		
		PopulationFactory f = new PopulationFactory( 
				initialState(),
				rule_C_S0
				,rule_C_S1
				,rule_S0_S1
				,rule_S0_S1
				,rule_S1_S1
				,rule_F_C
				,rule_S0_F
				,rule_S1_F
		); 
		
		StatisticSampling<PopulationModel> cSamp = StatisticSampling.measure("#C", SAMPLINGS, DEADLINE, 
				s -> s.getCurrentState().getOccupancy(C,S0,S1)) ;
		StatisticSampling<PopulationModel> fSamp = StatisticSampling.measure("#F", SAMPLINGS, DEADLINE, s -> s.getCurrentState().getOccupancy(F)) ;
		StatisticSampling<PopulationModel> lSamp = StatisticSampling.measure("#L", SAMPLINGS, DEADLINE, s -> s.getCurrentState().getOccupancy(L)) ;
		
		SimulationEnvironment<PopulationModel> sim = new SimulationEnvironment<PopulationModel>( f );

		sim.setSampling(new SamplingCollection<>(cSamp,fSamp,lSamp));
		
		sim.simulate(REPLICA,DEADLINE);
		cSamp.printTimeSeries(new PrintStream("data/seir_"+REPLICA+"_"+N+"_C_.data"),';');
		fSamp.printTimeSeries(new PrintStream("data/seir_"+REPLICA+"_"+N+"_F_.data"),';');
		lSamp.printTimeSeries(new PrintStream("data/seir_"+REPLICA+"_"+N+"_L_.data"),';');


	}
	

	public static int[] initialState() {
		return new int[] { INIT_C, INIT_S0, INIT_S1, INIT_F , INIT_L };
	}
}
