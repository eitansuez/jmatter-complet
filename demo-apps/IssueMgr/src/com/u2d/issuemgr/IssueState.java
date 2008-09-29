package com.u2d.issuemgr;

import java.util.*;
import com.u2d.type.atom.ChoiceEO;
import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 22, 2005
 * Time: 10:00:31 PM
 */
public class IssueState extends ChoiceEO
{
   public IssueState() {}
   public IssueState(String value) { setValue(value); }
   
   private static Set<String> STATUS_OPTIONS = new HashSet<String>();
   static
   {
      STATUS_OPTIONS.add(Issue.NEW);
      STATUS_OPTIONS.add(Issue.ASSIGNED);
      STATUS_OPTIONS.add(Issue.ACCEPTED);
      STATUS_OPTIONS.add(Issue.FIXED);
      STATUS_OPTIONS.add(Issue.CLOSED);
   }
   
   public Collection entries() { return STATUS_OPTIONS; }

   public String toString()
   {
      String localizedValue = ComplexType.localeLookupStatic("IssueState." + _value);
      return (localizedValue == null) ? _value : localizedValue;
   }

}
