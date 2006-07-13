/*
 * Created on Apr 7, 2005
 */
package com.u2d.xml;

import java.io.IOException;
import org.jfree.report.modules.parser.base.ReportGenerator;
import org.jfree.xml.ElementDefinitionException;

import junit.framework.TestCase;

/**
 * @author Eitan Suez
 */
public class JFreeReportXMLParseTest extends TestCase
{

   public void testJFreeReportXMLValidateDTDFlag()
   {
      ReportGenerator generator = ReportGenerator.getInstance();
      generator.setValidateDTD(false);
      assertTrue(!generator.isValidateDTD());
   }
   
   // turn off network connection first..
   public void testJFreeReportXMLValidateDTDForReal()
   {
      ReportGenerator generator = ReportGenerator.getInstance();
      generator.setValidateDTD(false);
      java.net.URL layoutURL = getClass().getResource("/com/u2d/xml/test.xml");

      try
      {
         generator.parseReport(layoutURL);
      }
      catch (ElementDefinitionException ex)
      {
         ex.printStackTrace();
         fail("should not have generated an exception");
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
         fail("should not have generated an exception");
      }
   }
   
}
