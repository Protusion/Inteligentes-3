package problems.tsp.maze;

import optimization.Configuration;
import problems.tsp.TSP;
import utils.Position;
import visualization.*;


/** 
 * Extends the TSP to represent it in a maze where movements are either horizontal or vertical, 
 * and uses manhattan as distance. 
 */
public class MazeTSP extends TSP implements ProblemVisualizable{

	/** Constructors */
	public MazeTSP(){ generateInstance(20, 10, 0); }
	public MazeTSP(int rangeXY, int numCities){ generateInstance(rangeXY, numCities, 0); }
	public MazeTSP(int rangeXY, int numCities, int seed){ generateInstance(rangeXY, numCities, seed); }

	
	/** Returns a view of the problem. */
	@Override
	public ProblemView getView() {
		MazeTSPView mazeView = new MazeTSPView(this, 600);
		return mazeView;
	}
	
	/** 
	 * Calculates the score of a configuration as the sum of the path. 
	 */
	@Override
	public double score(Configuration configuration) {
		
		/**
		 * COMPLETAR
		 */
		
		return 0;
	}
	
	/** Calculates the distance between two points. */
	private double dist(Position from, Position to){
		return Math.abs(from.x-to.x) + Math.abs(from.y-to.y);
	}
}
