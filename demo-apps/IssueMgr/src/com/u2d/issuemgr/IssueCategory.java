package com.u2d.issuemgr;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;
import com.u2d.list.PagedList;
import com.u2d.element.CommandInfo;
import com.u2d.find.FieldPath;
import com.u2d.find.QuerySpecification;
import com.u2d.find.SimpleQuery;
import com.u2d.find.inequalities.IdentityInequality;
import com.u2d.reflection.Cmd;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 24, 2005
 * Time: 6:17:21 PM
 */
public class IssueCategory extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO();
   
   public static String defaultSearchPath = "name";

   public IssueCategory() {}

   public StringEO getName() { return _name; }

   @Cmd
   public Object Issues(CommandInfo cmdInfo)
   {
      ComplexType type = ComplexType.forClass(Issue.class);
      FieldPath path = new FieldPath("com.u2d.issuemgr.Issue#category");
      QuerySpecification spec =
            new QuerySpecification(path, new IdentityInequality().new Equals(), this);
      SimpleQuery query = new SimpleQuery(type, spec);

      return new PagedList(query);
   }

   public Title title() { return _name.title(); }
}
