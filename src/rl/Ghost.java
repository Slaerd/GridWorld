package rl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Ghost extends Agent{
	static int NumGhosts = 0;
	
	private int[] dx_c, dy_c;
	private Image ghost;
	private int Id;
	
	private double pr_getPc=0.5; // Agressivité du fantome : 1=suit Pacman, 0=mvt aléatoire

	
	Ghost(Board BD) {
		super(BD);
		loadimages();
        dx_c = new int[4];
        dy_c = new int[4];
        Id = NumGhosts;
        NumGhosts++;
        
        InitNeighCells();
	}
	
	@Override
	void move() {
        int pos;
        int count;

        // test: I'm on a cell ?
        if (x % b.blocksize == 0 && y % b.blocksize == 0) {
	        pos = posx + b.nrofblocks * posy;
	        count = 0;
	
	        // against a wall
	        if ((b.screendata[pos] & 1) == 0 && dx != 1) {
	            dx_c[count] = -1;
	            dy_c[count] = 0;
	            count++;
	        }
	
	        if ((b.screendata[pos] & 2) == 0 && dy != 1) {
	            dx_c[count] = 0;
	            dy_c[count] = -1;
	            count++;
	        }
	
	        if ((b.screendata[pos] & 4) == 0 && dx != -1) {
	            dx_c[count] = 1;
	            dy_c[count] = 0;
	            count++;
	        }
	
	        if ((b.screendata[pos] & 8) == 0 && dy != -1) {
	            dx_c[count] = 0;
	            dy_c[count] = 1;
	            count++;
	        }
	        if (count == 0) {
	
	            if ((b.screendata[pos] & 15) == 15) {
	                dx = 0;
	                dy = 0;
	            } else {
	                dx = -dx;
	                dy = -dy;
	            }
	
	        } else {
	
	            count = (int) (Math.random() * count);
	
		        if (count > 3) {
		                count = 3;
		            }
		
		        // choose direction:
		        if(pr_getPc>Math.random()) { // get the pacman
		        	
		        	ArrayList<Tuple<Integer,Integer>> list_cells = getListCells();
		        	int choice_cell = (int) Math.floor(Math.random()*list_cells.size());
		        	dx = list_cells.get(choice_cell).x;
		        	dy = list_cells.get(choice_cell).y;
		        }  else { // some random direction
		        	int dir = (Math.random()>0.5?1:0);
		            dx = dx_c[count]*dir;
		            dy = dy_c[count]*(1-dir);	     
		        }
	        }
        }
        setx(x+(dx * speed));
        sety(y+(dy * speed));
        /*
         * Killing the pacman
        if (pacmanx > (x - 12) && pacmanx < (x + 12)
                && pacmany > (y - 12) && pacmany < (y + 12)
                && ingame) {

        	dying = true;
        }*/
	}
	
	private ArrayList<Tuple<Integer,Integer>> getListCells() {
		ArrayList<Tuple<Integer,Integer>> list_cells = new ArrayList<Tuple<Integer,Integer>>();
		
		double maxdist=100000;
    	for(Tuple<Integer,Integer> c : neighcells) {
    		double dist = Math.pow(x+c.x_block - b.pc.x,2)
    		            + Math.pow(y+c.y_block - b.pc.y,2);
    		
    		if(dist<maxdist) {
    			list_cells.clear();
    			maxdist = dist;
    			list_cells.add(c);		    
    		} else if (dist==maxdist)
    			list_cells.add(c);
    	}
    	
    	return list_cells;
	}
		

	@Override
	void draw(Graphics2D g2d) {
        g2d.drawImage(ghost, x+1, y+1, b);
		
	}

	@Override
	void doAnim() {
		// TODO Auto-generated method stub
		
	}
	
	void loadimages() {
		ghost = new ImageIcon("pacpix/Ghost1.png").getImage();
	}

}
