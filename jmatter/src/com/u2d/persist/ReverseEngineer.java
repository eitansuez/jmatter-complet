package com.u2d.persist;

import com.u2d.type.atom.StringEO;

import javax.persistence.Entity;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.text.SimpleDateFormat;

/**
 * @author Jim Slack
 */
public class ReverseEngineer {
  public static final int MAX_STRING_LEN = 255;
  public static final int MAX_DISPLAY_STRING_LEN = 50;
  public static final String UPDATE_SCRIPT_NAME = "reveng_update_script.sql";
  public static final String UPDATE_SCRIPT_DATE_FORMAT = "yyyy-MM-dd";
  
  // Note: suffixes "id" and "fk" (all lower case) are deliberately left off because they may be
  // legitimate word endings.
  public static final String FOREIGN_KEY_NAME_PATTERN_WITH_UNDERSCORE_CASE_INSENSITIVE
      = "(\\w+)(_id|_fk)";
  public static final String FOREIGN_KEY_NAME_PATTERN_WITHOUT_UNDERSCORE_CASE_SENSITIVE
      = "(\\w*)[a-z](ID|FK)"; // Must have at least one lower case letter just before the suffix.
  public static final String[] JMATTER_SYSTEM_TABLES = {
    "aggregatefield", "associationfield", "atomicfield", "classbar_items", 
    "command", "commandrestriction", "complextype", "compositefield", 
    "compositeindexedfield", "compositequery", "contactmethod", 
    "creationrestriction", "eocommand", "field", "fieldrestriction", 
    "folder", "folder_items", "indexedfield", "loggedevent", "member", 
    "overloadedeocmd", "programmingelement", "queryspecifications", 
    "restriction", "role", "simplereport", "userrestriction", "users", 
    "usstate"};
    
  public static final String[] MYSQL5_RESERVED_WORDS = {
      "add", "all", "alter", "analyze", "and", "as", "asc", "asensitive", 
      "asensitive", "before", "between", "bigint", "binary", "blob", "both", 
      "by", "call", "cascade", "case", "change", "char", "character", "check", 
      "collate", "column", "condition", "connection", "constraint", 
      "continue", "convert", "create", "cross", "current_date", 
      "current_timestamp", "current_user", "cursor", "database", "databases", 
      "day_hour", "day_microsecond", "day_minute", "day_second", "dec", 
      "decimal", "declare", "default", "delayed", "delete", "desc", 
      "describe", "deterministic", "distinct", "distinctrow", "div", "double", 
      "drop", "dual", "each", "else", "elseif", "enclosed", "escaped", 
      "exists", "exit", "explain", "false", "fetch", "float", "float4", 
      "float8", "for", "force", "foreign", "from", "fulltext", "goto", 
      "grant", "group", "having", "high_priority", "hour_microsecond", 
      "hour_minute", "hour_second", "if", "ignore", "in", "index", "infile", 
      "inner", "inout", "insensitive", "insert", "int", "int1", "int2", 
      "int3", "int4", "int8", "integer", "interval", "into", "is", "iterate", 
      "join", "key", "keys", "kill", "label", "leading", "leave", "left", 
      "like", "limit", "lines", "load", "localtime", "localtimestamp", "lock", 
      "long", "longblob", "longtext", "loop", "loop", "low_priority", "match", 
      "mediumblob", "mediumint", "mediumtext", "middleint", 
      "minute_microsecond", "minute_second", "mod", "modifies", "natural", 
      "not", "no_write_to_binlog", "null", "numeric", "on", "optimize", 
      "option", "optionally", "or", "order", "out", "outer", "outfile", 
      "precision", "primary", "procedure", "purge", "read", "reads", "real", 
      "references", "regexp", "release", "rename", "repeat", "replace", 
      "require", "restrict", "return", "revoke", "right", "rlike", "schema", 
      "schemas", "second_microsecond", "select", "sensitive", "separator", 
      "set", "show", "smallint", "soname", "spatial", "specific", "sql", 
      "sqlexception", "sqlstate", "sqlwarning", "sql_big_result", 
      "sql_calc_found_rows", "sql_small_result", "ssl", "starting", 
      "straight_join", "table", "terminated", "then", "tinyblob", "tinyint", 
      "tinytext", "to", "trailing", "trigger", "true", "undo", "union", 
      "unique", "unlock", "unsigned", "update", "upgrade", "usage", "use", 
      "using", "utc_date", "utc_time", "utc_timestamp", "values", "varbinary", 
      "varchar", "varcharacter", "varying", "when", "where", "while", "with", 
      "write", "xor", "year_month", "zerofill"};
    
