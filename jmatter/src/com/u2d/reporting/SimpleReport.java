package com.u2d.reporting;

import com.u2d.model.*;
import com.u2d.type.atom.StringEO;
import com.u2d.list.CriteriaListEO;
import com.u2d.list.RelationalList;
import com.u2d.element.Field;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;
import com.u2d.find.Query;
import com.u2d.view.swing.list.TableView;
import com.u2d.view.ListEView;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 24, 2006
 * Time: 1:20:07 PM
 */
public class SimpleReport extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO();
   private Query _query;

   private final RelationalList _fields = new RelationalList(Field.class);
   public static final Class fieldsType = Field.class;
   
   public static final String[] fieldOrder = {"name", "query", "fields"};
   
   public SimpleReport() {}
   
   public StringEO getName() { return _name; }
   public Query getQuery() { return _query; }
   public void setQuery(Query query)
   {
      Query oldQuery = _query;
      _query = query;
      firePropertyChange("query", oldQuery, _query);
   }
   public RelationalList getFields() { return _fields; }
   
   @Cmd(mnemonic='x')
   public ListEView Execute(CommandInfo cmdInfo)
   {
      CriteriaListEO leo = _query.execute();
      leo.useTableModel(leo.tableModel(_fields.getItems()));
      return vmech().getListViewAsTable(leo);
//      return new TableView(leo, leo.tableModel(_fields.getItems()));
   }
   
   public Title title() { return _name.title(); }
   public static String pluralName() { return "Reports"; }

}
