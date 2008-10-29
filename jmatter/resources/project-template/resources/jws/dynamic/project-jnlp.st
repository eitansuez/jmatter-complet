<?xml version="1.0" encoding="utf-8"?>

<jnlp spec="1.0+"
      codebase="\$\$codebase"
      href="project.jnlp">

 <information>
   <title>@PROJECTNAME@ Application</title> <!-- TODO: revise title appropriately -->
   <vendor>JMatterSoft LLC</vendor> <!-- TODO: plug in vendor name here -->
   <homepage href="index.html" />
   <description kind="one-line">@PROJECTNAME@ App</description>
   <description kind="short">@PROJECTNAME@ App</description>
   <description kind="tooltip">@PROJECTNAME@ App</description>
   <icon kind="shortcut" href="App32.png" /> <!-- TODO: copy App32.png to 'static' folder -->
   <icon kind="splash" href="splash.png" /> <!-- TODO: reference proper splash img url -->
   <offline-allowed />
   <shortcut online="false">
     <desktop />
     <menu submenu="JMatter" />
   </shortcut>
 </information>

 <security>
   <all-permissions />
 </security>

 <resources>
   <j2se version="1.5+" />
   <jar href="lib/swingvm.jar" main="true" />
   $jars:{jar|
   <jar href="$jar$" />
   }$
 </resources>

 <application-desc main-class="com.u2d.view.swing.EntryPoint">
   <argument>-Xdock:name=@PROJECTNAME@</argument>
 </application-desc>

 <applet-desc main-class="com.u2d.view.swing.JMApplet"
              width="100%" height="100%"
              name="Sympster">
 </applet-desc>

</jnlp>

