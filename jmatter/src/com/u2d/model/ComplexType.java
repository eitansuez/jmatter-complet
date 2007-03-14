/*
 * Created on Jan 19, 2004
 */
package com.u2d.model;

import java.util.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.swing.Icon;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.beans.IntrospectionException;
import com.u2d.app.*;
import com.u2d.element.*;
import com.u2d.field.*;
import com.u2d.find.CompositeQuery;
import com.u2d.find.QueryCommandAdapter;
import com.u2d.list.*;
import com.u2d.pattern.*;
import com.u2d.persist.*;
import com.u2d.view.View;
import com.u2d.reflection.*;
import com.u2d.type.Choice;
import com.u2d.type.atom.*;
import com.u2d.pubsub.AppEventType;

/**
 * @author Eitan Suez
 */
public class ComplexType extends AbstractComplexEObject
                         implements FieldParent, Localized
{
   private static PropertyResourceBundle localeBundle = null;
   static
   {
      try
      {
         localeBundle = (PropertyResourceBundle)
               ResourceBundle.getBundle("locale-metadata", Locale.getDefault());
      }
      catch (MissingResourceException ex) {}
   }
   
   private static Properties metadata = null;
   static
   {
      ClassLoader loader = ComplexType.class.getClassLoader();
      InputStream stream = loader.getResourceAsStream("model-metadata.properties");
      if (stream == null)
      {
         Tracing.tracer().info("No model-metadata properties file..");
      }
      else
      {
         metadata = new Properties();
         try
         {
            metadata.load(stream);
         }
         catch (IOException ex)
         {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
         }
      }
   }
   
   
   private static transient Map<Class, ComplexType> _typeCache = new HashMap<Class, ComplexType>();
   public static String[] commandOrder = {"Browse", "New", "Find"};

   private static Color DEFAULT_COLOR = new Color(215, 211, 140);

   public static ComplexType forClass(Class targetClass)
   {
      if (!(ComplexEObject.class.isAssignableFrom(targetClass)))
      {
         throw new RuntimeException("Cannot create Type for "+targetClass.getName());
      }

      if (_typeCache.get(targetClass) == null)
         _typeCache.put(targetClass, new ComplexType(targetClass));

      return (ComplexType) _typeCache.get(targetClass);
   }


   public static ComplexType forObject(ComplexEObject targetObject)
   {
      return forClass(targetObject.getClass());
   }

   /* **** */

   private String _className, _shortName, _naturalName, _pluralName;
   private Class _clazz;
   private transient Map<Class, Onion> _commands = new HashMap<Class, Onion>();
   private transient Onion _typeCommands = new Onion();
   private List _fields = new ArrayList();
   private Map _fieldsMap = new HashMap();
   private final ColorEO _colorCode = new ColorEO();
   private String _sortBy;

   private static Map CONCRETE_TYPE_MAP = new HashMap();
   private static Map ABSTRACT_TYPE_MAP = new HashMap();


   private ComplexType(Class instanceClass)
   {
      _clazz = instanceClass;
      deriveNames();

      updateConcreteTypeMap();
      updateAbstractTypeMap();

      _iconLg = IconLoader.typeIcon(this, "32");
      _iconSm = IconLoader.typeIcon(this, "16");
      _iconsLg = IconLoader.pluralIcon(this, "32");
      _iconsSm = IconLoader.pluralIcon(this, "16");

      harvest();

      setState(_readState, true);

      _fieldsMap = AggregateField.makeFieldMap(_fields);
      populateCommandsList();
      _fieldsList.setItems(_fields);
      
      localize();
   }

   private void harvest()
   {
      Color code = (Color) Harvester.introspectField(_clazz, "colorCode", DEFAULT_COLOR);
      _colorCode.setValue(code);
      _sortBy = (String) Harvester.introspectField(_clazz, "sortBy");
      
      
      String dateFormat = metadata.getProperty("DateEO.format");
      if (dateFormat != null)
      {
         DateEO.setStandardDateFormat(dateFormat);
      }
      String timeFormat = metadata.getProperty("TimeEO.format");
      if (timeFormat != null)
      {
         TimeEO.setStandardTimeFormat(timeFormat);
      }
      
      String searchPath = (String) Harvester.introspectField(_clazz, "defaultSearchPath");
      if (searchPath != null)
      {
         String fieldPath = getQualifiedName() + "#" + searchPath;
         _defaultSearchPath.setValue(fieldPath);
      }

      try
      {
         _commands = Harvester.harvestCommands(_clazz, this);
         _typeCommands = harvestTypeCommands();
         if (_clazz == ComplexType.class)  // no support for dynamic type creation!  we're in javaland afterall..
         {
            Command newCmd = command("New");
            _typeCommands.remove(newCmd);
         }
         _fields = Harvester.harvestFields(this);

         loadFieldMetaData();
      }
      catch (IntrospectionException ex)
      {
         System.err.println(ex);
         ex.printStackTrace();
         System.exit(1);
      }
   }
   
   private void updateConcreteTypeMap()
   {
      if (CONCRETE_TYPE_MAP.get(_clazz) == null)
      {
         CONCRETE_TYPE_MAP.put(_clazz, new PlainListEObject(ComplexType.class));
      }
      
      Class[] interfaces = _clazz.getInterfaces();
      for (int i=0; i<interfaces.length; i++)
      {
         if (ComplexEObject.class.isAssignableFrom(interfaces[i]))
         {
            concreteTypes(interfaces[i]).add(this);
         }
      }
      Class superclass = _clazz.getSuperclass();
      if (superclass!=null)
      {
         if (ComplexEObject.class.isAssignableFrom(superclass))
         {
            concreteTypes(superclass).add(this);
         }
      }
      
      if (!isAbstract(_clazz))
      {
         concreteTypes(_clazz).add(this);
      }
      
      // special:
      concreteTypes(ComplexEObject.class).add(this);  // by default all concrete types are 
   }
   private AbstractListEO concreteTypes(Class cls)
   {
      if (CONCRETE_TYPE_MAP.get(cls) == null)
         CONCRETE_TYPE_MAP.put(cls, new PlainListEObject(ComplexType.class));
      return (AbstractListEO) CONCRETE_TYPE_MAP.get(cls);
   }
   private void updateAbstractTypeMap()
   {
      if (ABSTRACT_TYPE_MAP.get(_clazz) == null)
      {
         ABSTRACT_TYPE_MAP.put(_clazz, new HashSet());
      }
      Set set = (Set) ABSTRACT_TYPE_MAP.get(_clazz);
      Class[] interfaces = _clazz.getInterfaces();
      for (int i=0; i<interfaces.length; i++)
      {
         if (ComplexEObject.class.isAssignableFrom(interfaces[i]))
         {
            set.add(interfaces[i]);
         }
      }

      Class superclass = _clazz.getSuperclass();
      // don't recurse through supertypes (on purpose)
      // see implementation of fireappnotification in this class
      if (superclass != null && ComplexEObject.class.isAssignableFrom(superclass))
      {
         set.add(superclass);
         superclass = superclass.getSuperclass();
      }
   }

   public static boolean isAbstract(Class cls)
   {
      return cls.isInterface() || Modifier.isAbstract(cls.getModifiers());
   }
   public boolean isAbstract() { return isAbstract(_clazz); }
   public boolean isInterfaceType() { return _clazz.isInterface(); }
   public boolean isChoice()
   {
      return Choice.class.isAssignableFrom(_clazz);
   }

   public boolean isAssignableFrom(ComplexType type)
   {
      return _clazz.isAssignableFrom(type.getJavaClass());
   }

   public AbstractListEO concreteTypes()
   {
      return (AbstractListEO) CONCRETE_TYPE_MAP.get(_clazz);
   }
   public boolean hasConcreteSubTypes()
   {
      return (concreteTypes().getSize() > 1);
   }
   public ComplexType[] getAbstractTypes()
   {
      Set set = (Set) ABSTRACT_TYPE_MAP.get(_clazz);
      Iterator itr = set.iterator();
      Class cls;
      ComplexType[] types = new ComplexType[set.size()];
      int i=0;
      while (itr.hasNext())
      {
         cls = (Class) itr.next();
         types[i++] = ComplexType.forClass(cls);
      }
      return types;
   }

   public boolean hasFieldOfType(Class cls)
   {
      return firstFieldOfType(cls) != null;
   }
   public Field firstFieldOfType(Class cls)
   {
      return firstFieldOfType(cls, false);  // should the default be false?
   }
   public Field firstFieldOfType(Class cls, boolean deep)
   {
      Field fld = null;
      for (int i=0; i<_fields.size(); i++)
      {
         fld = (Field) _fields.get(i);
         if (fld.getJavaClass().equals(cls))
         {
            return fld;
         }
      }
      if (!deep) return null;
      
      // recurse to children of aggregate fields..
      for (int i=0; i<type().fields().size(); i++)
      {
         Field childField = (Field) _type.fields().get(i);
         if (childField.isAggregate())
         {
            fld = ((AggregateField) childField).firstFieldOfType(StringEO.class);
            if (fld != null)
               break;
         }
      }
      
      return fld;
   }
   public boolean isCalendarable()
   {
      return hasFieldOfType(TimeSpan.class);
   }

   private void deriveNames()
   {
      _className = _clazz.getName();
      _shortName = shortName(_clazz);
      _naturalName = ProgrammingElement.deriveLabel(_shortName);
      _pluralName = derivePluralName(_naturalName);
   }

   //private static Reflector _reflector = new ClassicReflector();
   private static Reflector _reflector = new AnnotationsReflector();
   public static Reflector reflector() { return _reflector; }

   /**
    * The reason that cannot just fall back to type().commands() is that
    * static command methods on the class are unique per class and need
    * to be harvested per class.
    */
   protected Onion harvestTypeCommands()
   {
      // 1. inspect type..
      Onion base = new Onion();
      try
      {
         Class[] paramTypes = { CommandInfo.class };
         Method method = AbstractComplexEObject.ReadState.class.getMethod("Open",
                                                                          paramTypes);
         Command openCmd =
            _reflector.reflectCommand(method,
                                      AbstractComplexEObject.ReadState.class, this);
//         openCmd.getName().setValue("Inspect Type");
         base.add(openCmd);
      }
      catch (NoSuchMethodException ex)
      {
         System.err.println("No Such Method: "+ex.getMessage());
         ex.printStackTrace();
      }
      
      return Harvester.simpleHarvestCommands(_clazz, new Onion(base), true, this);
   }

   public Onion commands() { return _typeCommands; }

   public Onion filteredCommands()
   {
      return commands().filter(Command.commandFilter(this));
   }

   public Onion commands(State state)
   {
      if (state == null) throw new IllegalArgumentException("Cannot request commands for a null state");
      return commands(state.getClass());
   }
   public Onion commands(Class stateClass)
   {
      return (Onion) _commands.get(stateClass);
   }
   
   public Onion filteredCommands(EObject target, Class stateClass)
   {
      Onion commands = commands(stateClass);
      return commands.filter(Command.commandFilter(target));
   }

   public Command command(String commandName, Class stateClass)
   {
      Onion commands = commands(stateClass);
      SimpleFinder finder = Command.finder(commandName);
      return (Command) commands.find(finder);
   }
   public Command command(String commandName, State state)
   {
      Onion commands = commands(state);
      SimpleFinder finder = Command.finder(commandName);
      return (Command) commands.find(finder);
   }
   public Command command(String commandName)
   {
      Onion commands = commands();
      SimpleFinder finder = Command.finder(commandName);
      Command cmd = (Command) commands.find(finder);
      return cmd;
      // q: don't you need to disambiguate between different commands 
      //   with the same name but that exist in different states??
   }
   public Command instanceCommand(String commandName)
   {
      Command cmd = command(commandName);
      for (Class stateClass : _commands.keySet())
      {
         Onion commands = _commands.get(stateClass);
         SimpleFinder finder = Command.finder(commandName);
         cmd = (Command) commands.find(finder);
         if (cmd != null) break;
      }
      return cmd;
   }
   
   public Command findCommand(String commandName)
   {
      Command cmd = command(commandName);
      if (cmd == null)
      {
         cmd = instanceCommand(commandName);
      }
      return cmd;
   }

   /**
    * note it tries to locate a command from the commandslist
    * used only for restoring a command that was saved to persistencemechanism
    */
//   public Command findCommand(String commandName)
//   {
//      for (Iterator itr = _commandsList.iterator(); itr.hasNext(); )
//      {
//         Command cmd = (Command) itr.next();
//         if (cmd.name().equals(commandName))
//            return cmd;
//      }
//      return null;
//   }

   public String defaultCommandName() { return "Browse"; }

   public Class getJavaClass() { return _clazz; }
   public FieldParent parent() { return null; } // by definition
   public List fields() { return _fields; }

   public Field field(String propName)
   {
      Field field = (Field) _fieldsMap.get(propName);
      if (field == null && !type().equals(this))
      {
         field = type().field(propName);
      }
      return field;
   }

   /**
    * Returns the short name (with spacing) for this object in a pluralised form.
    * The plural from is obtained from the defining class's pluralName() method, 
    * if it exists, or by adding 's', 'es', or 'ies dependending of the name's 
    * ending.
    */
   private String derivePluralName(String name)
   {
      try
      {
         return (String) getJavaClass().getMethod("pluralName", null).invoke(null, null);
      } catch (NoSuchMethodException ignore) {
      } catch (IllegalAccessException ignore) {
      } catch (InvocationTargetException ignore) {
      }

      if (name.endsWith("y")) {
         name = name.substring(0, name.length() - 1) + "ies";
      } else if (name.endsWith("s")) {
         name += "es";
      } else {
         name += 's';
      }

      return name;
   }

   public String name() { return _shortName; }
   public String getQualifiedName() { return _className; }
   public String getNaturalName() { return _naturalName; }
   public String getPluralName() { return _pluralName; }

   private void loadFieldMetaData()
   {
      if (metadata == null) return;
      
      FieldRecurser.recurseFields(_fields, new FieldProcessor()
         {
            public void processField(Field field)
            {
               String stringValue = metadata.getProperty(field.getPath()+".default");

               if (stringValue != null)
               {
                  if ( field instanceof AtomicField )
                  {
                     ((AtomicField) field).getDefaultValue().setValue(stringValue);
                  }
                  else if (field instanceof AssociationField)
                  {
                     ((AssociationField) field).setDefaultSpec(stringValue);
                  }
               }

               String value = metadata.getProperty(field.getPath()+".required");
               if (value != null)
               {
                  Boolean required = Boolean.valueOf(value);
                  field.getRequired().setValue(required.booleanValue());
               }

            }
         });

   }

   public String localeLookup(String key)
   {
      return localeLookupStatic(key);
   }
   public static String localeLookupStatic(String key)
   {
      if (localeBundle == null) return null;
      try
      {
         return localeBundle.getString(key);
      }
      catch (MissingResourceException ex) {}
      return null;
   }

   private void localize()
   {
      if (localeBundle == null) return;
      localizeLabel();
      localizeFields();
      localizeCommands();
   }
   
   private void localizeLabel()
   {
      try
      {
         _naturalName = localeBundle.getString(_shortName);
         _pluralName = localeBundle.getString(_pluralName);
      }
      catch (MissingResourceException ex) {}
   }
   private void localizeFields()
   {
      FieldRecurser.recurseFields(_fields, new FieldProcessor()
         {
            public void processField(Field field)
            {
               field.localize(ComplexType.this);
            }
         });
   }
   private void localizeCommands()
   {
      allCommands(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            ((Command) ceo).localize(ComplexType.this);
         }
      });
   }
   public void instanceCommands(Block block)
   {
      for (Iterator itr = _commands.values().iterator(); itr.hasNext(); )
      {
         Onion cmds = (Onion) itr.next();
         cmds.forEach(block);
      }
   }
   public void allCommands(Block block)
   {
      instanceCommands(block);
      _typeCommands.forEach(block);
   }
   

   // *** icon stuff ***

   protected Icon _iconSm, _iconLg, _iconsSm, _iconsLg;
   public Icon iconSm() { return _iconSm; }
   public Icon iconLg() { return _iconLg; }
   public Icon iconsSm() { return _iconsSm; }
   public Icon iconsLg() { return _iconsLg; }

   public Title title() { return new Title(_pluralName); }
   public ColorEO getColorCode() { return _colorCode; }
   public Color colorCode() { return _colorCode.colorValue(); }
   public String sortBy() { return _sortBy; }  // a property name
   public Field sortField() { return field(_sortBy); }

   public boolean isSortable()
   {
      //return _sortBy != null;
      return false; // disable for now..i just realized that
      // hibernate sort() is not sophisticated enough.  it's not
      // an hql sort but a sql sort, so for example sort appointments
      // by charge.amount throws an exception :-(
   }


   // type commands:
   @Cmd
   public AbstractListEO Browse(CommandInfo cmdInfo)
   {
      if (_clazz.equals(ComplexType.class))
      {
         return persistedTypes();
      }
      return persistor().browse(_clazz);
   }

   private PlainListEObject _persistedTypes;
   private PlainListEObject persistedTypes()
   {
      if (_persistedTypes == null)
      {
         _persistedTypes = new PlainListEObject(ComplexType.class);
         HBMPersistenceMechanism hbm = hbmPersistor();
         Set<Class> persistClasses = hbm.getClasses();
         for (Class cls : persistClasses)
         {
            _persistedTypes.add(ComplexType.forClass(cls));
         }
      }
      return _persistedTypes;
   }

   // fetch items programmatically (do not expose in ui).  Browse
   // pages.  use this when want entire listing.  a good example is 
   // USStates or any other Choice type.
   public AbstractListEO list()
   {
      return persistor().list(this);
   }

   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo)
   {
      try
      {
         ComplexEObject ceo = (ComplexEObject) _clazz.newInstance();
         ceo.initialize();
         ceo.setTransientState();
         return ceo;
      }
      catch (java.lang.InstantiationException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }
      catch (IllegalAccessException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }
      return null;  // TODO: consider throwing instead of catching both exceptions 
   }
   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo,
                             ComplexType type)
   {
      return type.New(cmdInfo);
   }
   public ComplexType baseType()
   {
      if (hasConcreteSubTypes())
         return this;
      return null;
   }

   @Cmd
   public View Find(CommandInfo cmdInfo)
   {
      return vmech().getFindView2(this);
   }
   @Cmd
   public CompositeQuery SmartList(CommandInfo cmdInfo)
   {
      CompositeQuery query = new CompositeQuery(this);
      query.setTransientState();
      return query;
   }

   @Cmd
   public /* ComplexEObject */ String ImportFromXML(CommandInfo info)
   {
      // a. pick xml file to import from
      // b. type assumption is "this"
      // c. unmarshal xml to object
      // d. set object to transient state
      // e. return object

      // return null;
      return "[tbd]";
   }



   // transferable stuff - TODO:  a possible candidate for aop
   private DataFlavor _flavor = null;
   public DataFlavor getFlavor()
   {
      if (_flavor == null)
      {
         try
         {
            String dataFlavorType = DataFlavor.javaJVMLocalObjectMimeType;
            dataFlavorType += ";class="+_clazz.getName();
            _flavor = new DataFlavor(dataFlavorType);
         }
         catch (ClassNotFoundException ex)
         {
            ex.printStackTrace();
         }
      }
      return _flavor;
   }


   // for copy-paste operations
   private ComplexEObject _bufferCopy;
   public ComplexEObject bufferCopy() { return _bufferCopy; }
   public void bufferCopy(ComplexEObject bufferCopy) { _bufferCopy = bufferCopy; }


   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (!(obj instanceof ComplexType)) return false;
      ComplexType ct = (ComplexType) obj;
      return _className.equals(ct.getQualifiedName());
   }

   public int hashCode() { return 31 * _clazz.hashCode(); }


   public boolean isSingleton()
   {
      return Singleton.class.isAssignableFrom(_clazz);
   }
   public ComplexEObject instance()
   {
      if (!isSingleton())
         return New(null);

      try
      {
         Method getInstance = _clazz.getMethod("getInstance");
         return (ComplexEObject) getInstance.invoke(null);
      }
      catch (Exception ex)
      {
         System.err.println("Exception: "+ex.getMessage());
         ex.printStackTrace();
         return null;
      }
   }


   private TreeModel _searchTreeModel;
   public TreeModel searchTreeModel()
   {
      if (_searchTreeModel == null)
         _searchTreeModel = new SearchTreeModel();
      return _searchTreeModel;
   }

   class SearchTreeModel implements TreeModel
   {
      public Object getRoot()
      {
         _treeFields = new HashSet();
         return ComplexType.this;
      }
      public Object getChild(Object parent, int index)
      {
         FieldParent fp = fieldParent(parent);
         return searchFields(fp).get(index);
      }
      public int getChildCount(Object parent)
      {
         FieldParent fp = fieldParent(parent);
         return searchFields(fp).size();
      }
      public boolean isLeaf(Object node)
      {
         if (node instanceof AssociationSearchNode)
         {
            Field field = ((AssociationSearchNode) node).getField();
            if (_treeFields.contains(field))
               return true;
            else
            {
               _treeFields.add(field);
               return false;
            }
         }
         else if (node instanceof ComplexType)
         {
            return true;
         }

         Field field = (Field) node;
         return (field.isAtomic() || field.isIndexed() || field.isAssociation()
               || field.isChoice() );
      }
      // TreeModel implemented specifically for JComboTree, which doesn't employ these:
      public int getIndexOfChild(Object parent, Object child) { return 0; }
      public void addTreeModelListener(TreeModelListener listener) {}
      public void removeTreeModelListener(TreeModelListener listener) {}
      public void valueForPathChanged(TreePath path, Object newValue) {}


      private Map searchFieldMap = new HashMap();
      private Set _treeFields;

      private List searchFields(FieldParent fp)
      {
         if (searchFieldMap.get(fp) == null)
         {
            java.util.List searchFields = new ArrayList();
            if (fp.isAbstract())
            {
               searchFields.add(fp);
            }

            Field field = null;
            for (int i=0; i<fp.fields().size(); i++)
            {
               field = (Field) fp.fields().get(i);
               if (!field.isSearchable())
                  continue;

               searchFields.add(field);
               // add it a second time: (for drilling down to association's fields)
               if (field.isAssociation())
               {
                  // exclude interface type associations because the concrete
                  // types vary and so cannot (and should not) drill down to
                  // interface type associations
                  if (!field.isInterfaceType())
                  {
                     searchFields.add(new AssociationSearchNode((AssociationField) field));
                  }
               }
            }
            searchFieldMap.put(fp, searchFields);
         }
         return (List) searchFieldMap.get(fp);
      }
      private FieldParent fieldParent(Object parent)
      {
         if (parent instanceof AssociationSearchNode)
         {
            AssociationField field = ((AssociationSearchNode) parent).getField();
            return field.fieldtype();
         }
         return (FieldParent) parent;
      }

   }

   public class AssociationSearchNode
   {
      AssociationField _field;
      public AssociationSearchNode(AssociationField field) { _field = field; }
      public AssociationField getField() { return _field; }
      public String toString() { return _field.toString(); }
   }


   private final RelationalList _fieldsList = new RelationalList(Field.class);
   public static Class fieldsListType = Field.class;
   public RelationalList getFieldsList() { return _fieldsList; }


   private final RelationalList _commandsList = new RelationalList(Command.class);
   public static Class commandsListType = Command.class;
   public RelationalList getCommandsList()
   {
      if (_commandsList.isEmpty())
         populateCommandsList();
      return _commandsList;
   }

   public boolean isEmpty() { return false; }

   private final RelationalList _queries = new RelationalList(CompositeQuery.class);
   public static Class queriesType = CompositeQuery.class;
   public RelationalList getQueries() { return _queries; }

   public void addQuery(CompositeQuery query)
   {
      getQueries().add(query);
      Command cmd = new QueryCommandAdapter(query, this);
      _typeCommands.add(cmd);
      _queryCmdMap.put(query, cmd);
      // discussion:  query.command("Execute") is how you should get the command
      // but need to change its name to the query's name;  so what you really
      // need is:  query.command("Execute").clone().setName(query.getName());
   }
   Map _queryCmdMap = new HashMap();
   public void removeQuery(CompositeQuery query)
   {
      getQueries().remove(query);
      _typeCommands.remove(_queryCmdMap.get(query));
      _queryCmdMap.remove(query);
   }


   private void populateCommandsList()
   {
      Set cmds = populateCommands(_commands);
      cmds.addAll(populateCommands(commands()));
      _commandsList.setItems(cmds);
   }
   private Set populateCommands(Map cmdMap)
   {
      Set cmdSet = new HashSet();
      for (Iterator itr=cmdMap.keySet().iterator(); itr.hasNext(); )
      {
         Class key = (Class) itr.next();
         Onion stateCmds = (Onion) cmdMap.get(key);
         cmdSet.addAll(populateCommands(stateCmds));
      }
      return cmdSet;
   }
   private Set populateCommands(Onion cmdOnion)
   {
      Set<Command> cmdSet = new HashSet<Command>();
      for (Iterator itr=cmdOnion.deepIterator(); itr.hasNext(); )
      {
         Command cmd = (Command) itr.next();
         if (MINORCOMMANDS.contains(cmd.name())) continue;
         cmdSet.add(cmd);
      }
      return cmdSet;
   }
   private static List MINORCOMMANDS =
      Arrays.asList(new String[] {"Copy", "Paste", "Save", "Cancel"});



   public int validate(ComplexEObject instance)
   {
      int count = 0;
      for (Iterator itr = fields().iterator(); itr.hasNext(); )
      {
         Field field = (Field) itr.next();
         count += field.validate(instance);
      }
      return count;
   }

   public static String pluralName() { return "Types"; }

   public static Class getCustomTypeImplementorClass()
   {
      return ComplexTypeUserTypeDelegate.class;
   }


   public static void associateQueries(PersistenceMechanism pmech)
   {
      if (!(pmech instanceof HBMPersistenceMechanism))
      {
         return;
      }
      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech;
      Session s = hbm.getSession();

      try
      {
         Criteria c = s.createCriteria(CompositeQuery.class);
         c.addOrder(Order.asc("queryType"));
         Iterator itr = c.list().iterator();
         CompositeQuery query = null;
         while (itr.hasNext())
         {
            query = (CompositeQuery) itr.next();
            query.onLoad();
            query.getQueryType().addQuery(query);
         }
      }
      catch (HibernateException ex)
      {
         System.err.println("HibernateException: "+ex.getMessage());
         ex.printStackTrace();
      }
   }

   public boolean isMeta() { return true; }


   private StringEO _defaultSearchPath = new StringEO();

   public StringEO getDefaultSearchPath()
   {
      StringEO path = _defaultSearchPath;
      if ( (path.isEmpty()) && !_clazz.isInterface() &&
           (ComplexEObject.class.isAssignableFrom(_clazz.getSuperclass())) )
      {
         path.setValue(ComplexType.forClass(_clazz.getSuperclass()).getDefaultSearchPath());
      }
      return path;
   }
   
   public String defaultSearchPath() { return getDefaultSearchPath().stringValue(); }

   public void defaultSearchPath(String path)
   {
      _defaultSearchPath.setValue(path);
   }
   public boolean hasDefaultSearchPath()
   {
      return (!getDefaultSearchPath().isEmpty());
   }
   
   
   public static String shortName(Class cls)
   {
      String name = cls.getName();
      return name.substring(name.lastIndexOf(".")+1);
   }


   /* override fireappeventnotification to also notify abstract types that
      this type represent */

   public void fireAppEventNotification(AppEventType evtType)
   {
      super.fireAppEventNotification(evtType);
      ComplexType[] abstractTypes = getAbstractTypes();
      for (int i=0; i<abstractTypes.length; i++)
      {
         abstractTypes[i].fireAppEventNotification(evtType);
      }
   }
   public void fireAppEventNotification(AppEventType evtType, Object target)
   {
      super.fireAppEventNotification(evtType, target);
      ComplexType[] abstractTypes = getAbstractTypes();
      for (int i=0; i<abstractTypes.length; i++)
      {
         abstractTypes[i].fireAppEventNotification(evtType, target);
      }
   }
   
   public ComplexType superType()
   {
      Class superClass = _clazz.getSuperclass();
      if ( superClass == null ||
           !(ComplexEObject.class.isAssignableFrom(superClass)) )
      {
         return null;
      }
      
      return ComplexType.forClass(superClass);
   }

}
