import java.awt.*;
import java.awt.image.*;
import java.awt.MediaTracker;
import java.net.*;
import java.applet.*;
import java.lang.Integer;

public class Packman extends java.applet.Applet implements Runnable{
   Thread packman=null;
   Image bg,monstimg,monstmask,heroimg,quadimg,appleimg,butup,butdw,scorebrd,scoremsk,quadhero;
   Image gameover,pauseup,pausedw;
   Image virtimage;
   Graphics tmpG;
   Graphics g = null;
   boolean runit = false,quadon=false;
   Font font;            
   Color color;
   AudioClip eatm,tele,quadauon,quadauoff,death;
   Rectangle rect3 = new Rectangle(440,350,60,30);     
   Rectangle rect1 = new Rectangle(520,350,60,30);     
   int hero[] = new int[10];
   int monst[][]=new int[4][10]; //{1,1,2,3,4,5,5,6,7,8};
   int virtplgr[][] = new int[15][15];
   int occupied[][] = new int[15][15];   
   int done = 0,hit=0,points=0,lives = 3,steps = 0,eaten=0,speed=100,level=0;
   int walkarr[][] =  {{10,12,12,14,12,14,15,14,12,14,12,12, 6},
                       { 3,10,12,13, 6, 3, 1, 3,10,13,12, 6, 3},
                       { 3, 3,10, 4, 3,11,14, 7, 3, 8, 6, 3, 3},
                       { 3, 3, 3, 8,15, 7, 3,11,15, 4, 3, 3, 3},
                       { 3, 3, 9,12, 5, 1, 3, 1, 9,12, 5, 3, 3},
                       {11,13,14,12, 4,10,15, 6, 8,12,14,13, 7},
                       {15, 4,11,12,12,15,15,15,12,12, 7, 8,15},
                       {11,14,13,12, 4, 9,15, 5, 8,12,13,14, 7},
                       { 3, 3,10,12, 6, 2, 3, 2,10,12, 6, 3, 3},
                       { 3, 3, 3, 8,15, 7, 3,11,15, 4, 3, 3, 3},
                       { 3, 3, 9, 4, 3,11,13, 7, 3, 8, 5, 3, 3},
                       { 3, 9,12,14, 5, 3, 2, 3, 9,14,12, 5, 3},
                       { 9,12,12,13,12,13,15,13,12,13,12,12, 5}};
                                                            
   
   public void paint(Graphics g) {
      int i,j,c;
      if (lives == 0) {
         g.drawImage(scoremsk,445,285,this);
         g.drawImage(gameover,125,125,this);
         return;                    
      }
      tmpG.drawImage(bg,0,0,this);
      tmpG.drawImage(butup,440,350,this);
      tmpG.drawImage(pauseup,520,350,this);         
      for (i=0;i<13;i++) {
         for (j=0;j<13;j++)
            if (((c = virtplgr[i][j]) == 1) || (c == 2)) {
               if (c == 1)
                  tmpG.drawImage(appleimg,i*30+32,j*30+32,this);
               else
                  tmpG.drawImage(quadimg,i*30+34,j*30+34,this);                  
            }               
      }      
      if (quadon==true)
         tmpG.drawImage(quadhero,hero[0]+32,hero[1]+32,this); // Just the ordinary
      else
         tmpG.drawImage(heroimg,hero[0]+32,hero[1]+32,this); // Use quadhero
      if (!((hero[0] == hero[2]) && (hero[1] == hero[3]))) 
         tmpG.drawImage(monstmask,hero[2]+32,hero[3]+32,this);                                        
      for (i=0;i<4;i++) { 
         tmpG.drawImage(monstimg,monst[i][0]+32,monst[i][1]+32,this);      
         if (!((monst[i][0] == monst[i][2]) && (monst[i][1] ==monst[i][3])))
            tmpG.drawImage(monstmask,monst[i][2]+32,monst[i][3]+32,this);                     
      }
      tmpG.drawImage(scorebrd,440,50,this);
      tmpG.drawImage(scoremsk,540,165,this);
      tmpG.setFont(font);
      tmpG.setColor(color);
      tmpG.drawString(""+points,540,185);
      tmpG.drawString(""+level,540,207);
      g.drawImage(virtimage,0,0,this);
   }
   

