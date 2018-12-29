package bellman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import Jama.Matrix;

public class GridWorldEx{
	
	private boolean[][] grid;
	private double[][] reward;
	private int size_x;
	private int size_y;
	private int nbStates;
	private Random rdmnum;
	private double gamma = 0.5;
	private long seed = 124;
	private int MAXREWARD = 20;
	private HashMap<Integer,HashMap<String,Double>> action;
	private HashMap<String,HashMap<Integer,ArrayList<double[]>>> pi;
	private ArrayList<String> dir;
											//#####################################################################
	private boolean showOtherStuff = false; //##### AFFICHE OU NON AUTRE CHOSE QUE LES V^PI POUR LA LISIBILITE ####
											//#####################################################################
	
	private HashMap<String,double[]> stateAccess;

	public GridWorldEx(int num_g) {
		this.rdmnum = new Random(this.seed);
		this.dir = new ArrayList<String>();
		this.dir.add("left");
		this.dir.add("up");
		this.dir.add("right");
		this.dir.add("down");
		this.dir.add("stay");
		this.dir.add("jumpF");
		
		initStateAccess();
		CreateGrid(num_g);
		InitRdmPol();
		InitTransitionMat();
		
		WallCst();
	}
	
	public void initStateAccess() {
		double[] singleAction = new double[1];
		singleAction[0] = 1.;
		double[] doubleAction = new double[2];
		doubleAction[0] = 0.8;
		doubleAction[1] = 0.2;
		
		stateAccess = new HashMap<String,double[]>();
		
		stateAccess.put("left", singleAction);
		stateAccess.put("right", singleAction);
		stateAccess.put("up", singleAction);
		stateAccess.put("down", singleAction);
		stateAccess.put("stay", singleAction);
		stateAccess.put("jumpF", doubleAction);
	}
	
	public int[][] getDirNeighbor(String act){
		int[][] d = new int[stateAccess.get(act).length][2];
		
		if(act.equals("left")) d[0][0]=-1;
		if(act.equals("right")) d[0][0]=1;
		if(act.equals("up")) d[0][1]=-1;
		if(act.equals("down")) d[0][1]=1;
		if(act.equals("jumpF")) {
			d[0][0] = 2;
			d[0][1] = 0;
			d[1][0] = 0;
			d[1][1] = 2;
		}
		
		return d;
	}
	
	public HashMap<Integer,ArrayList<double[]>> computeTrans(String act) {
		HashMap<Integer,ArrayList<double[]>> trans = new HashMap<Integer,ArrayList<double[]>>();
		for(int i = 0; i < size_x; i++){
			for(int j = 0; j < size_y; j++){
				
				ArrayList<double[]> reachableStates = new ArrayList<double[]>();
				
				
				int[][] possibleShifts = getDirNeighbor(act);

				for(int k = 0; k<possibleShifts.length; k++) {
					
					double[] stateNProba = new double[2];
					int[] stateShift = possibleShifts[k];
					stateNProba[0] = GridToState((i + stateShift[0] + size_x)%size_x, (j + stateShift[1] + size_y)%size_y);
					stateNProba[1] = stateAccess.get(act)[k]; 
					reachableStates.add(stateNProba);
				}
				
				trans.put(GridToState(i,j), reachableStates);
			}
		}
		return trans;
	}
	
	public void WallCst() {
		for(int i=0; i<size_x; i++) {
			for(int j=0; j<size_y; j++) {				
				if(!grid[i][j]) {
					HashMap<String,Double> a = new HashMap<String,Double>();
					a.put("left", 0.0);
					a.put("up", 0.0);
					a.put("right", 0.0);
					a.put("down", 0.0);
					a.put("stay", 1.0);
					a.put("jumpF", 0.0);
					action.put(GridToState(i,j),a);
				}
			}
		}
	}
	/**
	 * Transforme des coordonnees (i,j) en numero d'etat 
	 */
	private int GridToState(int i, int j) {
		return i + size_x * j;
	}
	
