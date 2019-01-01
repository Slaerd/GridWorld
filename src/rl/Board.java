package rl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallfont = new Font("Helvetica", Font.BOLD, 14);

    // graphical variables
    private Image ii;
    private final Color dotcolor = new Color(192, 192, 0);
    private Color mazecolor;

    public int nrofblocks;
    public final int blocksize = 24;
    private final int scrsize; // = nrofblocks * blocksize;
	private boolean do_repaint = true;
        
    private Image pacman_score = new ImageIcon("pacpix/PacMan3left.png").getImage();
    
    // World variables
    Pacman pc;
    Ghost[] ghosts; 
    private int world_age = 0;
    private final int maxghosts = 12;
    private int nrofghosts = 1;
    private boolean dying = false;
    private boolean inside_game = false;
    private int fq_showScore = 10000;
    public int x_food = -1, y_food = -1;    private boolean ingame = false;
    public int score = 0;    
    public int reqdx, reqdy;
    

    /*private final short leveldata[] = {
        19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
        25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
        1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
        1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
        1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
        9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };*/
    
   /* private final short leveldata[] = {
            3, 10, 10, 10, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6,
            5, 32, 32, 32, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4,
            5, 32, 32, 32, 1, 0, 0, 0, 0, 0, 0, 16, 0, 0, 4,
            5, 32, 32, 32, 1, 0, 0, 8, 0, 0, 0, 0, 0, 0, 4,
            1, 2, 2, 2, 0, 0, 4, 32, 1, 0, 0, 0, 0, 0, 4,
            1, 0, 0, 0, 0, 0, 4, 32, 1, 0, 0, 0, 0, 8, 4,
            9, 0, 0, 0, 8, 8, 12, 32, 9, 8, 8, 0, 4, 0, 5,
            33, 1, 0, 4, 32, 32, 32, 32, 32, 32, 32, 1, 4, 0, 5,
            33, 1, 0, 0, 2, 2, 6, 32, 3, 2, 2, 0, 4, 32, 5,
            33, 1, 0, 0, 0, 0, 4, 32, 1, 0, 0, 0, 4, 32, 5,
            33, 1, 0, 0, 0, 0, 4, 32, 1, 0, 0, 0, 4, 32, 5,
            33, 1, 0, 0, 0, 0, 16, 2, 16, 0, 0, 0, 4, 32, 5,
            33, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 32, 5,
            33, 9, 8, 8, 8, 8, 8, 8, 8, 8, 0, 0, 0, 2, 4,
            41, 40, 40, 40, 40, 40, 40, 40, 40, 40, 9, 8, 8, 8, 12
        };*/
    
    private final short leveldata[] = {
    		3, 2, 2, 2, 2, 6,
    		1, 0, 0, 0, 0, 4,
    		1, 0, 0, 0, 0, 4,
    		1, 0, 0, 0, 0, 4,
    		1, 0, 0, 0, 0, 4,
    		9, 8, 8, 8, 8, 12
    };
    
    public short levelstate[] = new short[leveldata.length];

    public short STATE_EMPTY = 0;
    public short STATE_WALL = 1;
    public short STATE_BADGUY = 2;
    public short STATE_GOODSTUFF = 4;
    
    public short[] screendata;
    private Timer timer;

    public Board() {
    	// size of the maze
    	nrofblocks = (int) Math.sqrt(levelstate.length);
        scrsize = nrofblocks * blocksize;
        
    	// Add agents
    	pc = new Pacman(this);
    	ghosts = new Ghost[maxghosts];
    	for(int i=0; i<maxghosts; i++)	
    		ghosts[i] = new Ghost(this);
    	
        initVariables();
        
        addKeyListener(new TAdapter()); // to control the keyboard

        setFocusable(true);

        setBackground(Color.black);
        setDoubleBuffered(true);
        
        System.out.println("WorldAge\tNbMorts\tNbFood\tNbBloque");
    }

    private void initVariables() {

        screendata = new short[nrofblocks * nrofblocks];
        mazecolor = new Color(5, 100, 5);
        d = new Dimension(400, 400);      
        
        timer = new Timer(40, this);
        timer.start();
    }

    /*@Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }*/
    
    private void initLevelState() {
    	for(int i=0; i<screendata.length; i++) {
    		levelstate[i] = 0;
    		if((screendata[i]&32)!=0)
    			levelstate[i] += STATE_WALL;
    		else if((screendata[i]&16)!=0)
    			levelstate[i] += STATE_GOODSTUFF;
    	}
    }
    
    public int getIndexFromCoord(int x, int y) {
    	int ind = x + y*nrofblocks;
    	if((x<0) || (x>=nrofblocks) ||(y<0) || (y>=nrofblocks)) 
    		return -1; // out of the world 
    	else return ind;
    }	


    private void playGame(Graphics2D g2d) {

        if (dying) {
            death();
        } else {
        	// move pacman
        	pc.move();
        	
        	if(g2d != null) pc.draw(g2d);        	
            dying = pc.restart;
            
            // move the ghosts
            for(int i=0; i<nrofghosts; i++)
            {
            	levelstate[getIndexFromCoord(ghosts[i].x/blocksize,ghosts[i].y/blocksize)] -= STATE_BADGUY;
            	ghosts[i].move();            	
            	levelstate[getIndexFromCoord(ghosts[i].x/blocksize,ghosts[i].y/blocksize)] += STATE_BADGUY;
            	if(g2d != null) ghosts[i].draw(g2d);
            }
        }
    }
    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, scrsize / 2 - 30, scrsize - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, scrsize / 2 - 30, scrsize - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (scrsize - metr.stringWidth(s)) / 2, scrsize / 2);
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(smallfont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, scrsize / 2 + 96, scrsize + 16);

        for (i = 0; i < 3; i++) {
            g.drawImage(pacman_score, i * 28 + 8, scrsize + 1, this);
        }
    }

    private void death() {

    	// rebooting the game
    	pc.restart = false;
    	initLevelState();
        continueLevel();
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < scrsize; y += blocksize) {
            for (x = 0; x < scrsize; x += blocksize) {

                g2d.setColor(mazecolor);
                g2d.setStroke(new BasicStroke(2));
                
                if ((screendata[i] & 1) != 0) {   // wall on the left
                    g2d.drawLine(x, y, x, y + blocksize - 1);
                }

                if ((screendata[i] & 2) != 0) {   // wall on the top
                    g2d.drawLine(x, y, x + blocksize - 1, y);
                }

                if ((screendata[i] & 4) != 0) {   // wall on the right
                    g2d.drawLine(x + blocksize - 1, y, x + blocksize - 1,
                            y + blocksize - 1);
                }

                if ((screendata[i] & 8) != 0) {   // wall on the bottom
                    g2d.drawLine(x, y + blocksize - 1, x + blocksize - 1,
                            y + blocksize - 1);
                }

                if ((screendata[i] & 16) != 0) {  // food !
                    g2d.setColor(dotcolor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {        
        initLevel();
        nrofghosts = 1;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < nrofblocks * nrofblocks; i++) {
            screendata[i] = leveldata[i];
        }

        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;

        for (i = 0; i < nrofghosts; i++) { 
        	ghosts[i].setx(2 * blocksize);
        	ghosts[i].sety(2 * blocksize);
            ghosts[i].dy = 0;
            ghosts[i].dx = dx;
            dx = -dx;
            levelstate[getIndexFromCoord(ghosts[i].posx,ghosts[i].posy)] += STATE_BADGUY;

            ghosts[i].speed = 3; //validspeeds[random];
        }

        pc.setx(4 * blocksize); // old value 7
        pc.sety(4 * blocksize); // old value 11
        pc.dx = 0;
        pc.dy = 0;
        reqdx = 0;
        reqdy = 0;
        pc.viewdx = -1;
        pc.viewdy = 0;
        
        random_cheese();    
        dying = false;
    }
    
    public void random_cheese() {
    	if((x_food!=-1)&&(y_food!=-1))
        {
        	levelstate[getIndexFromCoord(x_food,y_food)] -= STATE_GOODSTUFF;
     		screendata[getIndexFromCoord(x_food,y_food)] -= 16;
        }
    	
    	boolean put_cheese = false;
        while(!put_cheese) {
        	int x = (int) Math.floor(Math.random()*nrofblocks);
        	int y = (int) Math.floor(Math.random()*nrofblocks);
        	
        	if(((levelstate[getIndexFromCoord(x,y)] & STATE_WALL) ==0) && 
             	   (x!=pc.x) && (y!=pc.y)) {
         		levelstate[getIndexFromCoord(x,y)] += STATE_GOODSTUFF;
         		screendata[getIndexFromCoord(x,y)] += 16;
         		x_food = x;        		
         		y_food = y;
         		put_cheese = true;
         	}
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // call here all the drawing
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        
        initLevelState();
        
        for(int i=0; i<nrofghosts; i++)
        	levelstate[getIndexFromCoord(ghosts[i].x/blocksize,ghosts[i].y/blocksize)] = 2;
        
        pc.doAnim();

        if (ingame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
        world_age ++;
    }
    
    public void playWithoutDisplay() {
    	inside_game = true;
    	while(inside_game) {
	    	for(int i=0; i<nrofghosts; i++)
	        	levelstate[getIndexFromCoord(ghosts[i].x/blocksize,ghosts[i].y/blocksize)] = 2;      
	
	        if (ingame) {
	            playGame(null);
	        } else {
	            showIntroScreen(null);
	        }
	        world_age ++;
	        if((world_age%fq_showScore)==0) {
	        	inside_game=false;
	        }
    	}
    	System.out.println(world_age+"\t"+pc.eaten+"\t"+pc.good+"\t"+pc.stuck);
    	pc.reset_state();
    }

    class TAdapter extends KeyAdapter {
    	
        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if(key == KeyEvent.VK_I){ // Toggle visible/invisible update
            	do_repaint = !do_repaint;
            	inside_game = false;
            }

            if (ingame) {
                if (key == KeyEvent.VK_LEFT) {
                    reqdx = -1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    reqdx = 1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    reqdx = 0;
                    reqdy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    reqdx = 0;
                    reqdy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    ingame = false;
                } else if (key == KeyEvent.VK_SPACE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == 's' || key == 'S') {
                    ingame = true;
                    initGame();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT
                    || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
                reqdx = 0;
                reqdy = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	if(do_repaint) {
    		repaint();
    	} else {
    		if(!inside_game) 
    			playWithoutDisplay();
    	}
    }
}