   public void update(Graphics g) {
      done = 0;
      paint(g);
   }  

   public boolean keyDown(Event evt, int key)
   {
      if (((key == Event.DOWN) || (key == 109)) && ((walkarr[hero[0]/30][hero[1]/30] & 8) == 8)) {
         hero[4] = 8;
         hero[5] = 0;
         hero[6] = 30;
         return true;         
      }         
      else if (((key == Event.UP) ||(key == 105)) && ((walkarr[hero[0]/30][hero[1]/30] & 4) == 4)) {
         hero[4] = 4;
         hero[5] = 0;
         hero[6] = -30;
         return true;         
      }         
      else if (((key == Event.LEFT) || (key == 106)) && ((walkarr[hero[0]/30][hero[1]/30] & 1) == 1)) {
         hero[4] = 1;
         hero[5] = -30;
         hero[6] = 0;
         return true;         
      }         
      else if (((key == Event.RIGHT)|| (key == 107)) && ((walkarr[hero[0]/30][hero[1]/30] & 2) == 2)) {
         hero[4] = 2;
         hero[5] = 30;
         hero[6] = 0;
         return true;         
      }
      return true;         
   }

   public void update_monsters() 
   {
      int i,j,j1,dir=0,olddir;
      for (i=0;i<4;i++) {
         if ((walkarr[monst[i][0]/30][monst[i][1]/30] == 3) || (walkarr[monst[i][0]/30][monst[i][1]/30] == 12)) {      
            monst[i][2] = monst[i][0];
            monst[i][3] = monst[i][1];
            switch(monst[i][4]) {
               case 1 :{ monst[i][0] -= 30; break;}
               case 2 :{ monst[i][0] += 30; break;}                               
               case 4 :{ monst[i][1] -= 30; break;}
               case 8 :{ monst[i][1] += 30; break;}
            }
         }            
         else {         
            if (monst[i][4]==1)
               olddir = 2;
            else if (monst[i][4]==2)
               olddir = 1;
            else if (monst[i][4]==4)
               olddir = 8;
            else 
               olddir = 4;                           
            while (1==1) {
               dir = (int) Math.round(Math.random()*3);                
               if (dir == 3)
                  dir = 4;
               else if (dir == 2)
                  dir = 8;                                                                 
               else if (dir == 1)
                  dir = 2;
               else
                  dir = 1;                  
               if (((dir & walkarr[monst[i][0]/30][monst[i][1]/30]) == dir) && ((dir != olddir)|| (walkarr[monst[i][0]/30][monst[i][1]/30]== olddir)))
                   break;                                                                        
            }
            monst[i][4] = dir;
            monst[i][2] = monst[i][0];
            monst[i][3] = monst[i][1];
            switch(dir) {
               case 1 :{ monst[i][0] -= 30; break;}
               case 2 :{ monst[i][0] += 30; break;}                               
               case 4 :{ monst[i][1] -= 30; break;}
               case 8 :{ monst[i][1] += 30; break;}
            }
         }               
         if (monst[i][0] == -30)
            monst[i][0] = 360;
         if (monst[i][0] == 390)
            monst[i][0] = 0;
         if (monst[i][1] == -30)
            monst[i][1] = 360;
         if (monst[i][1] == 390)
            monst[i][1] = 0;
      }
   }
   