	/**
	 * Transforme un numero d'etat en coordonnees (i,j) dans un tableau [a,b]
	 */
	private int[] StateToGrid(int s) {
		int[] coord = new int[2];
		coord[1] = (int) s/size_x;
		coord[0] = s - coord[1]*size_x;
		return coord;
	}

	/**
	 * Initialise notre politique de maniere equiprobable
	 */
	private void InitRdmPol() {
		action = new HashMap<Integer,HashMap<String,Double>>();
		for(int i = 0; i < size_x; i++){
			for(int j = 0; j < size_y; j++){
				HashMap<String,Double> probaActions = new HashMap<String,Double>();
				for(String act : dir){
					probaActions.put(act, 1./dir.size());
				}
				action.put(GridToState(i,j), probaActions);
			}
		}
	}
		
	/**
	 * Insere dans pi les tables de transitions de chaque action calculees avec computeTrans
	 */
	private void InitTransitionMat() {
		pi = new HashMap<String,HashMap<Integer,ArrayList<double[]>>>();
		for(String act : this.dir) {
			pi.put(act,computeTrans(act));
		}
	}
	
	/**
	 * Calcule le vecteur R^pi, dont la composante i contient la recompense moyenne
	 * en faisant une seule action depuis l'etat numero i
	 */
	private double[] computeVecR() {
		double[] R = new double[nbStates];
		for(int s=0; s<nbStates; s++) {
			double averageRew= 0;
			HashMap<String,Double> a = action.get(s);
			
			for(String act : this.dir) {
				Double probaAction = a.get(act); 

				for(double[] stateNProba : pi.get(act).get(s)) {
					int[] sPrime = new int[2];
					sPrime = StateToGrid((int)stateNProba[0]);
					averageRew += probaAction*stateNProba[1] * reward[sPrime[0]][sPrime[1]];
				}
			}
			R[s] = averageRew;
		}
		return R;
	}
	
	/**
	 * Calcule la matrice P^pi, dont la composante (i,j) contient 
	 * la probabilite d'atteindre l'etat j depuis l'etat i
	 */
	private double[][] computeMatP() {
		double[][] P = new double[nbStates][nbStates];
		
		for(int s = 0; s < nbStates; s++) {			//initialisation
			for(int sP = 0; sP < nbStates; sP++)
				P[s][sP] = 0;
		}
		
		for(int s=0; s<nbStates; s++) {
			for(String act : this.dir) {
				HashMap<String,Double> a = action.get(s);
				Double probaAction = a.get(act);
				for(double[] stateNProba : pi.get(act).get(s)) {
					int sPrime = (int)stateNProba[0];
					P[s][sPrime] += probaAction * stateNProba[1];
				}	
			}
		}
		return P;
	}
	
	/**
	 * Construit le tableau P en tant qu'objet matriciel
	 * @return
	 */
	private Matrix BuildMatA() {
		double[][] f_A = new double[nbStates][nbStates];
		double[][] P = computeMatP();
		for(int s=0; s<nbStates; s++) {
			f_A[s][s] = 1;
			for(int sp=0; sp<nbStates; sp++) {
				f_A[s][sp] -= this.gamma*P[s][sp];
			}
		}
		
		return new Matrix(f_A);
	}

	/**
	 * Construit le tableau R en tant qu'objet matriciel
	 * @return
	 */
	private Matrix BuildMatb() {
		double[] vec_b = computeVecR();
		double[][] b = new double[vec_b.length][1];
		for(int i=0; i<vec_b.length; i++) {
			b[i][0] = vec_b[i];
		}
		return new Matrix(b);
	}
	
	/**
	 * On fait l'operation (I - gamma*P) * R
	 * on obtient V
	 */
	private double[][] SolvingP() {
		Matrix x = BuildMatA().solve(BuildMatb());
		return x.getArray();
	}
	
