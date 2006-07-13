package com.u2d.find;

import org.hibernate.Criteria;
import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.type.atom.StringEO;
import com.u2d.list.CriteriaListEO;

/**
 * Date: May 23, 2005
 * Time: 11:19:07 AM
 *
 * @author Eitan Suez
 */
public interface Query extends ComplexEObject
{
   public Criteria getCriteria();
   public ComplexType getQueryType();
   public StringEO getName();
   public CriteriaListEO execute();
}