  public static final String[] JAVA6_RESERVED_WORDS = {
      "abstract", "assert", "boolean", "break", "byte", "case", "catch", 
      "char", "class", "const", "continue", "default", "do", "double", "else", 
      "enum", "extends", "final", "finally", "float", "for", "goto", "if", 
      "implements", "import", "instanceof", "int", "interface", "long", 
      "native", "new", "package", "private", "protected", "public", "return", 
      "short", "static", "strictfp", "super", "switch", "synchronized", 
      "this", "throw", "throws", "transient", "try", "void", "volatile", 
      "while"};

  private static Map<Integer, String> sqlToJavaMap = new HashMap<Integer, String>();
  // JMatter types:
  // BooleanEO, CharEO, ChoiceEO, ColorEO, DateEO, DateTime, DateWithAge,
  // Email, FileEO, FileWEO, FloatEO, ImgEO, IntEO, Logo, LongEO,
  // Password, Percent, Photo, SSN, StringEO, TextEO, TimeEO,
  // TimeInterval, TimeSpan, URI, USDollar, USPhone, USZipCode.
  static {
    sqlToJavaMap.put(Types.BIT, "BooleanEO");
    sqlToJavaMap.put(Types.TINYINT, "IntEO");
    sqlToJavaMap.put(Types.SMALLINT, "IntEO");
    sqlToJavaMap.put(Types.INTEGER, "IntEO");
    sqlToJavaMap.put(Types.BIGINT, "LongEO");
    sqlToJavaMap.put(Types.FLOAT, "FloatEO");
    sqlToJavaMap.put(Types.REAL, "FloatEO");
    sqlToJavaMap.put(Types.DOUBLE, "FloatEO");
    sqlToJavaMap.put(Types.NUMERIC, "USDollar");
    sqlToJavaMap.put(Types.DECIMAL, "USDollar");
    sqlToJavaMap.put(Types.CHAR, "StringEO");
    sqlToJavaMap.put(Types.VARCHAR, "StringEO");
    sqlToJavaMap.put(Types.LONGVARCHAR, "StringEO");
    sqlToJavaMap.put(Types.DATE, "DateEO");
    sqlToJavaMap.put(Types.TIME, "TimeEO");
    sqlToJavaMap.put(Types.TIMESTAMP, "DateEO");
    sqlToJavaMap.put(Types.BINARY, "ImageEO");
    sqlToJavaMap.put(Types.VARBINARY, "ImageEO");
    sqlToJavaMap.put(Types.LONGVARBINARY, "ImageEO");
    sqlToJavaMap.put(Types.NULL, "");
    sqlToJavaMap.put(Types.OTHER, "ImageEO");
    sqlToJavaMap.put(Types.JAVA_OBJECT, "");
    sqlToJavaMap.put(Types.DISTINCT, "");
    sqlToJavaMap.put(Types.STRUCT, "");
    sqlToJavaMap.put(Types.ARRAY, "");
    sqlToJavaMap.put(Types.BLOB, "ImageEO");
    sqlToJavaMap.put(Types.CLOB, "TextEO");
    sqlToJavaMap.put(Types.REF, "");
    sqlToJavaMap.put(Types.DATALINK, "");
    sqlToJavaMap.put(Types.BOOLEAN, "BooleanEO");
    // the Types fields below i believe were introduced in jdbc4 which is supported by java6:
//    sqlToJavaMap.put(Types.ROWID, "");
//    sqlToJavaMap.put(Types.NCHAR, "StringEO");
//    sqlToJavaMap.put(Types.NVARCHAR, "StringEO");
//    sqlToJavaMap.put(Types.LONGNVARCHAR, "StringEO");
//    sqlToJavaMap.put(Types.NCLOB, "TextEO");
//    sqlToJavaMap.put(Types.SQLXML, "");
  }

  private Properties dbProperties;
  private Database database;
  private Pattern foreignKeyNamePatternWithUnderscore;
  private Pattern foreignKeyNamePatternWithoutUnderscore;
  
  public ReverseEngineer(String packageName, String hibernatePropertiesFileName) 
      throws IOException, ClassNotFoundException, SQLException, 
      ReverseEngineerException {
    foreignKeyNamePatternWithUnderscore 
        = Pattern.compile(FOREIGN_KEY_NAME_PATTERN_WITH_UNDERSCORE_CASE_INSENSITIVE, 
        Pattern.CASE_INSENSITIVE);
    foreignKeyNamePatternWithoutUnderscore 
        = Pattern.compile(FOREIGN_KEY_NAME_PATTERN_WITHOUT_UNDERSCORE_CASE_SENSITIVE);
    loadDbProperties(hibernatePropertiesFileName);
    Class.forName(dbProperties.getProperty("hibernate.connection.driver_class"));
    
    loadMetaData();
    for (Table table : database.tables) {
      String pojo = makePojo(packageName, table);
      String packageFolders = String.format("src%s%s", File.separator,
          packageName.replace(".", File.separator));
      String fileName = String.format("%s%s%s.java", packageFolders, File.separator,
          table.className);
      (new File(fileName)).delete();    
      (new File(packageFolders)).mkdirs();
      BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
      file.write(pojo);
      file.close();
    }
    setRequiredFields();
    makeUpdateScript();
  }
  
