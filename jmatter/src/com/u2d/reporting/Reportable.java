/*
 * Created on May 4, 2004
 */
package com.u2d.reporting;

import javax.swing.table.TableModel;
import java.util.*;

/**
 * @author Eitan Suez
 */
public interface Reportable
{
   public String reportName();
   public Properties properties();
   public TableModel tableModel();
   
   public ReportFormat reportFormat();
}
