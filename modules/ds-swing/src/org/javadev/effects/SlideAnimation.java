/*
Copyright (c) 2004 
Sam Berlin sberlin@limepeer.com
Luca Lutterotti Luca.Lutterotti@ing.unitn.it
Dmitry Markman, PhD dmarkman@mac.com
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.javadev.effects;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.*;


/**
 * The SlideAnimation is a class to animate the transition between
 * one component and the other for AnimatingCardLayout. Based on the Dashboard
 * code of Dmitry Markman.
 * <p/>
 * Description
 *
 * @author Luca Lutterotti
 * @author Dmitry Markman
 * @version $Revision: 1.1 $, $Date: 2004/12/21 16:58:14 $
 * @since JDK1.1
 */

public class SlideAnimation implements Animation {

  SpecialPanel animationPanel = null;
  private AnimationListener listener = null;
  boolean direction = true;
  int       animationDuration = 2000;

  public void setDirection(boolean direction){
    this.direction = direction;
  }

  public void setAnimationDuration(int animationDuration){
    this.animationDuration = (animationDuration < 1000)?1000:animationDuration;
  }


  public Component animate(final Component toHide, final Component toShow, AnimationListener listener) {
    this.listener = listener;
    animationPanel = new SpecialPanel(this, (direction)?toHide:toShow, (direction)?toShow:toHide);
    animationPanel.needToStartThread = true;
    animationPanel.beginAngle = (direction)?0:180;
    animationPanel.endAngle = (direction)?180:0;
    animationPanel.setAnimationDuration(animationDuration);
    return animationPanel;


  }

  public Component getAnimationPanel() {
    return animationPanel;
  }

  void rotationFinished() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        animationPanel = null;
        listener.animationFinished();
        listener = null;
      }
    });
  }


  class SpecialPanel extends JPanel{
  SlideAnimation owner;

  BufferedImage firstImage;
  BufferedImage secondImage;
  Component component1;
  Component component2;

  float angle = 0;

  public float beginAngle = 0;
  public float endAngle = 360;

  float deltaAngle = 0.5f;
  float effectTime = 2000;
  long dt = Math.round(effectTime* deltaAngle/180);
  int counter = 0;
  long totalDrawTime = 0;

  public boolean needToStartThread = false;

      SpecialPanel(SlideAnimation owner,BufferedImage firstImage, BufferedImage secondImage){
          this.owner = owner;
          this.firstImage = firstImage;
          this.secondImage = secondImage;
          angle = beginAngle;
          setOpaque(false);
      }

      SpecialPanel(SlideAnimation owner,Component component1, Component component2){
          this.owner = owner;
          this.component1 = component1;
          this.component2 = component2;
          angle = beginAngle;
          setOpaque(false);
      }

      public void setAnimationDuration(int animationDuration){
        effectTime = (animationDuration < 1000)?1000:animationDuration;
        dt = Math.round(effectTime* deltaAngle/180);
      }

      void startThread(float val1,float val2){
          counter = 0;
          totalDrawTime = 0;
          this.beginAngle = val1;
          this.endAngle = val2;
          if(endAngle < beginAngle)   deltaAngle = -Math.abs(deltaAngle);
          else                        deltaAngle = Math.abs(deltaAngle);
          angle = beginAngle;          
          final Runnable repaint = new Runnable() { //am@kikamedical.com Arnaud Masson
             public void run() {
                 repaint();
                 getToolkit().sync();
             }                                      };
          Thread t = new Thread(new Runnable(){
              public void run(){
                  float absDeltaAngle=Math.abs(deltaAngle);
                  long startTime = System.currentTimeMillis();
                  long initTime = System.currentTimeMillis();
                  while(true){
                      long time = System.currentTimeMillis();
                      angle += deltaAngle*(time - startTime)/dt;//idea am@kikamedical.com Arnaud Masson
                      startTime = time;
                      if(((angle >= endAngle-deltaAngle/2) && (deltaAngle > 0)) ||
                         ((angle <= endAngle-deltaAngle/2) && (deltaAngle < 0))){
                          angle = endAngle;
                          if(Math.abs(angle - 360) < absDeltaAngle / 2) angle = 0;
                          if(Math.abs(angle - 180) < absDeltaAngle / 2) angle = 180;
                          repaint();
                          if(counter != 0) System.out.println("total count "+counter+" time "+(System.currentTimeMillis() - initTime)+" average time "+(totalDrawTime/counter));
                          break;
                      }
                      if(angle >= 360) angle = 0;
                      try{
                          Thread.sleep(dt);
                          repaint();
                          getToolkit().sync();
                         //SwingUtilities.invokeAndWait(repaint);  //idea am@kikamedical.com Arnaud Masson
                      }catch(Throwable tt){
                      }
                  }
                  if(owner != null) owner.rotationFinished();
                  synchronized(SpecialPanel.this){
                      if(component1 != null) firstImage = null;
                      if(component2 != null) secondImage = null;
                  }
              }
          });
          t.start();
      }

      public void update(Graphics g){
          paint(g);
      }

      public synchronized void paint(Graphics g){
          if(needToStartThread){
              totalDrawTime = 0;
              counter = 0;
              needToStartThread = false;
              startThread(beginAngle,endAngle);
              if(firstImage == null){
                  firstImage = createImageFromComponent(component1);
              }
              if(secondImage == null){
                  secondImage = createImageFromComponent(component2);
              }
          }
          if(firstImage == null || secondImage == null) return;
          Graphics2D g2d = (Graphics2D)g;
          int ww = firstImage.getWidth();
          int hh = firstImage.getHeight();
          {
              BufferedImage currImage = null;
              int []currPixels = null;
              int w = firstImage.getWidth();
              int offset = (int)(w*angle/180);
              if(offset < 0) offset = 0;
              if(offset > w) offset = w;

              long beforeDraw = System.currentTimeMillis();
              g2d.drawImage(firstImage,null,0,0);
              g2d.drawImage(secondImage,null,w - offset,0);
              totalDrawTime += (System.currentTimeMillis() - beforeDraw);
              counter++;

        }
      }

      BufferedImage createImageFromComponent(Component comp){
          BufferedImage retImage = null;
          if(comp == null) return retImage;
          try{
              GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
              GraphicsDevice gd = genv.getDefaultScreenDevice();
              GraphicsConfiguration gc = gd.getDefaultConfiguration();
              java.awt.image.ColorModel cm = gc.getColorModel();
            boolean hasAlpha = cm.hasAlpha();
            int cw = comp.getSize().width;
            int ch = comp.getSize().height;
            if(hasAlpha){
                retImage = gc.createCompatibleImage(cw,ch);
            }else{
                retImage = new BufferedImage(cw,ch,BufferedImage.TYPE_INT_ARGB);
            }
              if(retImage == null) return retImage;
              Graphics og = retImage.getGraphics();
                  comp.paint(og);
              og.dispose();
          }catch(Throwable t){
          }
          return retImage;

      }

  }



}
