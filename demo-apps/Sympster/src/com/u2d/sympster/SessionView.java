package com.u2d.sympster;

import com.u2d.view.ComplexEView;
import com.u2d.model.EObject;
import com.u2d.css4swing.style.ComponentStyle;
import com.u2d.type.atom.TimeEO;
import com.u2d.type.atom.TimeSpan;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 28, 2008
 * Time: 5:50:03 PM
 */
public class SessionView extends JPanel implements ComplexEView
{
   private Session _session;
   private ChangeListener titleChangeListener;

   public SessionView(Session session)
   {
      _session = session;
      init();
   }
   
   private void init()
   {
      FormLayout formlayout = new FormLayout("pref:grow, pref", "pref:grow, pref:grow");
      DefaultFormBuilder builder = new DefaultFormBuilder(formlayout, this);
      CellConstraints cc = new CellConstraints();
      builder.add(sessionTitle(), cc.rc(1, 1));
      builder.add(speakerPnl(), cc.rc(2, 1));
      builder.add(logisticsPnl(), cc.rchw(1, 2, 2, 1));
   }
   private JLabel sessionTitle()
   {
      final JLabel lbl = new JLabel();
      ComponentStyle.setIdent(lbl, "session-title");
      if (_session.getEvent() == null)
      {
          lbl.setText(_session.title().toString());
      }
      else
      {
          lbl.setText(_session.getEvent().getTitle().stringValue());
          titleChangeListener = new ChangeListener()
          {
             public void stateChanged(ChangeEvent e)
             {
                lbl.setText(_session.getEvent().getTitle().stringValue());
             }
          };
          _session.getEvent().getTitle().addChangeListener(titleChangeListener);
      }
      return lbl;
   }
   private JPanel speakerPnl()
   {
      JPanel speakerPnl = new JPanel();
      ComponentStyle.setIdent(speakerPnl, "speaker-pnl");
      if (_session.getEvent() instanceof Talk)
      {
         Talk talk = (Talk) _session.getEvent();
         Speaker speaker = talk.getSpeaker();
         
         FormLayout formlayout = new FormLayout("pref, 5px, pref:grow", "pref:grow");
         DefaultFormBuilder builder = new DefaultFormBuilder(formlayout, speakerPnl);
         CellConstraints cc = new CellConstraints();

         if (speaker == null)
         {
            builder.add(new JLabel("No speaker assigned yet for this session"));
         }
         else
         {
            builder.add(new JLabel(speaker.getPhoto().imageValue()), cc.rc(1, 1));
            builder.add(speakerInfoPnl(speaker), cc.rc(1, 3, "t, l"));
         }
      }
      else if (_session.getEvent() instanceof BOF)
      {
         // tbd
      }
      return speakerPnl;
   }
   private JPanel speakerInfoPnl(Speaker speaker)
   {
      FormLayout formlayout = new FormLayout("pref", "pref, pref, pref:grow");
      DefaultFormBuilder builder = new DefaultFormBuilder(formlayout);
      CellConstraints cc = new CellConstraints();
      builder.add(new JLabel(speaker.getName().stringValue()), cc.rc(1, 1));
      builder.add(new JLabel(speaker.getTitle().stringValue()), cc.rc(2, 1));
      JPanel pnl = builder.getPanel();
      pnl.setOpaque(false);
      return pnl;
   }
   private JPanel logisticsPnl()
   {
      JPanel pnl = new JPanel();
      ComponentStyle.setIdent(pnl, "logistics-pnl");
      
      FormLayout formlayout = new FormLayout("pref");
      DefaultFormBuilder builder = new DefaultFormBuilder(formlayout, pnl);
      
      TimeSpan span = _session.getTime();
      
      builder.appendRow("pref");
      SimpleDateFormat dayofweek = new SimpleDateFormat("EEEE");
      builder.append(new JLabel(dayofweek.format(span.startDate())));

      builder.appendRow("pref");
      builder.append(new JLabel(_session.getTime().formatAsDate()));
      
      builder.appendRow("pref");
      String fromTime = TimeEO.stdTimeFormat().format(span.startDate());
      String toTime = TimeEO.stdTimeFormat().format(span.endDate());
      String times = String.format("%s - %s", fromTime, toTime);
      builder.append(new JLabel(times));
      
      builder.appendGlueRow();
      Room room = _session.getLocation();
      JLabel roomLbl = new JLabel();
      if (room == null)
      {
          roomLbl.setOpaque(false);
          roomLbl.setText("--");
      }
      else
      {
          roomLbl.setOpaque(true);
          roomLbl.setBackground(room.getColor().colorValue());
          roomLbl.setText(room.title().toString());
      }
      builder.append(roomLbl);
      builder.appendGlueRow();
      
      return pnl;
   }

   public EObject getEObject() { return _session; }

   public void detach()
   {
      _session.getEvent().getTitle().removeChangeListener(titleChangeListener);
   }
   public void stateChanged(ChangeEvent e)
   {
   }
   public void propertyChange(PropertyChangeEvent evt)
   {
   }
   public boolean isMinimized() { return false; }
}
