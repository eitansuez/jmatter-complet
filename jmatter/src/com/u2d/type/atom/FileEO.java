package com.u2d.type.atom;

import com.u2d.model.*;
import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2005
 * Time: 10:40:33 PM
 */
public class FileEO extends AbstractAtomicEO
{
   public enum FileUseIntent
   {
      OPEN, SAVE
   }
   protected File _value;
   protected FileUseIntent _intent = FileUseIntent.OPEN;

   public FileEO() {}
   public FileEO(File value) { _value = value; }
   public FileEO(String value) { this(new File(value)); }

   public Object getValue() { return _value; }
   public File fileValue() { return _value; }
   
   public String stringValue()
   {
      if (_value == null) return "";
      try {
         return _value.getCanonicalPath();
      } catch (IOException ex) { return ""; }
   }

   public void setValue(File value)
   {
      _value = value;
      fireStateChanged();
   }

   public void setValue(EObject value)
   {
      if (!(value instanceof FileEO))
         throw new IllegalArgumentException("Invalid type on set;  must be FileEO");
      setValue(((FileEO) value).fileValue());
   }

   public Title title() {  return new Title(toString()); }

   public String toString()
   {
      if (_value == null) return "";
      return _value.getName();
   }


   public boolean isEmpty()
   {
      return (_value == null);
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      if (!(obj instanceof FileEO)) return false;
      FileEO file = (FileEO) obj;
      return (_value.equals(file.getValue()));
   }

   public int hashCode() { return _value.hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getFileRenderer(); }
   public AtomicEditor getEditor() { return vmech().getFileEditor(); }

   // attempt to resolve stringValue as a path or url
   public void parseValue(String stringValue)
   {
      File file = new File(stringValue);
      setValue(file);
   }

   public EObject makeCopy() { return new FileEO(fileValue()); }

   public String escape() { return "\"" + stringValue() + "\""; }


   private static FileFilter dirFilter = new FileFilter()
   {
      public boolean accept(File file)
      {
         return file.isDirectory();
      }
   };

   public List listRecursive(FileFilter filter)
   {
      File[] files = _value.listFiles(filter);
      List filesList = new ArrayList(Arrays.asList(files));
      
      File[] subdirs = _value.listFiles(dirFilter);
      for (File subdir : subdirs)
      {
         filesList.addAll(new FileEO(subdir).listRecursive(filter));
      }
      
      return filesList;
   }
   
   
   public FileUseIntent getIntent() { return _intent; }

}
