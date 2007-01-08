package com.u2d.view.swing;

import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.app.PersistenceMechanism;
import com.u2d.calendar.Calendarable;
import com.u2d.view.swing.find.FindView;
import com.u2d.view.swing.calendar.CalendarFrame;
import com.u2d.view.swing.list.ListEOFrame;
import javax.swing.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 5, 2007
 * Time: 2:01:10 PM
 */
class FrameInfo
{
   protected String type;
   protected Long id;
   protected String classname;
   protected EObject _eo;
   
   public static String FIND = "Find", CALENDAR = "Calendar", LIST = "List", CEO = "ceo";

   public FrameInfo(String classname)
   {
      this.classname = classname;
   }
   public FrameInfo(String type, Long id, String classname)
   {
      this(classname);
      this.type = type;
      this.id = id;
   }
   public void restore(PersistenceMechanism pmech)
   {
      try
      {
         if (isFindType())
         {
            _eo = ComplexType.forClass(ceoClass());
         }
         else if (isCalendarType())
         {
            // needs to be fixed.. (disable for now)
            ComplexEObject ceo = pmech.load(ceoClass(), id);
            _eo = ((Calendarable) ceo).calendar();
         }
         else if (isListType())
         {
            _eo = pmech.browse(ceoClass());
         }
         else if (isCeoType())
         {
            _eo = pmech.load(ceoClass(), id);
         }
      }
      catch (ClassNotFoundException ex) {}
   }
   protected Class ceoClass() throws ClassNotFoundException
   {
      return Class.forName(classname);
   }
      
   public boolean isFindType() { return (FIND.equals(type)); }
   public boolean isCalendarType() { return (CALENDAR.equals(type)); }
   public boolean isListType() { return (LIST.equals(type)); }
   public boolean isCeoType() { return (CEO.equals(type)); }
   
   public JInternalFrame restoreFrame()
   {
      if (isFindType())
      {
         return new GenericFrame(new FindView((ComplexType) _eo));
      }
      else if (isCalendarType())
      {
         return new CalendarFrame(_eo.getMainView());
      }
      else if (isListType())
      {
         return new ListEOFrame(_eo.getMainView());
      }
      else if (isCeoType())
      {
         return new EOFrame(_eo.getMainView());
      }
      return null;
   }
   
   public static FrameInfo deserialize(XMLDecoder dec, PersistenceMechanism pmech)
   {
      FrameInfo finfo = new FrameInfo((String) dec.readObject(),
                                      (Long) dec.readObject(), (String) dec.readObject());
      finfo.restore(pmech);
      return finfo;
   }
   
   public void serialize(XMLEncoder enc)
   {
      enc.writeObject(type);
      enc.writeObject(classname);
      enc.writeObject(id);
   }
}

class FindFrameInfo extends FrameInfo
{
   public FindFrameInfo(String classname)
   {
      super(classname);
   }

   public void restore(PersistenceMechanism pmech)
   {
      try {
         _eo = ComplexType.forClass(ceoClass());
      } catch (ClassNotFoundException e) { e.printStackTrace(); }
   }


   public JInternalFrame restoreFrame()
   {
      return new GenericFrame(new FindView((ComplexType) _eo));
   }

   public void serialize(XMLEncoder enc)
   {
      super.serialize(enc);
   }
}