package com.u2d.sympster;

import com.u2d.model.ComplexEObject;
import com.u2d.type.atom.StringEO;
import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 6, 2006
 * Time: 5:06:00 PM
 */
@Entity
public interface Event extends ComplexEObject
{
   public StringEO getTitle();
}
