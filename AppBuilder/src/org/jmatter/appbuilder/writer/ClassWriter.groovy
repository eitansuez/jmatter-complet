package org.jmatter.appbuilder.writer

import org.jmatter.appbuilder.EntityAB
import org.jmatter.appbuilder.FieldAB
import com.u2d.model.Harvester
import org.jmatter.appbuilder.CommandAB
import org.jmatter.appbuilder.MemberAB
import org.jmatter.appbuilder.FieldMetadata

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Sep 14, 2008
 * Time: 10:30:13 AM
 */
class ClassWriter
{
   EntityAB entity
   StringWriter writer
   int indentLevel = 0
   int indentSize = 3
   Set imports, readOnly, identities, fieldOrder
   
   ClassWriter(EntityAB entity)
   {
      imports = [] as Set
      readOnly = [] as Set
      identities = [] as Set
      fieldOrder = [] as Set

      this.entity = entity

      addImport "javax.persistence.Entity"
      addImport "com.u2d.model.AbstractComplexEObject"
      addImport "import com.u2d.model.Title"

      if (!entity.getChildFields().isEmpty())
      {
         addImport "com.u2d.reflection.Fld"
      }
      
      def fields = entity.getChildFields().getItems()
      fields.each { FieldAB field ->
         def fieldType = fieldType(field)
         addImport fieldType
         String fldName = field.getName().stringValue()
         if (field.getMetadata().getReadOnly().isTrue())
         {
            readOnly << fldName
         }
         if (field.getMetadata().getIdentity().isTrue())
         {
            identities << fldName
         }
         fieldOrder << fldName

         if (field.isRelationalListField())
         {
            addImport "com.u2d.list.RelationalList"
         }
         if (field.isCompositeListField())
         {
            addImport "com.u2d.list.CompositeList"
         }
      }

      if (!entity.getCommands().isEmpty())
      {
         addImport "com.u2d.element.CommandInfo"
         addImport "com.u2d.reflection.Cmd"
      }
   }

   def addImport(String clsName)
   {
      if (qualified(clsName) && !samePackage(clsName))
      {
         imports << clsName
      }
   }

   def samePackage(String clsName)
   {
      pkgName().equals(pkgName(clsName))
   }
   
   def qualified(String clsName)
   {
      clsName.indexOf(".") > 0
   }
   def fieldType(FieldAB field) { field.getFieldType().stringValue() }
   def shortTypeName(String type) { type.substring(type.lastIndexOf(".") + 1) }

   def pkgName(String type) { type.substring(0, type.lastIndexOf(".")) }

   def pkgName()
   {
     if (entity.getPackageName().isEmpty())
     {
       return entity.getProject().getDefaultPackageName().stringValue()
     }
     entity.getPackageName().stringValue()
   }

   def entityName() { entity.getName().stringValue() }

   def String writeIt()
   {
      writer = new StringWriter()

      writeDecl this.&packageDecl
      writeDecl this.&importDecls
      classDecl {
         writeDecl this.&staticDecls
         writeDecl this.&constrDecl
         writeDecl this.&fieldDecls
         writeDecl this.&commandDecls
         writeDecl this.&titleDecl
      }

      writer.toString()
   }

   // ====================

   def indent() { " " * indentSize * indentLevel }
   def separate() { writer.append("\n") }
   def write(String text)
   {
      writer.append(indent())
      writer.append(text)
   }
   def writeln(String text)
   {
      write(text)
      separate()
   }
   def writeLines(String text)
   {
      def lines = text.split("\n")
      lines.each { line ->
         writeln(line)
      }
   }
   def writeDecl(Closure closure)
   {
      closure()
      separate()
   }
   def nest(Closure closure)
   {
      writeln "{"
      indentLevel++
      closure()
      indentLevel--
      writeln "}"
   }

   // ====================

   def packageDecl()
   {
      writeln "package ${pkgName()};"
   }
   def importDecls()
   {
      imports.each { clsName ->
         writeln "import ${clsName};"
      }
   }

   def classDecl(Closure closure)
   {
      writeln "@Entity"
      writeln "public class ${entityName()} extends AbstractComplexEObject"
      nest closure
   }

   def constrDecl()
   {
      writeln "public ${entityName()}()"
      nest {}
   }

   def staticDecls()
   {
      staticDecl("readOnly", readOnly)
      staticDecl("identities", identities)
      staticDecl("fieldOrder", fieldOrder)

      writeNaturalNameIfNeeded();
      writePluralNameIfNeeded();
   }
   def writeNaturalNameIfNeeded()
   {
      if (!entity.getCaption().isEmpty())
      {
         writeln "public static String naturalName()"
         nest {
            writeln "return \"${entity.getCaption()}\";"
         }
      }
   }
   def writePluralNameIfNeeded()
   {
      if (!entity.getPluralName().isEmpty())
      {
         writeln "public static String pluralName()"
         nest {
            writeln "return \"${entity.getPluralName()}\";"
         }
      }
   }

