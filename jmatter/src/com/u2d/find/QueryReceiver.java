package com.u2d.find;

import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 19, 2007
 * Time: 12:33:49 PM
 */
public interface QueryReceiver
{
   public ComplexType queryType();
   public void setQuery(Query query);
}
