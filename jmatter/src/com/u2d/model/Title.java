package com.u2d.model;

/**
 * Original is org.nakedobjects.object.Title
 * 
 * Credit for this class is hereby given to the authors of the NO framework and to the nakedobjects.org organization
 */
public class Title
{
   private StringBuffer string = new StringBuffer();

   public Title()
   {
      super();
   }

   public Title(String text)
   {
      concat(text);
   }

   public Title(EObject object)
   {
      concat(object);
   }

   public Title(EObject object, String defaultValue)
   {
      concat(object, defaultValue);
   }

   public Title append(int number)
   {
      append(String.valueOf(number));
      return this;
   }

   public Title append(String text)
   {
      if (!text.equals(""))
      {
         appendSpace();
         string.append(text);
      }
      return this;
   }

   public Title append(String joiner, String text)
   {
      if (!text.equals(""))
      {
         if (string.length() > 0)
         {
            concat(joiner);
         }
         appendSpace();
         string.append(text);
      }
      return this;
   }

   public Title append(String joiner, EObject object)
   {
      append(joiner, object, "");
      return this;
   }

   public Title append(String joiner, EObject object, String defaultValue)
   {
      if (string.length() > 0
         && (object != null && object.title().toString().length() > 0)
         || (defaultValue != null && defaultValue.length() > 0))
      {
         concat(joiner);
         appendSpace();
         concat(object, defaultValue);
      }
      return this;
   }

   public Title append(EObject object)
   {
      if (object != null
         && object.title() != null
         && !object.title().toString().equals(""))
      {
         appendSpace();
         string.append(object.title());
      }
      return this;
   }

   public Title append(EObject object, String defaultValue)
   {
      appendSpace();
      concat(object, defaultValue);
      return this;
   }

   public Title appendSpace()
   {
      if (string.length() > 0)
      {
         string.append(" ");
      }
      return this;
   }

   public Title appendWrapped(String open, String close, String text)
   {
      return appendSpace().concat(open).concat(text).concat(close);
   }
   public Title appendWrapped(String open, String close, EObject object)
   {
      return appendSpace().concat(open).concat(object).concat(close);
   }
   public Title appendParens(String text)
   {
      return appendWrapped("(", ")", text);
   }
   public Title appendParens(EObject object)
   {
      return appendWrapped("(", ")", object);
   }
   public Title appendBracket(String text)
   {
      return appendWrapped("[", "]", text);
   }
   public Title appendBracket(EObject object)
   {
      return appendWrapped("[", "]", object);
   }
   public Title appendBrace(EObject object)
   {
      return appendWrapped("{", "}", object);
   }

   public final Title concat(String text)
   {
      string.append(text);
      return this;
   }

   public final Title concat(EObject object)
   {
      concat(object, "");
      return this;
   }

   public final Title concat(EObject object, String defaultValue)
   {
      if (object == null)
      {
         string.append(defaultValue);
      }
      else
      {
         string.append(object.title());
      }
      return this;
   }

   public String toString()
   {
      return string.toString();
   }
}
