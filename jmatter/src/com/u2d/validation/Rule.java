package com.u2d.validation;

import com.u2d.model.EObject;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: May 12, 2008
 * Time: 11:16:11 AM
 */
public interface Rule
{
   boolean pass();
   boolean fail();
   String msg();
   List<EObject> inputs();
   EObject targetObject();
   Severity severity();
}
