/*
 * Created on Nov 1, 2004
 */
package com.u2d.app;

import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.restrict.CommandRestriction;
import com.u2d.restrict.FieldRestriction;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class Role extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO();
   private final RelationalList _users = new RelationalList(User.class);
   public static Class usersType = User.class;
   public static String usersInverseFieldName = "role";
   
   private final RelationalList _cmdRestrictions = new RelationalList(CommandRestriction.class);
   public static Class cmdRestrictionsType = CommandRestriction.class;
   public static String cmdRestrictionsInverseFieldName = "role";
   
   private final RelationalList _fldRestrictions = new RelationalList(FieldRestriction.class);
   public static Class fldRestrictionsType = FieldRestriction.class;
   public static String fldRestrictionsInverseFieldName = "role";

   public static String[] fieldOrder = {"name", "users", "cmdRestrictions", "fldRestrictions"};
   public static String[] identities = {"name"};

   public Role() {}

   public Role(String name)
   {
      _name.setValue(name);
   }
   
   public StringEO getName() { return _name; }
   public RelationalList getUsers() { return _users; }
   public RelationalList getCmdRestrictions() { return _cmdRestrictions; }
   public RelationalList getFldRestrictions() { return _fldRestrictions; }
   
   
   public Title title() { return _name.title(); }

}
