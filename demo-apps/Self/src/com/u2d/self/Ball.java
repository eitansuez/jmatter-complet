package com.u2d.self;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.IntEO;
import com.u2d.type.atom.ColorEO;
import com.u2d.type.atom.BooleanEO;
import javax.persistence.Entity;

@Entity
public class Ball
      extends AbstractComplexEObject
{
   private final IntEO radius = new IntEO();
   private final ColorEO color = new ColorEO();
   private final BooleanEO filled = new BooleanEO();

   public Ball()
   {
   }

   public IntEO getRadius() { return radius; }
   public ColorEO getColor() { return color; }
   public BooleanEO getFilled() { return filled; }

   public Title title()
   {
      return new Title("Ball");
   }
}
