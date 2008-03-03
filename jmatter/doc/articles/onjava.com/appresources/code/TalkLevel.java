package org.jmatter.j1mgr;

import com.u2d.type.atom.StringEO;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.persist.Persist;
import com.u2d.view.EditWithCombo;

@Persist
@EditWithCombo
public class TalkLevel extends AbstractComplexEObject
{
   private final StringEO _value = new StringEO();

   public TalkLevel() { }

   public StringEO getValue() { return _value; }

   public Title title() { return _value.title(); }
}
