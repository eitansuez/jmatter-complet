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
      return append(joiner, object, "");
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

   public Title appendNoSpace(String joiner, EObject object)
   {
      return appendNoSpace(joiner, object, "");
   }
   public Title appendNoSpace(String joiner, EObject object, String defaultValue)
   {
      if (string.length() > 0
         && (object != null && object.title().toString().length() > 0)
         || (defaultValue != null && defaultValue.length() > 0))
      {
         concat(joiner);
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

   public Title appendChar(char singleChar)
   {
      if (string.length() > 0)
      {
         string.append(singleChar);
      }
      return this;
   }
   public Title appendWrapped(String open, String close, String text)
   {
      if (text.length() > 0)
      {
         appendSpace().concat(open).concat(text).concat(close);
      }
      return this;
   }
   public Title appendWrapped(String open, String close, EObject object)
   {
      if (object != null && !object.isEmpty())
      {
         appendSpace().concat(open).concat(object).concat(close);
      }
      return this;
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


   /*
    * The basic difference with join is that the joiner is a word
    * and not punctuation.  I could try to programmatically distinguish
    * a punctuation from a word but there's no harm if a human did that.
    * With joiner words, a space exists on both sides of the word.
    */
   public Title join(String joinerWord, String text)
   {
      appendSpace();
      return append(joinerWord, text);
   }
   public Title join(String joinerWord, EObject object)
   {
      appendSpace();
      return append(joinerWord, object);
   }

   
   
   public String toString()
   {
      return string.toString();
   }


}
