package com.u2d.app;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.ChoiceEO;

public class UserPreferences
      extends AbstractComplexEObject
{
   private final ViewOpenChoice _openNewViews = new ViewOpenChoice(ViewOpenChoice.IN_NEWWINDOW);

   public UserPreferences()
   {
   }

   public ViewOpenChoice getOpenNewViews()
   {
      return _openNewViews;
   }

   public Title title()
   {
      if (parentObject() != null)
      {
         Title title = new Title(parentObject().title().toString());
         return title.append(":", "User Preferences");
      }
      return new Title("User Preferences");
   }
}