   /**
      * From AndroMDA project (BSD License).
      * Linguistically pluralizes a singular noun. <p/>
      * <ul>
      * <li><code>noun</code> becomes <code>nouns</code></li>
      * <li><code>key</code> becomes <code>keys</code></li>
      * <li><code>word</code> becomes <code>words</code></li>
      * <li><code>property</code> becomes <code>properties</code></li>
      * <li><code>bus</code> becomes <code>busses</code></li>
      * <li><code>boss</code> becomes <code>bosses</code></li>
      * </ul>
      * <p/> Whitespace as well as <code>null</code> arguments will return an
      * empty String.
      * </p>
      *
      * @param singularNoun A singular noun to pluralize
      * @return The plural of the argument singularNoun
      */
     public static String pluralize(final String singularNoun)
     {
         String pluralNoun = singularNoun.trim();
         //String pluralNoun = trimToEmpty(singularNoun);
 
         int nounLength = pluralNoun.length();
 
         if (nounLength == 1)
         {
             pluralNoun = pluralNoun + 's';
         }
         else if (nounLength > 1)
         {
             char secondToLastChar = pluralNoun.charAt(nounLength - 2);
 
             if (pluralNoun.endsWith("y"))
             {
                 switch (secondToLastChar)
                 {
                     case 'a': // fall-through
                     case 'e': // fall-through
                     case 'i': // fall-through
                     case 'o': // fall-through
                     case 'u':
                         pluralNoun = pluralNoun + 's';
                         break;
                     default:
                         pluralNoun = pluralNoun.substring(0, nounLength - 1) + "ies";
                 }
             }
             else if (pluralNoun.endsWith("s"))
             {
                 switch (secondToLastChar)
                 {
                     case 's':
                         pluralNoun = pluralNoun + "es";
                         break;
                     default:
                         pluralNoun = pluralNoun + "ses";
                 }
             }
             else
             {
                 pluralNoun = pluralNoun + 's';
             }
         }
         return pluralNoun;
     }  
  
  /**
   * Capitalize the first letter of the word, leaving the rest of the letters alone.
   */
  public String capitalize(String word) {
    return Character.toUpperCase(word.charAt(0)) + word.substring(1);
  }

  /**
   * Uncapitalize the first letter of the word, leaving the rest of the letters alone.
   */
  public String uncapitalize(String word) {
    return Character.toLowerCase(word.charAt(0)) + word.substring(1);
  }
  
  public void loadDbProperties(String hibernatePropertiesFileName) 
      throws IOException {
    dbProperties = new Properties();
    dbProperties.load(new FileInputStream(hibernatePropertiesFileName));
  }