   public void init() 
   {
      int i,j;
      MediaTracker tracker = new MediaTracker(this);
      eatm = getAudioClip(getDocumentBase(),"eatm.au");
      tele = getAudioClip(getDocumentBase(),"tele.au");
      quadauon = getAudioClip(getDocumentBase(),"quadon.au");
      quadauoff = getAudioClip(getDocumentBase(),"quadoff.au");
      death     = getAudioClip(getDocumentBase(),"death.au");     
      bg        = getImage(getDocumentBase(),"playg1.gif");
      quadhero  = getImage(getDocumentBase(),"qhero.gif");
      monstimg  = getImage(getDocumentBase(),"monstimg.gif");
      monstmask = getImage(getDocumentBase(),"monstmsk.gif");
      heroimg   = getImage(getDocumentBase(),"heroimg.gif");
      quadimg   = getImage(getDocumentBase(),"quad1.gif");
      appleimg  = getImage(getDocumentBase(),"appleimg.gif");
      butup     = getImage(getDocumentBase(),"butup.gif");
      butdw     = getImage(getDocumentBase(),"butdw.gif");
      scorebrd  = getImage(getDocumentBase(),"scorebrd.gif");
      scoremsk  = getImage(getDocumentBase(),"scoremsk.gif");
      gameover  = getImage(getDocumentBase(),"gameover.gif");
      pauseup   = getImage(getDocumentBase(),"pauseup.gif");
      pausedw   = getImage(getDocumentBase(),"pausedw.gif");     
      font      = new Font("Courier",1,16);
      color     = new Color(102,153,255);
      tracker.addImage(bg,0);
      tracker.addImage(quadhero,2);
      tracker.addImage(monstimg,1);           
      tracker.addImage(monstmask,10);
      tracker.addImage(heroimg,3);   
      tracker.addImage(quadimg,4);
      tracker.addImage(appleimg,5);
      tracker.addImage(butup,6);
      tracker.addImage(butdw,7);
      tracker.addImage(scoremsk,8);
      tracker.addImage(scorebrd,9);
      tracker.addImage(pauseup,11);
      tracker.addImage(pausedw,12);        
      hero[0] = 30;
      hero[1] = 0;
      hero[2] = monst[2][2] = monst[0][2] = monst[3][2] = monst[1][2] = 420;         
      hero[3] = monst[2][3] = monst[0][3] = monst[1][3] = monst[3][3] = 380;
      monst[0][0] = 150;
      monst[0][1] = 150;      
      monst[1][0] = 210;
      monst[1][1] = 210;
      monst[2][0] = 210;
      monst[2][1] = 150;
      monst[3][0] = 150;
      monst[3][1] = 210;           
      virtimage = createImage(600,450);
      tmpG = virtimage.getGraphics();      
      while (!tracker.checkAll())
         try {
            tracker.waitForAll();
         }catch(InterruptedException e){showStatus("Interrupt received");};                  
      for (i=0;i<14;i++)
         for (j=0;j<14;j++)
            virtplgr[i][j] = 1;
      virtplgr[10][3] = 2;
      virtplgr[2][9] = 2;      
      repaint();
      showStatus("Press restart");
   }        
   public boolean mouseUp(Event evt,int x,int y) 
   {
      g = getGraphics();   
      g.drawImage(butup,440,350,this);  
      return true;
   }
   

