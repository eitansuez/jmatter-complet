package com.u2d.self;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 16, 2007
 * Time: 4:44:28 PM
 * 
 * Raison d'etre for this class is:  java.awt.Point is modeled as an int pair.
 * I need higher precision.
 */
public class Point
{
   public double x, y;
   public Point() {}
   public Point(double x, double y) { this.x = x; this.y = y; }
}
