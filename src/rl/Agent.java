package rl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

abstract class Agent {
	
	public int x,y;
	public int dx, dy;
	public int posx, posy;
	protected int viewx, viewy;
	protected int viewdx, viewdy;
	public int speed;
	
	protected ArrayList< Tuple<Integer,Integer> > neighcells;
	
	GridWorld gw;
	Board b;
	 	 	
	Agent(Board BD) {		
		speed = 3;
		b = BD;
		InitNeighCells();
	}
	
	abstract void move();
	abstract void draw(Graphics2D g2d);
	
	abstract void doAnim();
	abstract void loadimages();
	
	// all the neighbors from one site
	public void InitNeighCells() {
		neighcells = new ArrayList< Tuple<Integer,Integer>>();
		for(int i=-1; i<2; i++) {
			for(int j=-1; j<2; j++) {
				if(i!=0 || j!=0) {
					neighcells.add(new Tuple<Integer,Integer>(i,j,b.blocksize,b.blocksize));
				}
			}
		}
	}
	
	public void setx(int x) {
		this.x = x;
		posx = x/b.blocksize;
	}
	public void sety(int y) {
		this.y = y;
		posy = y/b.blocksize;
	}
	
}
