package com.u2d.customui;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.composite.Name;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 23, 2006
 * Time: 2:10:02 PM
 */
public class Contact extends AbstractComplexEObject
{
   private final Name name = new Name();
   private final Address address = new Address();

   public static String[] fieldOrder = {"name", "address"};
   public static String[] tabViews = {"address"};
   public static final String[] flattenIntoParent = {"name"};

   public Contact() {}

   public void initialize() { address.initialize(); }

   public Name getName() { return name; }
   public Address getAddress() { return address; }

   public Title title() { return getName().title(); }

}
