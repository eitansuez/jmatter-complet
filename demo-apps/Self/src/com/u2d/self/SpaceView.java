package com.u2d.self;

import com.u2d.view.View;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.Date;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 16, 2007
 * Time: 3:27:08 PM
 */
public class SpaceView extends JPanel implements View
{
   private Space _space;
   private Dimension _size = new Dimension(300,150);
   
   private java.util.List<Point> positions = new ArrayList<Point>();
   private java.util.List<Double> directions = new ArrayList<Double>();
   
   private transient Random random = new Random(new Date().getTime());
   private double speed;

   public SpaceView(Space space)
   {
      _space = space;
      setSize(_size);
      setPreferredSize(_size);
      setMaximumSize(_size);
      setMinimumSize(_size);
      setLayout(null);
      
      updateSpeed();
      
      space.getTemperature().addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            updateSpeed();
         }
      });
      
      Timer timer = new Timer(40, new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updatePositions();
            repaint();
         }
      });
      timer.setRepeats(true);
      timer.setInitialDelay(500);
      timer.start();
   }
   
   private void updateSpeed()
   {
      int temp = _space.getTemperature().intValue();
      if (temp < 0) temp = 0;
      speed = temp / 10.0;
   }


   protected void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      // draw background..
      g2.setColor(Color.white);
      g2.fillRect(0, 0, getWidth()-1, getHeight()-1);
      // draw a border..
      BasicStroke stroke = new BasicStroke(3);
      g2.setStroke(stroke);
      g2.setColor(Color.yellow);
      g2.drawRect((int) stroke.getLineWidth()/2, (int) stroke.getLineWidth()/2, 
                  getWidth()-(int) stroke.getLineWidth(), getHeight()-(int) stroke.getLineWidth());
      
      ensurePositions();
      
      // for each ball..
      for (int i=0; i<_space.getBalls().getSize(); i++)
      {
         // draw a circle with ball color and fn(radius)
         Ball ball = (Ball) _space.getBalls().get(i);
         
         Point position = positions.get(i);
         int length = ball.getRadius().intValue();
         g2.setColor(ball.getColor().colorValue());
         if (ball.getFilled().booleanValue())
         {
            g2.fillOval((int) position.x, (int) position.y, length, length);
         }
         else
         {
            g2.drawOval((int) position.x, (int) position.y, length, length);
         }
      }
   }
   
   private void ensurePositions()
   {
      int numballs = _space.getBalls().getSize();
      if (positions.size() < numballs)
      {
         for (int i=positions.size(); i<numballs; i++)
         {
            positions.add(newPosition());
            directions.add(newDirection());
         }
      }
   }
   
   private void updatePositions()
   {
      java.util.List<Point> newPositions = new ArrayList<Point>(positions.size());
      for (int i=0; i<positions.size(); i++)
      {
         Point pos = positions.get(i);
         double theta = directions.get(i);
         double newx = pos.x + speed * Math.cos(theta);
         if (newx < 0) newx += getWidth();
         newx %= getWidth();
         double newy = pos.y + speed * Math.sin(theta);
         if (newy < 0) newy += getHeight();
         newy %= getHeight();
         Point newpos = new Point(newx, newy);
         newPositions.add(newpos);
      }
      positions = newPositions;
   }

   private Point newPosition()
   {
      // pick a random position
      int x = random.nextInt(getWidth());
      int y = random.nextInt(getHeight());
      return new Point(x, y);
   }
   private double newDirection()
   {
      return random.nextDouble() * Math.PI * 2;
   }

   public String getTitle() { return "View for Space"; }
   public Icon iconSm() { return null; }
   public Icon iconLg() { return null; }
   public boolean withTitlePane() { return false; }
}
