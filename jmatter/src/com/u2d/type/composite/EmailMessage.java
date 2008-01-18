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
      String mailto = "mailto:" + addresses() + "?subject=" + _subject;
      if (!_body.isEmpty())
      {
         mailto += "&body=" + _body;
      }
      if (!_attachment.isEmpty())
      {
         mailto += "&attachment=" + _attachment;
      }
      return mailto;
   }

}
