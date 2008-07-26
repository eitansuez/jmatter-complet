package com.u2d.self;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.IntEO;
import com.u2d.list.RelationalList;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.view.View;
import javax.persistence.Entity;

@Entity
public class Space
      extends AbstractComplexEObject
{
   private final IntEO temperature = new IntEO();
   
   private final RelationalList balls = new RelationalList(Ball.class);
   public static Class ballsType = Ball.class;
   

   public Space()
   {
   }

   public RelationalList getBalls() { return balls; }
   public IntEO getTemperature() { return temperature; }
   
   @Cmd(mnemonic='s')
   public View Show(CommandInfo cmdInfo)
   {
      return new SpaceView(this);
   }
   

   public Title title()
   {
      return new Title("Container");
   }

}
