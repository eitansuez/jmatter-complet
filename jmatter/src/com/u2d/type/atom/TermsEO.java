/*
 * Created on Mar 4, 2004
 */
package com.u2d.type.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;

/**
 * @author Eitan Suez
 */
public class TermsEO extends BooleanEO
{
   private String _termsText = "";
   
   public TermsEO() {}
   public TermsEO(String termsText) { _termsText = termsText; }

   public String terms() { return _termsText; }

   public AtomicRenderer getRenderer() { return vmech().getTermsRenderer(); }
   public AtomicEditor getEditor() { return vmech().getTermsEditor(); }

}