   def staticDecl(String declFldName, Set set)
   {
      if (set.size() > 0)
      {
         write("public static String[] ${declFldName} = {")
         def fieldsListing = set.collect { String fldName -> "\"${fldName}\"" }
         writer.append(fieldsListing.join(", "))
         writer.append("};")
         separate()
      }
   }

   def fieldDecls()
   {
      def fields = entity.getChildFields().getItems()
      fields.each { FieldAB field ->
         
         def fieldName = field.getName().stringValue()
         def fieldType = fieldType(field)
         def shortTypeName = shortTypeName(fieldType)
         def getterName = Harvester.makeGetterName(fieldName)

         if (field.isCompositeField())
         {
            writeln "private final ${shortTypeName} ${fieldName} = new ${shortTypeName}();"
            writeFldAnnotationIfNeeded(field)
            writeln "${shortTypeName} ${getterName}() { return ${fieldName}; }"
         }
         else if (field.isAssociationField())
         {
            def setterName = Harvester.makeSetterName(fieldName)
            def oldName = "old" + Harvester.capitalize(fieldName)

            writeln "private ${shortTypeName} ${fieldName};"
            writeFldAnnotationIfNeeded(field)
            writeln "${shortTypeName} ${getterName}() { return ${fieldName}; }"
            writeln "public void ${setterName}(${shortTypeName} ${fieldName})"
            nest {
               writeln "${shortTypeName} ${oldName} = this.${fieldName};"
               writeln "this.${fieldName} = ${fieldName};"
               writeln "firePropertyChange(\"${fieldName}\", ${oldName}, this.${fieldName});"
            }
         }
         else if (field.isRelationalListField())
         {
            writeln "private final RelationalList ${fieldName} = new RelationalList(${fieldType}.class);"
            writeln "public static Class ${fieldName}Type = ${fieldType}.class";
            writeln "public RelationalList ${getterName}() { return ${fieldName}; }"
         }
         else if (field.isCompositeListField())
         {
            writeln "private final CompositeList ${fieldName} = new CompositeList(${fieldType}.class);"
            writeln "public static Class ${fieldName}Type = ${fieldType}.class";
            writeln "public CompositeList ${getterName}() { return ${fieldName}; }"
         }
         separate()
      }
   }

   // TODO:  there are a number of other Fld options/attributes that I do not yet capture
   private def writeFldAnnotationIfNeeded(FieldAB field)
   {
      if ( !(field.getCaption().isEmpty() &&
             field.getDescription().isEmpty() &&
             field.getMnemonic().isEmpty()) ||
             field.getMetadata().needToWriteAnnotation() )
      {
         write "@Fld("
         List attrsList = baseAnnotationAttrs(field)

         FieldMetadata md = field.getMetadata();
         if (md.getHidden().isTrue())
         {
            attrsList << "hidden=true"
         }
         if (md.getPersist().isFalse())
         {
            attrsList << "persist=false"
         }
         if (!md.getFormat().isEmpty())
         {
            attrsList << "format=\"${md.getFormat()}\"";
         }
         if (md.getDisplaysize().intValue() > 0)
         {
            attrsList << "displaysize=${md.getDisplaysize()}"
         }
         if (!md.getColname().isEmpty())
         {
            attrsList << "colname=\"${md.getColname()}\""
         }
         if (md.getColsize().intValue() > 0)
         {
            attrsList << "colsize=${md.getColsize().intValue()}"
         }

         writer.append(attrsList.join(", "))
         writer.append(")")
         separate()
      }
   }

   def commandDecls()
   {
      def commands = entity.getCommands().getItems()
      commands.each { CommandAB cmd ->
         def cmdName = cmd.getName().stringValue()
         writeCmdAnnotationIfNeeded(cmd)
         writeln "public void ${cmdName}(CommandInfo cmdInfo)"
         nest {
            writeLines(cmd.getBody().stringValue())
         }
         separate()
      }
   }
   private def writeCmdAnnotationIfNeeded(CommandAB cmd)
   {
      if (!(cmd.getCaption().isEmpty() &&
            cmd.getDescription().isEmpty() &&
            cmd.getMnemonic().isEmpty()) &&
            cmd.getSensitive().isFalse())
      {
         write "@Cmd("
         List attrsList = baseAnnotationAttrs(cmd)
         if (cmd.getSensitive().isTrue())
         {
            attrsList << "sensitive=true"
         }
         writer.append(attrsList.join(", "))
         writer.append(")")
         separate()
      }
   }

   private def baseAnnotationAttrs(MemberAB member)
   {
      List attrsList = []
      if (!member.getCaption().isEmpty())
      {
         attrsList << "label=\"${member.getCaption()}\""
      }
      if (!member.getDescription().isEmpty())
      {
         attrsList << "description=\"${member.getDescription()}\""
      }
      if (!member.getMnemonic().isEmpty())
      {
         attrsList << "mnemonic='${member.getMnemonic()}'"
      }
      return attrsList
   }

   def titleDecl()
   {
      writeln "public Title title()"
      nest {
         if (entity.getTitleMethodBody().isEmpty())
         {
            writeln "// TODO: implement title method"
            writeln "return null;"
         }
         else
         {
            writeLines entity.getTitleMethodBody().stringValue()
         }
      }
   }

}