  public void loadMetaData() 
      throws SQLException, ReverseEngineerException {
    Connection connection = DriverManager.getConnection(
        dbProperties.getProperty("hibernate.connection.url"), 
        dbProperties.getProperty("hibernate.connection.username"),
        dbProperties.getProperty("hibernate.connection.password"));
    DatabaseMetaData metadata = connection.getMetaData();
    database = new Database();
    
    ResultSet resultSet = metadata.getTables(null, null, "%", new String[] {"TABLE"});
    while (resultSet.next()) {
      String tableName = resultSet.getString("TABLE_NAME");
      if (Arrays.binarySearch(JMATTER_SYSTEM_TABLES, tableName.toLowerCase()) < 0) {
        System.out.printf("Processing table %s ...\n", tableName);
        Table table = new Table(tableName, resultSet.getString("REMARKS"));
        database.tables.add(table);
        ResultSet resultSetColumns = metadata.getColumns(null, null, tableName, "%");
        while (resultSetColumns.next()) {
          Column column = new Column(table, resultSetColumns.getString("COLUMN_NAME"),
              resultSetColumns.getString("REMARKS"), resultSetColumns.getString("TYPE_NAME"),
              resultSetColumns.getInt("DATA_TYPE"), resultSetColumns.getInt("COLUMN_SIZE"),
              resultSetColumns.getInt("NULLABLE"));
          // Do some validity checking.
          if ((column.name.equalsIgnoreCase("id") || column.name.equalsIgnoreCase("version"))
              && !column.javaType.equals("IntEO") && !column.javaType.equals("LongEO")) {
              throw new ReverseEngineerException("Table '%s' column '%s' must be integer",
                  table.name, column.name);
          }
          if (column.name.equalsIgnoreCase("createdon")
              && !column.javaType.equals("DateEO") 
              && !column.javaType.equals("DateTime")) {
              throw new ReverseEngineerException("Table '%s' column '%s' must be date",
                  table.name, column.name);
          }
          table.columns.add(column);
        }
       // System.out.printf("Before getindexinfo1 for table %s ...\n", tableName);
        ResultSet resultSetIndexes = metadata.getIndexInfo(null, null, tableName, true, false);
        while (resultSetIndexes.next()) {
          //16 Jan 09 -Added since COLUMN_NAME will be null for tableIndexStatistic
          if(resultSetIndexes.getShort("TYPE")!= metadata.tableIndexStatistic)
          {
            Column column = table.findColumn(resultSetIndexes.getString("COLUMN_NAME"));
            column.isUnique = true;
          }
        }
        //System.out.printf("Before getindexinfo2 for table %s ...\n", tableName);
        // Do the same thing for approximate == true.
        resultSetIndexes = metadata.getIndexInfo(null, null, tableName, true, true);
        while (resultSetIndexes.next()) {
          //16 Jan 09 -Added since COLUMN_NAME will be null for tableIndexStatistic
          if(resultSetIndexes.getShort("TYPE")!= metadata.tableIndexStatistic)
          {
            Column column = table.findColumn(resultSetIndexes.getString("COLUMN_NAME"));
            column.isUnique = true;
          }
        }
        ResultSet resultSetKeys = metadata.getPrimaryKeys(null, null, tableName);
        while (resultSetKeys.next()) {
          if (resultSetKeys.getShort("KEY_SEQ") > 1) {
            throw new ReverseEngineerException("Table '%s' has compound primary key", tableName);
          }
          table.primaryKeyColumn = table.findColumn(resultSetKeys.getString("COLUMN_NAME"));
          if (table.primaryKeyColumn == null) {
            throw new ReverseEngineerException("Table '%s' is missing a primary key column", 
                tableName, table.primaryKeyColumn.name);
          } else if (!table.primaryKeyColumn.name.equalsIgnoreCase("id")){
            throw new ReverseEngineerException("Table '%s' primary key column '%s' should be named 'ID'", 
                tableName, table.primaryKeyColumn.name);
          } else {
            if (table.primaryKeyColumn.typeCode != Types.BIGINT
                && table.primaryKeyColumn.typeCode != Types.INTEGER) {
              throw new ReverseEngineerException("Table '%s' primary key '%s' is not BIGINT or INTEGER", 
                  tableName, table.primaryKeyColumn.name);
            }
            table.primaryKeyColumn.isPrimaryKey = true;
          }
        }
      }
    }
    System.out.println("Processing foreign keys ...");
    for (Table table : database.tables) {
      ResultSet resultSetForeignKeys = metadata.getImportedKeys(null, null,
          table.name);
      while (resultSetForeignKeys.next()) {
        Table pkTable = database.findTable(resultSetForeignKeys.getString("PKTABLE_NAME"));
        Column fkColumn = table.findColumn(resultSetForeignKeys.getString("FKCOLUMN_NAME"));
        ForeignKey foreignKey = new ForeignKey(table, pkTable, fkColumn,
            resultSetForeignKeys.getShort("DELETE_RULE"),
            resultSetForeignKeys.getShort("UPDATE_RULE"));
        table.foreignKeysFromHere.add(foreignKey);
        pkTable.foreignKeysToHere.add(foreignKey);
        fkColumn.foreignKey = foreignKey;
        fkColumn.javaType = foreignKey.pkTable.className;
        Matcher matcher = foreignKeyNamePatternWithUnderscore.matcher(fkColumn.attributeName);
        if (matcher.find()) {
          fkColumn.attributeName = matcher.group(1);
        } else {
          matcher = foreignKeyNamePatternWithoutUnderscore.matcher(fkColumn.attributeName);
          if (matcher.find()) {
            fkColumn.attributeName = matcher.group(1);
          }
        }
        // if (fkColumn.attributeName.toLowerCase().endsWith("id")) {
          // fkColumn.attributeName = fkColumn.attributeName.substring(0, fkColumn.attributeName.length() - 2);
        // } else if (fkColumn.attributeName.toLowerCase().endsWith("_id")) {
          // fkColumn.attributeName = fkColumn.attributeName.substring(0, fkColumn.attributeName.length() - 2);
        // }
      }
      // Make sure there are no duplicate FKs. (This sometimes happens in MySQL after
      // schema-update is run.)  If so, keep the FK that the column references (the last
      // last FK set), and delete any others. The database schema is not changed.
      List<ForeignKey> foreignKeysToRemove = new ArrayList<ForeignKey>();
      for (ForeignKey foreignKey : table.foreignKeysFromHere) {
        if (foreignKey.fkColumn.foreignKey != foreignKey) {
          foreignKeysToRemove.add(foreignKey);
          System.err.printf("WARNING: Duplicate foreign key constraints for column '%s' in table '%s'\n",
              foreignKey.fkColumn.name, foreignKey.fkTable.name);
        }
      }
      for (ForeignKey foreignKey : foreignKeysToRemove) {
        foreignKey.pkTable.foreignKeysToHere.remove(foreignKey);
        table.foreignKeysFromHere.remove(foreignKey);
      }
    }
    System.out.println("Creating inverse relationships...");
    for (Table table : database.tables) {
      // Set name for "many" side of relationships.  (Targets of FKs.)
      Map<String, List<ForeignKey>> collectionNames = new HashMap<String, List<ForeignKey>>();      
      for (ForeignKey foreignKey : table.foreignKeysToHere) {
        foreignKey.collectionName = pluralize(uncapitalize(foreignKey.fkTable.className));
        List<ForeignKey> fkList = collectionNames.get(foreignKey.collectionName);
        if (fkList == null) {
          fkList = new ArrayList<ForeignKey>();
          collectionNames.put(foreignKey.collectionName, fkList); 
        }
        if (foreignKey.collectionName == null) {
          throw new ReverseEngineerException("null FK for table %s, fk %s (1)",
            table.name, foreignKey.fkColumn.name);
        }
        fkList.add(foreignKey);
      }
      for (String collectionName : collectionNames.keySet()) {
        List<ForeignKey> fkList = collectionNames.get(collectionName);
        if (fkList.size() > 1) {
          // More than one collection with the same name in this class.  Rename each duplicate with
          // the name of the class followed by the name of the FK attribute.
          for (ForeignKey foreignKey : fkList) {
            foreignKey.collectionName = pluralize(String.format("%s%s",
                uncapitalize(foreignKey.fkTable.className), 
                capitalize(foreignKey.fkColumn.attributeName)));
            if (foreignKey.collectionName == null) {
              throw new ReverseEngineerException("null FK for table %s, fk %s (2)",
                table.name, foreignKey.fkColumn.name);
            }
          }
        }
      }
      // Get the "title" column for the table.  
      // 1. Look for first String column that is unique.
      for (Column column : table.columns) {
        if (column.userDefined() && column.foreignKey == null 
            && column.javaType.equals("StringEO") && column.isUnique) {
          table.titleColumn = column;
          break;
        }
      }
      if (table.titleColumn == null) {
        // 2. Look for first unique non-FK column.
        for (Column column : table.columns) {
          if (column.userDefined() && column.foreignKey == null && column.isUnique) {
            table.titleColumn = column;
            break;
          }
        }
      }
      if (table.titleColumn == null) {
        // 3. Look for first String column.
        for (Column column : table.columns) {
          if (column.userDefined() && column.javaType.equals("StringEO")) {
            table.titleColumn = column;
            break;
          }
        }
      }
      if (table.titleColumn == null) {
        // 4. Look for first user column.
        for (Column column : table.columns) {
          if (column.userDefined()) {
            table.titleColumn = column;
            break;
          }
        }
      }
      if (table.titleColumn == null) {
        // 5. Should never happen!! But just in case... take the first column.
        table.titleColumn = table.columns.get(0);
      }
    }
  }
    