   public boolean mouseDown(Event evt,int x,int y)
//   public void mousearound() 
   {
      int i,j;
      g = getGraphics();
      if ((rect1.inside(x,y)) && (lives != 0)) {   
         if  (runit) {
            g.drawImage(pausedw,520,350,this);         
            runit = false;
         }
         else {
            g.drawImage(pauseup,520,350,this);         
            runit = true;
         }            
      }                                 
      else if (rect3.inside(x,y)) {         
         showStatus("Here we go");
         g.drawImage(butdw,440,350,this);         
         for (i=0;i<14;i++)
            for (j=0;j<14;j++)
               virtplgr[i][j] = 1;
         virtplgr[10][3] = 2;
         virtplgr[2][9] = 2;      
         hero[0] = 30;
         hero[1] = 0;
         hero[2] = 420;
         hero[3] = 380;
         hero[4] = 0;
         hero[5] = 0;
         hero[6] = 0;
         monst[0][0] = 150;
         monst[0][1] = 150;      
         monst[1][0] = 210;
         monst[1][1] = 210;
         monst[2][0] = 210;
         monst[2][1] = 150;
         monst[3][0] = 150;
         monst[3][1] = 210;           
         monst[2][2] = monst[0][2] = monst[3][2] = monst[1][2] = 420;
         monst[2][3] = monst[0][3] = monst[1][3] = monst[3][3] = 380;
         monst[0][5] = 30;
         monst[0][6] = 0;
         monst[1][5] = -30;
         monst[1][6] = 0;
         monst[2][5] = -30;
         monst[2][6] = 0;
         monst[3][5] = 30;
         monst[3][6] = 0;
         monst[0][4] = 2;
         monst[1][4] = 1;
         monst[2][4] = 2;
         monst[3][4] = 1;   
         points = 0;
         lives  = 3;         
         runit  = true;
         quadon = false;
         steps  = 0;
         eaten  = 0;
         speed  = 150;
         level  = 0;
         repaint();               
      }                        
      else
         runit = false;
      return true;
   }
   
   
   public void drawit()
   {
      tmpG.setFont(font);
      int i,j,c;
      for (i=0;i<lives;i++) // Ritar ut antal liv kvar
         tmpG.drawImage(heroimg,460+i*40,290,this);
      for (i=0;i<13;i++)
         for (j=0;j<13;j++)
            occupied[i][j] =0;
      for (i=0;i<4;i++)
         occupied[monst[i][0]/30][monst[i][1]/30] = 1;
      for (i=0;i<13;i++) {
         for (j=0;j<13;j++) {
            if (((c = virtplgr[i][j]) == 1) || (c == 2)) {
               if ((c == 1) && (occupied[j][i] != 1))
                  tmpG.drawImage(appleimg,i*30+32,j*30+32,this);
               if ((c == 2) && (occupied[j][i] != 1))
                  tmpG.drawImage(quadimg,i*30+34,j*30+34,this);                  
            }               
            else
               tmpG.drawImage(monstmask,i*30+32,j*30+32,this);
         }          
      }      

      if (quadon==true)
         tmpG.drawImage(quadhero,hero[0]+32,hero[1]+32,this); // Use quadhero
      else 
         tmpG.drawImage(heroimg,hero[0]+32,hero[1]+32,this);  // Just the ordinary
      if (!((hero[0] == hero[2]) && (hero[1] == hero[3]))) 
         tmpG.drawImage(monstmask,hero[2]+32,hero[3]+32,this);                   
      for (i=0;i<4;i++) 
         tmpG.drawImage(monstimg,monst[i][0]+32,monst[i][1]+32,this);
      tmpG.drawImage(scoremsk,540,165,this);
      tmpG.setFont(font);
      tmpG.setColor(color);
      tmpG.drawString(""+points,540,185);
      tmpG.drawString(""+level,540,207);
      g.drawImage(virtimage,0,0,this);
   }
      

   public void start() 
   {
      if(packman == null) {
         packman = new Thread(this);
         packman.start();
      }
   }

   public void stop() {
      packman = null;
   }