	/**
	 * On ameliore la politique de maniere deterministe :
	 * On cherche l'action a pour chaque etat tel que Q(s,a) soit le plus grand
	 * et on choisit systematiquement cette action dans notre nouvelle politique
	 */
	private void ImprovePolicy(double[][] V) {
		action = new HashMap<Integer,HashMap<String,Double>>();
		
		for(int s = 0; s < nbStates; s++) {
			HashMap<String,Double> probaActions = new HashMap<String,Double>();
			String bestAction = "stay";
			double bestQ = 0;
			
			for(String act : this.dir) {
				double Q = 0;
				for(double[] stateNProba : pi.get(act).get(s)) {
					int sPrime = (int) stateNProba[0];
					int[] sPrimeGrid = StateToGrid(sPrime);
					Q += stateNProba[1] * (this.reward[sPrimeGrid[0]][sPrimeGrid[1]]+ gamma*V[sPrime][0]); 
				}
				
				if(Q > bestQ) {
					bestAction = act;
					bestQ = Q;
				}
			}

			for(String act : this.dir) {
				if(act == bestAction)
					probaActions.put(act, 1.0);
				else
					probaActions.put(act,0.0);
			}
			action.put(s, probaActions);
		}
		WallCst();
	}
	
	/**
	 * Calcule de maniere iterative V^pi a partir de la politique et
	 * des differentes recompenses sur la grille
	 * @param teta un nombre petit arbitraire
	 * @return V^pi
	 */
	private double[][] IterativePolicy(double teta) {
		double delta = 1 + teta;
		double[][] V = new double[nbStates][1];
		
		for(int i = 0; i < nbStates; i++)
			V[i][0] = 0;
		
		while(delta > teta) {
			//System.out.println(delta);
			delta = 0;
			for(int s = 0; s < nbStates; s++) {
				
				double oldV = V[s][0];
				double newV = 0;
				
				for(String act : this.dir) {
					double probaAction = action.get(s).get(act);
					
					for(double[] stateNProba: pi.get(act).get(s)) {
						int sPrime = (int) stateNProba[0];
						int[] sPrimeGrid = StateToGrid(sPrime);
						newV += probaAction * stateNProba[1] * (reward[sPrimeGrid[0]][sPrimeGrid[1]] + gamma*V[sPrime][0]);	
					}
				}
				
				V[s][0] = newV;
				delta = Math.max(delta, Math.abs(oldV - V[s][0]));
			}
		}
		return V;
	}
	
	/**
	 * Initialise une grille muree selon g
	 */
	private void CreateGrid(int g) {
		switch(g) {
			case 0:
				this.size_x = 8;
				this.size_y = 5;
				this.grid = new boolean[size_x][size_y];
				this.reward = new double[size_x][size_y];
				this.nbStates = size_x*size_y;
				for(int i=0; i<size_x; i++) {
					for(int j=0; j<size_y; j++)
					{
						grid[i][j] = true;
						reward[i][j] = -1;
					}
				}
				// put some walls
				reward[2][2] = -1000;
				reward[3][2] = -1000;
				reward[4][2] = -1000;
				reward[5][2] = -1000;
				grid[2][2] = false;
				grid[3][2] = false;
				grid[4][2] = false;
				grid[5][2] = false;
	 
				// put a strong reward somewhere
				reward[0][0] = 20;
				break;
			case 1:
				this.size_x = 6;
				this.size_y = 6;
				this.grid = new boolean[size_x][size_y];
				this.reward = new double[size_x][size_y];
				this.nbStates = size_x*size_y;
				for(int i=0; i<size_x; i++) {
					for(int j=0; j<size_y; j++)
					{
						grid[i][j] = true;
						reward[i][j] = -1;
					}
				}
				reward[0][1] = 100;
				reward[0][2] = -1000;
				reward[0][3] = -1000;
				reward[0][4] = -1000;
				reward[2][0] = -1000;
				reward[2][1] = -1000;
				reward[2][3] = -1000;
				reward[2][4] = -1000;
				reward[3][4] = -1000;
				reward[3][5] = -1000;
				reward[4][1] = -1000;
				reward[4][2] = -1000;
				reward[4][3] = -1000;
				reward[4][5] = -1000;
				reward[5][5] = -1000;
	 
				grid[0][2] = false;
				grid[0][3] = false;
				grid[0][4] = false;
				grid[2][0] = false;
				grid[2][1] = false;
				grid[2][3] = false;
				grid[2][4] = false;
				grid[3][4] = false;
				grid[3][5] = false;
				grid[4][1] = false;
				grid[4][2] = false;
				grid[4][3] = false;
				grid[4][5] = false;
				grid[5][5] = false;
				break;				
		 	default:
				System.out.println("Erreur choix grille!");
				System.exit(-1);
				break;
		}
	}
	
