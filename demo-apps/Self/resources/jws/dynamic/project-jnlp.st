<?xml version="1.0" encoding="utf-8"?>

<jnlp spec="1.0+"
      codebase="\$\$codebase"
      href="project.jnlp">

 <information>
   <title>"Self" JMatter Demo App</title>
   <vendor>JMatterSoft LLC</vendor>
   <homepage href="index.html" />
   <description kind="one-line">Self App</description>
   <description kind="short">Self App</description>
   <description kind="tooltip">Self App</description>
   <icon kind="shortcut" href="App32.png" />
   <icon kind="splash" href="splash.png" />
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
   <property name="jnlp.packEnabled" value="true" />
   <j2se version="1.5+" />
   <jar href="lib/swingvm.jar" main="true" />
   $jars:{jar|
   <jar href="$jar$" />
   }$
 </resources>

 <application-desc main-class="com.u2d.view.swing.EntryPoint">
   <argument>-Xdock:name=Self</argument>
 </application-desc>

 <applet-desc main-class="com.u2d.view.swing.JMApplet"
              width="100%" height="100%"
              name="Sympster">
 </applet-desc>

</jnlp>