   public void run() 
   {
      int i,j;
      boolean crash = false;
      while (packman != null) {
         if (runit) {
            try {packman.sleep(speed);} catch (InterruptedException e){}
            update_monsters();        
            if (quadon)
               steps += 1;
            if (steps == 30)
               quadauoff.play();            
            if (steps == 40) {
               quadon = false;
               steps = 0;
            }      
            for (i=0;i<4;i++)
               if ((hero[0] == monst[i][0]) && (hero[1] == monst[i][1]))
                  break;
            if (i != 4) {
               crash = true;
            }
            if (virtplgr[hero[0]/30][hero[1]/30] != 0) {
               virtplgr[hero[0]/30][hero[1]/30] = 0;
               points++;
               eaten++;
            }                           
            if (!crash) {
               if ((walkarr[hero[0]/30][hero[1]/30] & hero[4]) != hero[4]) {
                  hero[2] = hero[0];
                  hero[3] = hero[1];
               }
               else {
                  hero[2] = hero[0];
                  hero[3] = hero[1];        
                  hero[0] += hero[5];
                  hero[1] += hero[6];
               }                                       
               if (hero[0] == -30) {
                  tele.play();
                  hero[0] = 360;
               }
               if (hero[0] == 390) {
                  tele.play();
                  hero[0] = 0;
               }                  
               if (hero[1] == -30) {
                  tele.play();
                  hero[1] = 360;
               }
               if (hero[1] == 390) {
                  tele.play();
                  hero[1] = 0;
               }                          
               drawit();               
               for (i=0;i<4;i++)
                  if ((hero[0] == monst[i][0]) && (hero[1] == monst[i][1]))
                     break;                             
               if (i != 4)  
                  crash = true;
               else if (virtplgr[hero[0]/30][hero[1]/30] == 2) {
                  virtplgr[hero[0]/30][hero[1]/30] = 0;                     
                  quadauon.play();
                  quadon = true;
               }                  
            }      
            else  
               drawit();
            for (i=0;i<10000;i++)
                  ;
            if (crash) {
               if (!quadon) {
                  lives--;              
                  death.play();
               }                  
               if (lives == 0) {
                  showStatus("Press restart");                                       
                  runit = false;
                  repaint();
               }                  
               crash = false;
               // play ... putimage of explosion
               if (!quadon) {
                  try {packman.sleep(1000);} catch (InterruptedException e){}            
                  hero[0] = 30;
                  hero[1] = 0;
                  hero[2] = 0;
                  hero[3] = 0;
                  hero[4] = 2;
                  hero[5] = 30;
                  hero[6] = 0;
                  monst[0][0] = 150;
                  monst[0][1] = 150;      
                  monst[1][0] = 210;
                  monst[1][1] = 210;
                  monst[2][0] = 210;
                  monst[2][1] = 150;
                  monst[3][0] = 150;
                  monst[3][1] = 210;           
                  monst[0][2] = monst[0][3] = monst[1][2] = monst[1][3] = 0;
                  monst[2][2] = monst[2][3] = monst[3][2] = monst[3][3] = 0;
                  monst[0][5] = 30;
                  monst[0][6] = 0;
                  monst[1][5] = -30;
                  monst[1][6] = 0;
                  monst[2][5] = -30;
                  monst[2][6] = 0;
                  monst[3][5] = 30;
                  monst[3][6] = 0;
                  monst[0][4] = 2;
                  monst[1][4] = 1;
                  monst[2][4] = 2;
                  monst[3][4] = 1;
                  if (lives != 0)
                     repaint();     
               }
               else {
                  for (i=0;i<4;i++)
                     if ((monst[i][0] == hero[0]) && (monst[i][1] == hero[1]))                  
                        break;
                  eatm.play();
                  points += 5;
                  monst[i][0] = monst[i][1] = 180;
                  monst[i][2] = 420;
                  monst[i][3] = 380;
                  monst[i][4] = 8;
                  monst[i][5] = 0;
                  monst[i][6] = 30;
                  drawit();
               }                  
            }             
            if (eaten == 169) {
               for (i=0;i<14;i++)
                  for (j=0;j<14;j++)
                     virtplgr[i][j] = 1;
               virtplgr[10][3] = 2;
               level++;
               speed -= 50;
               repaint();
            }
            if (eaten == 338) {
               level++;
               for (i=0;i<14;i++)
                  for (j=0;j<14;j++)
                     virtplgr[i][j] = 1;
               virtplgr[10][3] = 2;
               speed -= 50;
               repaint();
            }
         }
      }
      packman = null;
   }   
}   