	/**
	 * Affiche la grille : 0 pour un mur et 1 pour une case accessible
	 */
	private void showGrid() {
		for(int j=0; j<size_y; j++) {
			for(int i=0; i<size_x; i++)
			System.out.print((this.grid[i][j]?1:0));
		System.out.println();
		}
	}
	
	/**
	 * Affiche la grille de recompenses
	 */
	private void showRewGrid() {
		for(int j=0; j<size_y; j++) {
			for(int i=0; i<size_x; i++)
			System.out.print((int)this.reward[i][j]+"\t");
		System.out.println();
		}
	}
	
	/**
	 * Affiche le vecteur R en parametre
	 * @param R
	 */
	private void showR(double[] R) {
		for(int s = 0; s < nbStates; s++) {
			if(s%size_x==0) System.out.println();
			System.out.print(Math.round(R[s]) + "\t");
		}
		System.out.println();
	}
	
	/**
	 * Affiche le vecteur V en parametre
	 * @param V
	 */
	private void showV(double[][] V) {
		for(int s = 0; s < nbStates; s++) {
			if(s%size_x==0) System.out.println();
			System.out.print(Math.round(V[s][0]) + "\t");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		GridWorldEx gd = new GridWorldEx(0);
		HashMap<Integer,HashMap<String,Double>> action0 = gd.action;
		System.out.println("Grille : \n");
		gd.showGrid();
		
		System.out.println();
		
		System.out.println("Recompenses : \n");

		gd.showRewGrid();

		System.out.println();
		
		System.out.println("#######################################"
				+ "\n###    CALCUL DE V PAR ITERATION    ###"
				+ "\n#######################################\n");
		
		{	
			double[][] V = new double[gd.nbStates][1];
			
			if(gd.showOtherStuff) {
				System.out.println("V_pi(s)_0 : ");
				gd.showV(V);
			}
			
			for(int i = 0; i < 10; i++) {
				gd.ImprovePolicy(V);
				V = gd.IterativePolicy(0.1);
			}
			
			System.out.println();
			
			System.out.println("V_pi(s)_t : ");
			gd.showV(V);
		}
		
		System.out.println();
		
		System.out.println("#######################################"
						+ "\n###    CALCUL DE V PAR INVERSION    ###"
						+ "\n#######################################\n");
		
		{
		  	double[][] V = new double[gd.nbStates][1];
			double[] R = new double[gd.nbStates];
			
			gd.action = action0;
			
			if(gd.showOtherStuff) {
				V = gd.SolvingP();
				System.out.println("V_pi(s)_0 : ");
				gd.showV(V);
				
				System.out.println();
				
				R = gd.computeVecR();
				System.out.println("R(s)_0 : ");
				gd.showR(R);
			}
			
			for(int i = 0; i < 10; i++) {
				gd.ImprovePolicy(V);
				V = gd.SolvingP();
			}
			
			System.out.println();
			
			System.out.println("V_pi(s)_t : ");
			gd.showV(V);
			
			System.out.println();
			
			if(gd.showOtherStuff) {
				R = gd.computeVecR();
				System.out.println("R_pi(s)_t : ");
				gd.showR(R);
			}
		}
		
	}
	
}
