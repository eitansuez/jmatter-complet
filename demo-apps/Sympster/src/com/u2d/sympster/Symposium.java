package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.Money;
import com.u2d.calendar.CalendarEO;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.element.Field;
import com.u2d.find.QuerySpecification;
import com.u2d.find.FieldPath;
import com.u2d.find.Inequality;
import com.u2d.find.inequalities.IdentityInequality;
import com.u2d.utils.Launcher;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.hibernate.Query;
import javax.persistence.Entity;

@Entity
public class Symposium extends CalendarEO
{
   private final StringEO name = new StringEO();
   private final Money registrationPrice = new Money();
   private Venue venue;
   private City city;

   public static Color colorCode = new Color(0x04b144);
   
   public static String[] fieldOrder = {"name", "registrationPrice", "city", "venue"};

   public Symposium() {}

   public StringEO getName() { return name; }
   
   public Venue getVenue() { return venue; }
   public void setVenue(Venue venue)
   {
      Venue oldVenue = this.venue;
      this.venue = venue;
      firePropertyChange("venue", oldVenue, this.venue);
   }
   // all venues where the city matches this symposium's city..
   public QuerySpecification venueOptions()
   {
      if (city == null) return null;  // if symposium.city is not set then no constraint on venue
      Field venueCity = ComplexType.forClass(Venue.class).field("city");
      FieldPath venueCityPath = new FieldPath(venueCity.fullPath());
      Inequality equals = new IdentityInequality(venueCity).new Equals();
      return new QuerySpecification(venueCityPath, equals, city);
   }
   
   public City getCity() { return city; }
   public void setCity(City city)
   {
      City oldCity = this.city;
      this.city = city;
      firePropertyChange("city", oldCity, this.city);
   }
   
   public Money getRegistrationPrice() { return registrationPrice; }
   
   public Title title() { return name.title(); }
   public static String pluralName() { return "Symposia"; }
   
   public AbstractListEO schedulables()
   {
      if (venue == null)
      {
         return null;
      }
//      return ComplexType.forClass(Room.class).list();
      return venue.getRooms();
   }

   public Class defaultCalEventType() { return Session.class; }

   @Cmd(mnemonic='l')
   public Object ShowCalendar(CommandInfo cmdInfo)
   {
      if (schedulables() == null || schedulables().isEmpty())
      {
         return "You must first specify a venue with rooms for this symposium";
      }
      return calendar();
   }
   
   @Cmd(mnemonic='l')
   public String ReportSchedule(CommandInfo cmdInfo)
         throws IOException
   {
      String queryString = "from Session s where s.symposium = :symposium order by s.time, s.event";
      Query hqlQuery = hbmPersistor().getSession().createQuery(queryString);
      hqlQuery.setParameter("symposium", this);
      AbstractListEO list = hbmPersistor().hqlQuery(hqlQuery);
      if (list == null) { return "This symposium has no sessions"; }
      List sessions = list.getItems();
      
      Map paramMap = new HashMap();
      String sympoTitle = String.format("%s schedule", this);
      paramMap.put("symposium_title", sympoTitle);

      
      /*
      alternative:  embed the query (parametrized) directly into the jrxml.
      something along these lines:
        <parameter name="symposium_id" class="java.lang.Long" />
        <queryString language="hql"><![CDATA[
          from Session s where s.symposium.id = $P{symposium_id}) order by s.time, s.event
          ]]></queryString>
        <field ... /> (fields used in report must be specified)
      and:
          paramMap.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION, session);
          paramMap.put("symposium_id", getID());
       */
      
      String reportName = "com/u2d/sympster/SessionsReport.jasper";
      try
      {
         JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(sessions);
         InputStream reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(reportName);

         JasperPrint print = JasperFillManager.fillReport(reportStream, paramMap, ds);
         File reportFile = File.createTempFile("report", ".pdf");
         reportFile.deleteOnExit();
         JRPdfExporter pdfExporter = new JRPdfExporter();
         pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
         pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE, reportFile);
         pdfExporter.exportReport();
         Launcher.openFile(reportFile);
         return null;
      }
      catch (JRException e)
      {
         e.printStackTrace();
         throw new RuntimeException("Failed to find resource "+reportName, e);
      }
   }
   
}