  public String makePojo(String packageName, Table table) {
    Output out = new Output();
    out.write("package %s;", packageName);
    out.write("");
    out.write("import com.u2d.model.*;");
    out.write("import com.u2d.app.PersistenceMechanism;");
    out.write("import com.u2d.type.*;");
    out.write("import com.u2d.type.atom.*;");
    out.write("import com.u2d.type.composite.*;");
    out.write("import com.u2d.list.RelationalList;");
    out.write("import com.u2d.reflection.Fld;");
    out.write("import javax.persistence.Entity;");
    out.write("");
    out.write("/**");
    out.write(" * %s.java", table.className);
    out.write(" *");
    out.write(" * @author (Autogenerated by ReverseEngineer)");
    out.write(" * @created %s", new java.util.Date());
    out.write(" */");
    out.write("@Entity");
    out.write("public class %s extends AbstractComplexEObject {", table.className);
    out.write("");
    for (Column column : table.columns) {
      if (column.userDefined()) {
        String remarks = column.remarks;
        if (StringEO.isEmpty(remarks)) {
          remarks = String.format("%s attribute of this %s object",
              column.attributeName, table.className);
        }
        out.write("  ////////////////////////////////////////////////////////");
        out.write("  //  The \"%s\" attribute.", column.attributeName);
        out.write("  ////////////////////////////////////////////////////////");
        out.write("");
        if (column.foreignKey == null) {
          out.write("  /**");
          out.write("   * %s.", remarks);
          out.write("   */");
          out.write("  private final %s %s = new %s();", column.javaType,
              column.attributeName, column.javaType);
          out.write("");
        } else {
          out.write("  /**");
          out.write("   * %s.", remarks);
          out.write("   */");
          out.write("  private %s %s;", column.javaType, column.attributeName);
          out.write("");
          out.write("  /**");
          out.write("   * The attribute in the %s class that is the other", column.foreignKey.pkTable.className);
          out.write("   * side of the %s relationship.", column.attributeName);
          out.write("   */");
          // TODO: need to create the inverse field in the other table!! (It's not in db!)
          out.write("  public static final String %sInverseFieldName = \"%s\";",
              column.attributeName, column.foreignKey.collectionName);
          out.write("");
          out.write("  /**");
          out.write("   * Sets the %s attribute of the %s object. (Setter methods", 
              column.attributeName, table.className);
          out.write("   * used *only* for relationships to other classes.)");
          out.write("   *");
          out.write("   * @param %s The new %s value.", column.attributeName, 
              column.attributeName);
          out.write("   */");
          out.write("  public void set%s(%s %s) {",
              capitalize(column.attributeName), column.foreignKey.pkTable.className,
              column.attributeName);
          out.write("    %s old = this.%s;", column.foreignKey.pkTable.className,
              column.attributeName);
          out.write("    this.%s = %s;", column.attributeName, column.attributeName);
          out.write("    firePropertyChange(\"%s\", old, this.%s);",
              column.attributeName, column.attributeName);
          out.write("  }");
          out.write("");
        }
        out.write("  /**");
        out.write("   * Gets %s.", remarks);
        out.write("   *");
        out.write("   * @return %s.", remarks);
        out.write("   */");
        String comma = "";
        StringBuilder fieldSpecs = new StringBuilder();
        if (column.javaType.equals("StringEO")) {
          fieldSpecs.append(String.format("colsize=%d, displaysize=%d", 
              column.length, Math.min(column.length, MAX_DISPLAY_STRING_LEN)));
          comma = ", ";
        }
        if (!StringEO.isEmpty(column.remarks)) {
          fieldSpecs.append(String.format("%sdescription=\"%s\"", comma, column.remarks));
          comma = ", ";
        }
        if (fieldSpecs.length() > 0) {
          out.write("  @Fld(%s)", fieldSpecs.toString());
        }
        out.write("  public %s get%s() {", column.javaType, capitalize(column.attributeName));
        out.write("    return this.%s;", column.attributeName);
        out.write("  }");
        out.write("");
      }
    }
    for (ForeignKey foreignKey : table.foreignKeysToHere) {
      out.write("  ////////////////////////////////////////////////////////");
      out.write("  //  The \"%s\" attribute, which is the \"many\" side of the",
          foreignKey.collectionName);
      out.write("  //  %s relationship with the %s class.",
          foreignKey.collectionName, foreignKey.fkTable.className);
      out.write("  ////////////////////////////////////////////////////////");
      out.write("");
      out.write("  private final RelationalList %s = new RelationalList(%s.class);",
          foreignKey.collectionName, foreignKey.fkTable.className);
      out.write("  public static final Class %sType = %s.class;",
          foreignKey.collectionName, foreignKey.fkTable.className);
      out.write("  public static String %sInverseFieldName = \"%s\";",
          foreignKey.collectionName, foreignKey.fkColumn.attributeName);
      out.write("  public static final int %sRelationType = PersistenceMechanism.ONE_TO_MANY;",
          foreignKey.collectionName);
      out.write("  public static final boolean %sRelationIsInverse = true;",
          foreignKey.collectionName);
      out.write("  /**");
      out.write("   * Gets the %s collection.", foreignKey.collectionName);
      out.write("   *");
      out.write("   * @return The %s collection.", foreignKey.collectionName);
      out.write("   */");
      out.write("  public RelationalList get%s() {", capitalize(foreignKey.collectionName));
      out.write("    return this.%s;", foreignKey.collectionName);
      out.write("  }");
      out.write("");
    }
    out.write("  ////////////////////////////////////////////////////////");
    out.write("  //  Other methods");
    out.write("  ////////////////////////////////////////////////////////");
    out.write("");
    out.write("  /**");
    out.write("   * Creates a new instance of %s.", table.className);
    out.write("   */");
    out.write("  public %s() {", table.className);
    out.write("  }");
    out.write("");
    out.write("  /**");
    out.write("   * Returns the plural of the name of this class.");
    out.write("   *");
    out.write("   * @return Plural of \"%s\"", table.className);
    out.write("   */");
    out.write("  public static String pluralName() {");
    out.write("    return \"%s\";", table.classNamePlural);
    out.write("  }");
    out.write("");
    out.write("  /**");
    out.write("   * Returns the \"title\" for this object, which is how the object");
    out.write("   * will appear in lists.");
    out.write("   *");
    out.write("   * @return Title string for this object.");
    out.write("   */");
    out.write("  public Title title() {");
    out.write("    Title title = this.%s.title();", table.titleColumn.attributeName);
    out.write("    // Append other fields here with title.append(), title.appendParens(), etc.");
    out.write("    return title;");
    out.write("  }");
    out.write("");
    out.write("  /**");
    out.write("   *  Specify the default search field for this class");
    out.write("   */");
    out.write("  public static String defaultSearchPath = \"%s\";", table.titleColumn.attributeName);
    out.write("");
    out.write("  /**");
    out.write("   * Order the fields should appear in the user interface.");
    out.write("   */");
    StringBuilder fieldOrder = new StringBuilder();
    String comma = "";
    for (Column column : table.columns) {
      if (column.userDefined()) {
        fieldOrder.append(String.format("%s\"%s\"", comma, column.attributeName));
        comma = ", ";
      }
    }
    out.write("  public static String[] fieldOrder = {%s};", fieldOrder);
    out.write("");
    out.write("  /**");
    out.write("   * List of fields that should have unique values.");
    out.write("   */");
    StringBuilder identities = new StringBuilder();
    comma = "";
    for (Column column : table.columns) {
      if (column.userDefined() && column.isUnique) {
        identities.append(String.format("%s\"%s\"", comma, column.attributeName));
        comma = ", ";
      }
    }
    out.write("  public static String[] identities = {%s};", identities);
    out.write("");
    out.write("  /**");
    out.write("   * Columns from other classes that should be part of this class's table.");
    out.write("   */");
    out.write("//    public static String[] flattenIntoParent = {\"fieldname1\", \"fieldname2\"};");
    out.write("");
    out.write("  /*");
    out.write("   *  The color used to represent this class.");
    out.write("   */");
    out.write("//    public static Color colorCode = new Color(0x2332);");
    out.write("");
    out.write("  /*");
    out.write("   *  Use a different Icon for each instance. Change photoFieldName.");
    out.write("   */");
    out.write("//    private transient PhotoIconAssistant assistant =");
    out.write("//                        new PhotoIconAssistant(this, photoFieldname);");
    out.write("//    public Icon iconLg() { return assistant.iconLg(); }");
    out.write("//    public Icon iconSm() { return assistant.iconSm(); }");
    out.write("");
    out.write("  /**");
    out.write("   *  Specify a custom view for this class.");
    out.write("   */");
    out.write("  //    public EView getMainView()");
    out.write("  //    {");
    out.write("  //       return new CustomEmployeeView(this);");
    out.write("  //    }");
    out.write("}");
    return out.toString();
  }
    
