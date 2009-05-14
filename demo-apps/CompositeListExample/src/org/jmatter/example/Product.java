package org.jmatter.example;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.Money;
import com.u2d.type.atom.USDollar;

import javax.persistence.Entity;

@Entity
public class Product extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "price"};
   public static String[] identities = {"name"};

   public Product() { }

   private final StringEO name = new StringEO();
   public StringEO getName() { return name; }

   private final USDollar price = new USDollar();
   public USDollar getPrice() { return price; }

   public Title title()
   {
      return name.title().appendParens(price);
   }
}
