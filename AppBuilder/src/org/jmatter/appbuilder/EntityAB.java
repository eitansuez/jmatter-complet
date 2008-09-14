package org.jmatter.appbuilder;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.ImgEO;
import com.u2d.type.atom.TextEO;
import com.u2d.reflection.Fld;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.IdxFld;
import com.u2d.element.CommandInfo;
import com.u2d.list.RelationalList;
import javax.persistence.Entity;
import org.jmatter.appbuilder.writer.ClassWriter;
import java.awt.datatransfer.StringSelection;
import java.awt.*;

@Entity
public class EntityAB extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "caption", "pluralName", "smallIcon", "largeIcon", "packageName", "project",
            "titleMethodBody"};
   public static String[] tabViews = {"childFields", "commands"};


   public EntityAB() { }

   private final StringEO name = new StringEO();
   public StringEO getName() { return name; }

   private final StringEO caption = new StringEO();
   @Fld(description="Leave blank unless want to override default derivation from name")
   public StringEO getCaption() { return caption; }

   private final StringEO pluralName = new StringEO();
   @Fld(description="optional;  derived if left blank")
   public StringEO getPluralName() { return pluralName; }

   private final ImgEO smallIcon = new ImgEO();
   public ImgEO getSmallIcon() { return smallIcon; }

   private final ImgEO largeIcon = new ImgEO();
   public ImgEO getLargeIcon() { return largeIcon; }

   private final StringEO packageName = new StringEO();
   @Fld(description="optional;  defaults to project's")
   public StringEO getPackageName() { return packageName; }

   private ProjectAB project;
   public ProjectAB getProject() { return project; }
   public void setProject(ProjectAB project)
   {
      ProjectAB oldProject = this.project;
      this.project = project;
      firePropertyChange("project", oldProject, this.project);
   }

   private final TextEO titleMethodBody = new TextEO();
   public TextEO getTitleMethodBody() { return titleMethodBody; }

   private final RelationalList childFields = new RelationalList(FieldAB.class);
   public static Class childFieldsType = FieldAB.class;
   @IdxFld(ordered=true)
   public RelationalList getChildFields() { return childFields; }

   private final RelationalList commands = new RelationalList(CommandAB.class);
   public static Class commandsType = CommandAB.class;
   @IdxFld(ordered=true)
   public RelationalList getCommands() { return commands; }


   @Cmd(mnemonic='w')
   public Object WriteSourceToClipboard(CommandInfo cmdInfo)
   {
      String source = new ClassWriter(this).writeIt();
      StringSelection selection = new StringSelection(source);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
      return "Done";
   }

   public Title title() { return name.title(); }

   public static String naturalName() { return "Entity"; }

}
