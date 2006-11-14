/*
 * Created on Apr 22, 2005
 */
package com.u2d.find;

import java.util.LinkedList;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.u2d.element.Field;
import com.u2d.field.AssociationField;
import com.u2d.type.atom.StringEO;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.*;

/**
 * @author Eitan Suez
 */
public class FieldPath extends AbstractAtomicEO
{
   private LinkedList _pathList;
   private String _pathString;
   
   public FieldPath() {}  // required by hibernate
   
   public FieldPath(String path)
   {
      _pathString = path;
      derivePathList();
   }
   public FieldPath(LinkedList path)
   {
      _pathList = path;
      derivePathString();
   }
   
   public String getPathString() { return _pathString; }
   public LinkedList getPathList() { return _pathList; }
   
   private void derivePathString()
   {
      StringBuffer buf = new StringBuffer();
      Object item = null;
      boolean first = true;
      for (int i=1; i<_pathList.size(); i++)
      {
         item = _pathList.get(i);
         if (item instanceof ComplexType.AssociationSearchNode)
         {
            Field field = ((ComplexType.AssociationSearchNode) item).getField();
            if (first) first = false;
            else buf.append("|");
            buf.append(field.fullPath());
         }
      }
      Field field = (Field) _pathList.getLast();
      if (!first) buf.append("|");
      buf.append(field.fullPath());
      
      _pathString = buf.toString();
   }
   
   public int length() { return _pathList.size(); }
   
   private void derivePathList()
   {
      _pathList = new LinkedList();
      
      String[] fieldPaths = _pathString.split("\\|");

      AssociationField afield = null;
      
      for (int i=0; i<fieldPaths.length - 1; i++)
      {
         afield = (AssociationField) Field.forPath(fieldPaths[i]);
         ComplexType.AssociationSearchNode node =
            afield.fieldtype().new AssociationSearchNode(afield);
         _pathList.addLast(node);
      }
      
      Field field = null;
      int last = fieldPaths.length - 1;
      field = Field.forPath(fieldPaths[last]);
      _pathList.addFirst(field.parent());
      _pathList.addLast(field);
   }
   
   public String toString() { return _pathString; }
   public ComplexType pathtype() { return (ComplexType) _pathList.get(0); }
   
   public Criteria getCriteria(Session s) throws HibernateException
   {
      Criteria c = s.createCriteria(pathtype().getJavaClass());
      return specify(c);
   }
   
   public Criteria specify(Criteria c) throws HibernateException
   {
      Object node;
      Field field;
      Criteria subCriteria = c;
      for (int i = 1; i < _pathList.size(); i++)
      {
         node = _pathList.get(i);
         if (node instanceof ComplexType.AssociationSearchNode)
         {
            field = ((ComplexType.AssociationSearchNode) node).getField();
            subCriteria = subCriteria.createCriteria(field.getCleanPath());
         }
      }
      return subCriteria;
   }
   
   public Field getLastField() { return (Field) _pathList.getLast(); }

   
   // ===== EOBject implementation ====//
   
   
   public boolean isEmpty() { return StringEO.isEmpty(_pathString); }

   public void parseValue(String stringValue)
   {
      if (stringValue == null) stringValue = "";
      _pathString = stringValue;
      derivePathList();
   }
   
   public EObject makeCopy()
   {
      return new FieldPath(this.getPathString());
   }
   
   // i'm hoping to get away with a custom view for the only context
   // in which fieldpath will be used:  a queryspecification view
   public AtomicRenderer getRenderer() { return null; }
   public AtomicEditor getEditor() { return null; }

   public void setValue(EObject value)
   {
      if (!(value instanceof FieldPath))
         throw new IllegalArgumentException("Invalid type on set; must be FieldPath");
      FieldPath path = ((FieldPath) value);
      _pathString = path.getPathString();
      _pathList = path.getPathList();
      fireStateChanged();
   }
   
   public Title title() { return new Title(_pathString); }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof FieldPath)) return false;
      return _pathString.equals(((FieldPath) obj).getPathString());
   }

   public int hashCode() { return _pathString.hashCode(); }

}
