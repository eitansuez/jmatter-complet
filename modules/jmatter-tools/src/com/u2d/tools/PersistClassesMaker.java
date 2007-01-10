package com.u2d.tools;

import com.sun.mirror.apt.*;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import java.util.*;
import java.io.PrintWriter;
import java.io.File;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 9, 2007
 * Time: 1:41:49 PM
 * 
 * See AntPersistClassesMaker.  That's the one the project is using.
 * This one is not actively used.
 * 
 * This strategy fails because java5's APT  processes
 * only files that need to be compiled.  So I can't extract an
 * annotation off a source file because it's up to date.
 * 
 * FYI:  I didn't bother checking in java5's tools.jar in lib/
 * So if you want to compile this, make sure you add it to your path.
 */
public class PersistClassesMaker implements AnnotationProcessorFactory
{
   private static final Collection<String> supportedAnnotations = 
         Collections.unmodifiableCollection(Arrays.asList("com.u2d.tools.Persist"));
   private static final Collection<String> supportedOptions = Collections.emptySet();
   
   public Collection<String> supportedAnnotationTypes() { return supportedAnnotations; }
   public Collection<String> supportedOptions() { return supportedOptions; }

   public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> set, 
                                              AnnotationProcessorEnvironment env)
   {
      return new Grabber(env, set.iterator().next());
   }
   
   private static class Grabber implements AnnotationProcessor
   {
      private final AnnotationProcessorEnvironment _env;
      private final AnnotationTypeDeclaration _atd;

      static Set<String> classSet = new HashSet<String>();

      Grabber(AnnotationProcessorEnvironment env, AnnotationTypeDeclaration atd)
      {
         _env = env;
         _atd = atd;
      }
      
      public void process()
      {
         for (Declaration decl : _env.getDeclarationsAnnotatedWith(_atd))
         {
            decl.accept(DeclarationVisitors.getDeclarationScanner(new GrabberVisitor(), 
                                                                      DeclarationVisitors.NO_OP));
         }
         
         try
         {
            String sourcepath = _env.getOptions().get("-sourcepath");
            File templateFile = new File(sourcepath, "persistClasses.vm");
         
            Properties config = new Properties();
            config.put("resource.loader", "file");
            config.put("file.resource.loader.class",
                       "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            config.put("file.resource.loader.path",
                       templateFile.getParent());

            Velocity.init(config);

            VelocityContext context = new VelocityContext();

            context.put("classnames", classSet);

            Template template = Velocity.getTemplate(templateFile.getName());
            PrintWriter writer = 
                  _env.getFiler().createTextFile(Filer.Location.CLASS_TREE, "", 
                                                 new File("persistClasses.xml"), null);
         
            template.merge(context, writer);
            writer.flush();
            writer.close();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

      }
      
      private static class GrabberVisitor extends SimpleDeclarationVisitor
      {
         public void visitTypeDeclaration(TypeDeclaration typeDeclaration)
         {
            classSet.add(typeDeclaration.getQualifiedName());
         }
      }
   }
}
