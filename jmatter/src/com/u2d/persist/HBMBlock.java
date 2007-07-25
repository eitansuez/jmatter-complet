package com.u2d.persist;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 25, 2007
 * Time: 12:03:23 PM
 */
public interface HBMBlock
{
   public void invoke(Session session);
}
