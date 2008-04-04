package com.u2d.type.composite;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.Email;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.FileEO;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;
import com.u2d.utils.Launcher;

import java.util.List;
import java.util.ArrayList;

public class EmailMessage
      extends AbstractComplexEObject
{
   private final Email _to = new Email();
   private final StringEO _subject = new StringEO();
   private final TextEO _body = new TextEO();
   private final FileEO _attachment = new FileEO();
   
   public static String[] fieldOrder = {"to", "subject", "body", "attachment"};
   
   // temporary hack until lists can deal with plain old eobjects (not just complexeobjects)
   private List<String> to_addresses = new ArrayList<String>();
   
   public EmailMessage()
   {
   }
   
   public EmailMessage(Email to, StringEO subject)
   {
      _to.setValue(to);
      _subject.setValue(subject);
   }
   public EmailMessage(Emailable to, StringEO subject)
   {
      _to.setValue(to.emailAddress());
      _subject.setValue(subject);
      
   }
   public EmailMessage(Emailable to, StringEO subject, TextEO body)
   {
      this(to, subject);
      _body.setValue(body);
   }
   public EmailMessage(Emailable to, StringEO subject, TextEO body, FileEO attachment)
   {
      this(to, subject, body);
      _attachment.setValue(attachment);
   }
   
   public Email getTo() { return _to; } 
   public StringEO getSubject() { return _subject; } 
   public TextEO getBody() { return _body; } 
   public FileEO getAttachment() { return _attachment; }
   

   public Title title()
   {
      return _subject.title();
   }


   public void addRecipient(String emailAddress)
   {
      to_addresses.add(emailAddress);
   }
   private String addresses()
   {
      // comma-separate list of addresses..
      StringBuffer buf = new StringBuffer(_to.stringValue());
      for (String email : to_addresses) {
         buf.append(",").append(email);
      }
      return buf.toString();
   }
   
   // mailto:eitan@u2d.com?subject=test&body=see attachment&attachment=/home/eitan/beryl-settings.Profile
   @Cmd(mnemonic='a')
   public void OpenInEmailApp(CommandInfo cmdInfo)
   {
      Launcher.openInEmailApp(this);
   }
   
   public String mailtoURL()
   {
      String mailto = "mailto:" + htmlEscape(addresses()) + "?subject=" + htmlEscape(_subject.stringValue());
      if (!_body.isEmpty())
      {
         mailto += "&body=" + htmlEscape(_body.stringValue());
      }
      if (!_attachment.isEmpty())
      {
         mailto += "&attachment=" + htmlEscape(_attachment.toString());
      }
      return mailto;
   }
   
   public static String htmlEscape(String text) 
   {
      String retValue = text;
      retValue = replaceAll(retValue, "%", "%25");
      retValue = replaceAll(retValue, " ", "%20");
      retValue = replaceAll(retValue, "!", "%21");
      retValue = replaceAll(retValue, "\"", "%22");
      retValue = replaceAll(retValue, "#", "%23");
      retValue = replaceAll(retValue, "$", "%24");
      retValue = replaceAll(retValue, "&", "%26");
      retValue = replaceAll(retValue, "'", "%27");
      retValue = replaceAll(retValue, "*", "%2A");
      retValue = replaceAll(retValue, "+", "%2B");
      retValue = replaceAll(retValue, ",", "%2C");
      retValue = replaceAll(retValue, ".", "%2E");
      retValue = replaceAll(retValue, "/", "%2F");
      retValue = replaceAll(retValue, ":", "%3A");
      retValue = replaceAll(retValue, ";", "%3B");
      retValue = replaceAll(retValue, "<", "%3C");
      retValue = replaceAll(retValue, "=", "%3D");
      retValue = replaceAll(retValue, ">", "%3E");
      retValue = replaceAll(retValue, "?", "%3F");
      retValue = replaceAll(retValue, "@", "%40");
      retValue = replaceAll(retValue, "[", "%5B");
      retValue = replaceAll(retValue, "\\", "%5C");
      retValue = replaceAll(retValue, "]", "%5D");
      retValue = replaceAll(retValue, "^", "%5E");
      retValue = replaceAll(retValue, "_", "%5F");
      retValue = replaceAll(retValue, "`", "%60");
      retValue = replaceAll(retValue, "{", "%7B");
      retValue = replaceAll(retValue, "|", "%7C");
      retValue = replaceAll(retValue, "}", "%7D");
      retValue = replaceAll(retValue, "~", "%7E");

      retValue = replaceAll(retValue, "\r", "%0A");
      retValue = replaceAll(retValue, "\n", "%0D");
		
      return retValue;
   }
   private static String replaceAll(String source, String toReplace, String replacement) 
   {
      int idx = source.lastIndexOf(toReplace);
      if (idx != -1) 
      {
         StringBuffer ret = new StringBuffer(source);
         ret.replace(idx, idx+toReplace.length(), replacement);
         while ((idx=source.lastIndexOf(toReplace, idx-1)) != -1) 
         {
            ret.replace(idx, idx+toReplace.length(), replacement);
         }
         source = ret.toString();
      }
      return source;
   }
   

}
