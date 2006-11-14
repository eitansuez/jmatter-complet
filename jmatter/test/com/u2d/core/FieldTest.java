/*
 * Created on Jan 19, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.*;
import com.u2d.type.composite.*;
import com.u2d.domain.*;
import com.u2d.element.Field;
import com.u2d.field.AggregateField;
import com.u2d.field.AtomicField;
import com.u2d.field.CompositeField;

/**
 * @author Eitan Suez
 */
public class FieldTest extends TestCase
{
   Shipment _shipment;
   AtomicField _nameField, _weightField;
   Field _fromField, _toField;
   
   protected void setUp() throws Exception
   {
      _shipment = new Shipment("My Shipment", 25);
      ComplexType shipmentEType = ComplexType.forClass(Shipment.class);
      
      _nameField = new AtomicField(shipmentEType, "name");
      _weightField = new AtomicField(shipmentEType, "weight");
      
      _fromField = new AggregateField(shipmentEType, "from");
      _toField = new AggregateField(shipmentEType, "to");
   }

   public void testDeriveLabel()
   {
      String result = Field.deriveLabel("name");
      assertEquals("Expected Name but got "+result, "Name", result);
      result = Field.deriveLabel("phoneNumber");
      assertEquals("Expected Phone Number but got "+result, "Phone Number", result);
      result = Field.deriveLabel("swapFirstAndLastNames");
      assertEquals("Expected Swap First And Last Names but got "+result, "Swap First And Last Names", result);
   }
   
   public void testSetDefaultValueWrongType()
   {
      try
      {
         _nameField.getDefaultValue().setValue(new IntEO(3));
         fail("Should have disallowed default value of the wrong type");
      }
      catch (IllegalArgumentException expected)
      {
         assertTrue(true);
      }
   }
   
   public void testSetDefaultValueCorrectType()
   {
      try
      {
         _nameField.getDefaultValue().setValue(new StringEO("Mr. hmm"));
      }
      catch (IllegalArgumentException ex)
      {
         fail("value is of the correct type -- should not have thrown illegalargumentexception");
      }
   }
   
   public void testGetType()
   {
      assertEquals(StringEO.class, _nameField.getJavaClass());
      assertEquals(FloatEO.class, _weightField.getJavaClass());
      assertEquals(USAddress.class, _fromField.getJavaClass());
      assertEquals(USAddress.class, _toField.getJavaClass());
   }
   
   public void testGetStringType()
   {
      StringEO result = (StringEO) _nameField.get(_shipment);
      String resultVal = result.stringValue();
      assertEquals(resultVal, "My Shipment");
   }
   
   public void testGetIntType()
   {
      FloatEO result = (FloatEO) _weightField.get(_shipment);
      float resultVal = result.floatValue();
      assertEquals(resultVal, 25, 0.01);
   }
   
   public void testAtomic()
   {
      assertTrue(_nameField.isAtomic());
      assertTrue(_weightField.isAtomic());
   }

   public void testAggregate()
   {
      assertTrue(_fromField.isAggregate());
      assertTrue(_toField.isAggregate());
   }
   
   public void testNaturalFieldPath()
   {
      AggregateField fromField = (AggregateField) _shipment.type().field("from");
      Field fromCityField = fromField.field("city");
      assertEquals("Shipment From's City", fromCityField.getNaturalPath());
   }

   public void testIdentityRequired()
   {
      Field nameFld = _shipment.field("name");
      assertTrue(nameFld.isComposite());
      CompositeField nameFldComposite = (CompositeField) nameFld;
      assertTrue(nameFldComposite.isIdentity());
      assertTrue(nameFldComposite.required());
   }

}
