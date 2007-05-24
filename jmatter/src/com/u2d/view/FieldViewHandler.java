package com.u2d.view;

import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.element.Field;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 24, 2007
 * Time: 1:06:27 PM
 */
public interface FieldViewHandler
{
   public EView getView(EObject value, EView defaultView);
}