  public void setRequiredFields() throws IOException {
    System.out.println("Setting required fields ...");
    final String MODEL_METADATA_FILE_NAME = "resources/model-metadata.properties";
    Properties modelMetadata = new Properties();
    modelMetadata.load(new FileInputStream(MODEL_METADATA_FILE_NAME));
    for (Table table : database.tables) {
      for (Column column : table.columns) {
        if (column.userDefined()) {
          String propertyName = String.format("%s.%s.required", table.className, 
              column.attributeName);
          if (column.isRequired) {
            modelMetadata.setProperty(propertyName, "true");
          } else {
            modelMetadata.remove(propertyName);
          }
        }
      }
    }
    modelMetadata.store(new FileOutputStream(MODEL_METADATA_FILE_NAME), "Updated by ReverseEngineer");
  }
    
  public void makeUpdateScript() 
      throws IOException {
    System.out.println("Making update script ...");
    BufferedWriter scriptFile = new BufferedWriter(new FileWriter(UPDATE_SCRIPT_NAME));
    SimpleDateFormat formatter = new SimpleDateFormat(UPDATE_SCRIPT_DATE_FORMAT);
    String todayString = formatter.format(new java.util.Date());
    for (Table table : database.tables) {
      scriptFile.write(String.format("update %s set version=0 where version is null;\n",
          table.name));
      scriptFile.write(String.format("update %s set createdOn='%s' where createdOn is null;\n",
          table.name, todayString));
    }
    scriptFile.close();
  }
    
