package com.u2d.movielib;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 23, 2005
 * Time: 4:40:25 PM
 */
public class Genre extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO();

   public Genre() {}

   public StringEO getName() { return _name; }

   public Title title() { return _name.title(); }
}
