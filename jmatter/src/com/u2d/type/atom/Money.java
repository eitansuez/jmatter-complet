package com.u2d.type.atom;

import com.u2d.model.*;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Nov 5, 2008
 * Time: 3:57:08 PM
 */
public class Money extends AbstractAtomicEO implements Comparable<Money>
{
   private BigDecimal _amt = null;
   private Currency _currency = Currency.getInstance(Locale.getDefault());

   public Money() { }
   
   public Money(BigDecimal amt, String currencyCode)
   {
      setValue(amt, currencyCode);
   }

   public void setValue(BigDecimal amt, String currencyCode)
   {
      Currency currency = Currency.getInstance(currencyCode);
      setValue(amt, currency);
   }
   public void setValue(BigDecimal amt, Currency currency)
   {
      _amt = amt;
      _currency = currency;
      fireStateChanged();
   }
   
   public void setValue(EObject value)
   {
      if (value == null)
      {
         setEmpty();
         return;
      }
      if (!(value instanceof Money))
      {
         throw new IllegalArgumentException("Invalid type on set;  must be Money");
      }
      if (value.equals(this)) return; // same.

      Money m = (Money) value;
      setValue(m.amount(), m.currency());
   }

   public BigDecimal amount() { return _amt; }
   public void amount(BigDecimal amt)
   {
      _amt = amt;
      fireStateChanged();
   }
   public Currency currency() { return _currency; }
   public void currency(Currency c)
   {
      _currency = c;
      fireStateChanged();
   }


   public String toString() { return format(); }
   public Title title() { return new Title(toString()); }

   private String format()
   {
      if (isEmpty())
      {
         return "--";
      }
      return String.format("%.2f %s", _amt, _currency.getCurrencyCode());
   }
   
   public void parseValue(String stringValue) throws java.text.ParseException
   {
      if (StringEO.isEmpty(stringValue))
      {
         setEmpty();
         return;
      }

      stringValue = stringValue.trim();

      // first, try <amt><space><currencycode>
      try
      {
         String[] parts = stringValue.split(" ");
         String numPart = parts[0];
         String currencyCode = parts[1];

         double value = NumberFormat.getInstance().parse(numPart).doubleValue();
         BigDecimal amt = new BigDecimal(value);
         setValue(amt, currencyCode);
      }
      catch (Exception ex)
      {
         // fall back to the absence of the currency code:
         try
         {
            double value = NumberFormat.getInstance().parse(stringValue).doubleValue();
            BigDecimal amt = new BigDecimal(value);
            amount(amt);
         }
         catch (ParseException ex2)
         {
            String msg = String.format("Failed to parse value '%s'", stringValue);
            throw new java.text.ParseException(msg, 0);
         }
      }
   }

   public String marshal()
   {
      return String.format("%s %s", _amt, _currency.getCurrencyCode());
   }

   public boolean isEmpty() { return _amt == null; }
   public void setEmpty() { amount(null); }

   public EObject makeCopy()
   {
      return new Money(_amt, _currency.getCurrencyCode());
   }

   public int compareTo(Money o)
   {
      if (equals(o)) return 0;
      if (o.currency().equals(currency()))
      {
         return amount().compareTo(o.amount());
      }
      // cannot make comparison when two values use different currencies (need exchange rate)
      throw new RuntimeException("Cannot compare amounts of different currencies without exchange rate");
   }

   public AtomicRenderer getRenderer() { return vmech().getMoneyRenderer(); }
   public AtomicEditor getEditor() { return vmech().getMoneyEditor(); }


}