  public boolean isAllUpperCase(String word) {
    for (int i = 0; i < word.length(); i++) {
      if (Character.isLowerCase(word.charAt(i))) {
        return false;
      }
    }
    return true;
  }
  
  public String sqlIdentifierToJavaIdentifier(String sqlIdentifier, boolean capitalizeFirstWord) {
    String javaIdentifier = sqlIdentifier;
    if (isAllUpperCase(javaIdentifier)) {
      javaIdentifier = javaIdentifier.toLowerCase();
    }
    // Make each character following an underscore uppercase.
    StringBuilder builder = new StringBuilder();
    String[] words = javaIdentifier.split("_");
    if (words.length > 1) {
      boolean firstWord = true;
      for (String word : words) {
        if (StringEO.isEmpty(word)) {
          continue;
        }
        if (!firstWord) {
          word = capitalize(word);
          builder.append("_");
        }
        builder.append(word);
        firstWord = false;
      }
      javaIdentifier = builder.toString();
    }
    if (capitalizeFirstWord) {
      javaIdentifier = capitalize(javaIdentifier);
    }
    // Make sure the word doesn't conflict with Java or MySQL.
    if (Arrays.binarySearch(MYSQL5_RESERVED_WORDS, javaIdentifier) >= 0
        || Arrays.binarySearch(JAVA6_RESERVED_WORDS, javaIdentifier) >= 0) {
      javaIdentifier = javaIdentifier + "_1";
    }
    return javaIdentifier;
  }
    
