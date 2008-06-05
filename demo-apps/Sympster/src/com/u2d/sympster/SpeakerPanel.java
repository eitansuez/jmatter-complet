package com.u2d.sympster;

import com.u2d.view.swing.AbeilleForm;
import com.u2d.model.EObject;
import com.jeta.forms.gui.form.FormAccessor;
import com.jeta.forms.components.panel.FormPanel;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: May 23, 2008
 * Time: 1:30:15 PM
 */
public class SpeakerPanel extends AbeilleForm
{
   private Speaker speaker;

   public SpeakerPanel(Speaker speaker)
   {
      this.speaker = speaker;
      setupUI();
   }

   private void setupUI()
   {
      setOpaque(false);
      setLayout(new BorderLayout());

      FormPanel formPanel = formPanel();
      formPanel.setOpaque(false);
      FormAccessor accessor = formPanel.getFormAccessor("main");
      accessor.replaceBean("name", getView(speaker.getName()));
      accessor.replaceBean("title", getView(speaker.getTitle()));
      accessor.replaceBean("bio", getView(speaker.getBio()));
      accessor.replaceBean("photo", getView(speaker.getPhoto()));

      add(formPanel, BorderLayout.CENTER);
   }
   
   public EObject getEObject() { return speaker; }
}
