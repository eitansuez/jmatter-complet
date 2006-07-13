/*
 * Created on May 13, 2004
 */
package com.u2d.model;

import com.u2d.element.CommandInfo;
import com.u2d.field.Association;
import com.u2d.field.AssociationField;
import com.u2d.field.IndexedField;
import com.u2d.pattern.Onion;
import com.u2d.view.View;
import com.u2d.view.swing.find.FindView2;
import com.u2d.list.RelationalList;
import com.u2d.reflection.CommandAt;

/**
 * @author Eitan Suez
 */
public class NullAssociation extends NullComplexEObject
{
   private Association _association;

   public NullAssociation(RelationalList leo)
   {
      this((IndexedField) leo.field(), leo.parentObject());
   }

   public NullAssociation(AssociationField field, ComplexEObject parent)
   {
      super(field.fieldtype());

      String inverseFieldName = (String) Harvester.introspectField(parent.getClass(),
                                                                   field.name() + "InverseFieldName");
      field.setInverse(inverseFieldName);

      setField(field, parent);
      _association = parent.association(field.name());
   }

   public NullAssociation(Association association)
   {
      super(association.type());
      setField(association.field(), association.parent());
      _association = association;
   }

   public NullAssociation(IndexedField field, ComplexEObject parent)
   {
      super(field.type());
      setField(field, parent);
      _association = parent.association(field.name());
   }

//   public Title title() { return _association.title(); }
//   public boolean isEmpty() { return _association.isEmpty(); }

   // override commandNew to also do the binding/association
   @CommandAt
   public ComplexEObject New(CommandInfo cmdInfo)
   {
      return New(cmdInfo, _type);
   }

   @CommandAt
   public ComplexEObject New(CommandInfo cmdInfo, ComplexType type)
   {
      final ComplexEObject ceo = type.New(cmdInfo);
      _association.associate(ceo);
      return ceo;
   }

   // if you have any 1+ parameter actions defined where at least
   // one parameter is of type complextype, then you must provide
   // this accompanying method, which is invoked reflectively by
   // paramslistview
   public ComplexType abstractType()
   {
      if (field().isAbstract())
      {
         return field().fieldtype();
      }
      return null;
   }
   public boolean isAbstract() { return field().isAbstract(); }



   @CommandAt
   public Object Browse(CommandInfo cmdInfo)
   {
      if (_association.field().isIndexed())
      {
         return vmech().getMultiPickView(_association.getAsList());
      }
      else
      {
         // using callbacks in this fashion is not necessary.
         // instead just return a special view of the object itself
         // that will display a view of a list of items to pick from.
         // it can then perform the association without needing a 
         // callback.  see what multipickview does for an example.
         AbstractListEO leo = _type.Browse(cmdInfo);
         leo.setPickState(_association);
         return leo;
      }
   }
   @CommandAt
   public View Find(CommandInfo cmdInfo)
   {
      View findView = _type.Find(cmdInfo);
      // i know i'm asking for trouble here..this is a first pass impl..
      ((FindView2) findView).setPickState(_association);
      return findView;
   }

   //    @CommandAt
   //   public void Association(CommandAt cmdInfo)
//   {
//      type().Paste(cmdInfo);
//   }

   public void set(ComplexEObject ceo)
   {
      _association.set(ceo);
   }

   public ComplexType type() { return field().fieldtype(); }

   public Onion commands()
   {
      Onion staticTypeCmds = field().fieldtype().staticCommands();

      if (staticTypeCmds.isEmpty())
         return cmds2;

      Onion copy = staticTypeCmds.deepCopy();
      copy.mergeIn(cmds2);
      return copy;
   }

   static Onion cmds2;
   static
   {
      cmds2 = Harvester.simpleHarvestCommands(NullAssociation.class,
                                              new Onion(), false, null, true /* shallow */);
   }

}
