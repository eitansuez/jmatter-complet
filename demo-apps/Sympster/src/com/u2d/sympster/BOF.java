package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.list.RelationalList;
import com.u2d.persist.Persist;

@Persist
public class BOF
      extends AbstractComplexEObject implements Event
{
   private final StringEO title = new StringEO();
   
   private final RelationalList participants = new RelationalList(Speaker.class);
   public static Class participantsType = Speaker.class;

   public static String[] fieldOrder = {"title", "participants"};

   public BOF() { }

   public StringEO getTitle() { return title; }
   public RelationalList getParticipants() { return participants; }

   public Title title()
   {
      return title.title().append(" with", participants);
   }
}