  /**
   * Main method for this class.
   *
   * @param args the command line arguments
   *
   * @throws ClassNotFoundException If the driver is not found.
   * @throws IOException If there is a problem getting the current folder.
   * @throws SQLException If there is a problem with the SQL statement.
   */
  public static void main(String[] args)
      throws ClassNotFoundException, IOException, SQLException, ReverseEngineerException {
    if (args.length != 2)
    {
       throw new IllegalArgumentException("Usage: java ReverseEngineer package-name hibernate-properties-file");
    }
     new ReverseEngineer(args[0], args[1]);
  }

  private class Database {
    public List<Table> tables = new ArrayList<Table>();
    public Database() {
    }
    public String toString() {
      return String.format("Database(tables: %s)", tables);
    }
    public Table findTable(String name) {
      for (Table table : tables) {
        if (table.name.equalsIgnoreCase(name)) {
          return table;
        }
      }
      return null;
    }
  }
  
  private class Table {
    public String name;
    public String className;
    public String classNamePlural;
    public String remarks;
    public List<Column> columns = new ArrayList<Column>();
    public Column primaryKeyColumn;
    public Column titleColumn;
    public List<ForeignKey> foreignKeysFromHere = new ArrayList<ForeignKey>();
    public List<ForeignKey> foreignKeysToHere = new ArrayList<ForeignKey>();
    public Table(String name, String remarks) {
      this.name = name;
      this.className = sqlIdentifierToJavaIdentifier(name, true);
      String pluralName = pluralize(this.className);
      String[] words = pluralName.split("\\s|_");
      StringBuilder builder = new StringBuilder();
      String space = "";
      for (String word : words) {
        builder.append(space);
        space = " ";
        builder.append(capitalize(word));
      }
      this.classNamePlural = builder.toString();
      if (StringEO.isEmpty(remarks)) {
        this.remarks = String.format("%s.java", this.className);
      } else {
        this.remarks = remarks;
      }
      this.primaryKeyColumn = null;
    }
    public String toString() {
      return String.format("Table(name:%s, pk: %s, columns:%s)", name, 
          primaryKeyColumn, columns);
    }
    public Column findColumn(String name) {
      for (Column column : columns) {
        if (column.name.equalsIgnoreCase(name)) {
          return column;
        }
      }
      return null;
    }
  }

  private class Column {
    public Table table;
    public String name;
    public String attributeName;
    public String remarks;
    public String typeName;
    public String javaType;
    public int length;
    public int typeCode;
    public boolean isPrimaryKey;
    public ForeignKey foreignKey;
    public boolean isUnique;
    public boolean isRequired;
    public Column(Table table, String name, String remarks, String typeName, 
        int typeCode, int length, int nullable) 
        throws ReverseEngineerException {
      this.table = table;
      this.name = name;
      this.attributeName = sqlIdentifierToJavaIdentifier(name, false);
      this.remarks = remarks;
      this.typeName = typeName;
      this.typeCode = typeCode;
      this.length = length;
      this.isPrimaryKey = false;
      this.foreignKey = null;
      this.isUnique = false;
      this.isRequired = nullable == 0;
      javaType = sqlToJavaMap.get(typeCode);
      if (StringEO.isEmpty(javaType)) {
        throw new ReverseEngineerException("Table %s column %s: unimplemented type mapping for %s",
          table.name, name, typeName);
      } else if (javaType.equals("StringEO") && length > MAX_STRING_LEN) {
        javaType = "TextEO";
      }
    }
    public boolean userDefined() {
      return !attributeName.equalsIgnoreCase("id") 
          && !attributeName.equalsIgnoreCase("version")
          && !attributeName.equalsIgnoreCase("createdOn");
    }
    public String toString() {
      return String.format("Column(name:%s)", name);
    }
  }

  private class ForeignKey {
    public Table fkTable, pkTable;
    public Column fkColumn;
    public String collectionName;
    public short deleteRule, updateRule;
    public ForeignKey(Table fkTable, Table pkTable, Column fkColumn,
        short deleteRule, short updateRule) {
      this.fkTable = fkTable;
      this.pkTable = pkTable;
      this.fkColumn = fkColumn;
      this.deleteRule = deleteRule;
      this.updateRule = updateRule;
    }
  }
  
  private class ReverseEngineerException extends Exception {
    public ReverseEngineerException(String formatString, Object... params) {
      super(String.format(formatString, params));
    }
  }
  
  private class Output {
    private List<String> list;
    public void write(String formatString, Object... params) {
      list.add(String.format(formatString, params));
    }
    public Output() {
      list = new ArrayList<String>();
    }
    public String toString() {
      StringBuilder builder = new StringBuilder();
      for (String line : list) {
        builder.append(line);
        builder.append("\n");
      }
      return builder.toString();
    }
  }
  
  
}
