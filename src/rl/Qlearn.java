package rl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Qlearn {
	public double epsilon = 0.0001; // parametre epsilon pour \epsilon-greedy
	public double alpha = 0.2; // taux d'apprentissage
	public double gamma = 0.9; // parametre gamma des eq. de Bellman/
	private Random rdm;
	private long seed = 400;
	
	// Suggestions
	public int actions[];
	public Hashtable< Tuple<Integer,Integer>, Double> q;
	
	// Constructeurs
	public Qlearn(int[] actions, Pacman pac) {
		rdm = new Random(seed);
		this.actions = actions;
		q = new Hashtable< Tuple<Integer,Integer>, Double>();

	}
	
	public Qlearn(int[] actions, double epsilon, double alpha, double gamma) {
		this.actions = actions;
		this.epsilon = epsilon;
		this.alpha = alpha;
		this.gamma = gamma;
		q = new Hashtable< Tuple<Integer,Integer>, Double>();
		
	}
	
	/**
	 * Met a jour Q(s,a) en suivant l'algorithme du Q-Learning
	 * @param s l'etat correspondant a la variable s de Q(s,a)
	 * @param action l'action correspondant a la variable a de Q(s,a)
	 * @param sPrime l'etat correspondant a la variable s' qui intervient dans la mise a jour de Q(s,a)
	 * @param reward la recompense pour avoir fait l'action a depuis s
	 */
	public void learnQ(int s, int action,int sPrime,double reward) {
		if(s != -1) {															// s = -1 correspond a l'etat initial,
			Tuple<Integer,Integer> SA = new Tuple<Integer,Integer>(s,action);	//  on ne fait rien
			Double newQSA = q.get(SA);
			
			if(newQSA == null) {	// On a avec le board de base 4^12 etats possibles,
				q.put(SA, 0.);		// que l'on ne va pas forcemment tous visiter.
				newQSA = 0.;		// On initialise donc Q pendant l'apprentissage plutot qu'a la construction d'un Qlearn
			}
			
			Double bestQ = -100.0;
			
			for(int actPrime : actions) {
				Tuple<Integer,Integer> SAPrime = new Tuple<Integer,Integer>(sPrime,actPrime);
				Double QSAPrime = q.get(SAPrime);
				
				if(QSAPrime == null) {
					q.put(SAPrime, 0.);
					QSAPrime = 0.;
				}
				
				bestQ = Math.max(QSAPrime, bestQ);
			}
			newQSA += alpha * (reward + gamma * bestQ - newQSA);
			q.replace(SA, newQSA);
		}
	}
	
	public void learnSARSA(int s, int action,int sPrime,double reward) {
		if(s != -1) {
			Tuple<Integer,Integer> SA = new Tuple<Integer,Integer>(s,action);
			Double newQSA = q.get(SA);
			
			if(newQSA == null) {
				q.put(SA, 0.);
				newQSA = 0.;
			}
			
			Double QSAPrime = 0.;
			Double chosenQ = -100.0;
	
			if(rdm.nextDouble() < epsilon) {
				int rdmAction = rdm.nextInt(8);
				Tuple<Integer,Integer> rdmSA = new Tuple<Integer,Integer>(sPrime,rdmAction);
				Double rdmQSA = q.get(new Tuple<Integer,Integer>(sPrime,rdmAction));
				
				if(rdmQSA == null) {
					q.put(rdmSA, 0.);
					rdmQSA = 0.;
				}
				
				chosenQ = rdmQSA;
			}else{
				for(int actPrime : actions) {
					Tuple<Integer,Integer> SAPrime = new Tuple<Integer,Integer>(sPrime,actPrime);
					QSAPrime = q.get(SAPrime);
					
					if(QSAPrime == null) {
						q.put(SAPrime, 0.);
						QSAPrime = 0.;
					}
					
					chosenQ = Math.max(QSAPrime, chosenQ);
				}
			}
			newQSA += alpha * (reward + gamma * chosenQ - newQSA);
			q.replace(SA, newQSA);
		}
	}
	
	public int chooseAction(int s) {
		if(rdm.nextDouble() < epsilon)
			return rdm.nextInt(8);
		else {
			int bestAction = 0;
			for(int act : actions) {
				Tuple<Integer,Integer> SA = new Tuple<Integer,Integer>(s,act);
				Tuple<Integer,Integer> bestSA = new Tuple<Integer,Integer>(s,bestAction);
				Double QSA = q.get(SA);
				Double bestQ = q.get(bestSA);
				
				if(QSA == null) {
					q.put(SA, 0.);
					QSA = 0.;
				}
				
				if(bestQ == null) {
					q.put(bestSA,0.);
					bestQ = 0.;
				}
				
				//System.out.println(bestQ);
				if(bestQ < QSA){
					bestAction = act;
				}
			}
			return bestAction;
		}	
	}
}