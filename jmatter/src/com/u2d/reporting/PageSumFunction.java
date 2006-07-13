/*
 * Created on Apr 6, 2005
 */
package com.u2d.reporting;

import org.jfree.report.function.AbstractFunction;
import org.jfree.report.function.Expression;
import java.io.Serializable;
import java.math.BigDecimal;
import org.jfree.report.event.PageEventListener;
import org.jfree.report.event.ReportEvent;
import org.jfree.util.Log;

/**
 * A JFreeReport function that will sum a field not by group and
 * not by report but by page.
 * 
 * That is, if there are 10 items, listed 5 per page, then this sum
 * function can be used to print the sum of the first five items
 * of a specific numeric field on the first page.  On the second
 * page the sum will appear again but this time it will be the sum
 * of the next five items.
 * 
 * @author Eitan Suez
 */
public class PageSumFunction extends AbstractFunction
                             implements Serializable, PageEventListener
{
   private static final BigDecimal ZERO = new BigDecimal(0.0);

   private String _field;
   private transient BigDecimal _sum;

   public PageSumFunction() {}

   public PageSumFunction(final String name)
   {
     this();
     setName(name);
   }

   public void reportInitialized(final ReportEvent event)
   {
      _sum = ZERO;
   }

   public Object getValue() { return _sum; }

   public String getField() { return _field; }
   public void setField(final String field) { _field = field; }

   public void itemsAdvanced(final ReportEvent event)
   {
     final Object fieldValue = getDataRow().get(getField());
     if (!(fieldValue instanceof Number))
     {
       Log.error("ItemSumFunction.advanceItems(): problem adding number.");
       return;
     }

     final Number n = (Number) fieldValue;
     _sum = _sum.add(new BigDecimal(n.doubleValue()));
   }

   public Expression getInstance ()
   {
     return (PageSumFunction) super.getInstance();
   }

   public void pageCanceled(ReportEvent event) {}
   public void pageFinished(ReportEvent event) {}
   public void pageStarted(ReportEvent event) { _sum = ZERO; }
   public void pageRolledBack(ReportEvent event) {}

}
