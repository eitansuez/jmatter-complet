package org.jmatter.j1mgr;

import com.u2d.model.AbstractListEO;
import com.u2d.persist.Persist;
import com.u2d.app.User;
import com.u2d.list.RelationalList;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;

@Persist
public class Attendee
      extends User
{
   private final RelationalList agenda = new RelationalList(Talk.class);
   public static Class agendaType = Talk.class;
   
   public Attendee()
   {
   }
   
   public RelationalList getAgenda() { return agenda; }
   
   public boolean addToAgenda(Talk talk)
   {
      if (agenda.contains(talk)) return false;
      
      agenda.add(talk);
      save();
      return true;
   }
   
   @Cmd(mnemonic='a')
   public AbstractListEO MyAgenda(CommandInfo cmdInfo)
   {
      return agenda;
   }
   

}
