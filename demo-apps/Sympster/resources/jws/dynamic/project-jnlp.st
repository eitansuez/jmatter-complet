<?xml version="1.0" encoding="utf-8"?>

<jnlp spec="1.0+"
      codebase="\$\$codebase"
      href="project.jnlp">

 <information>
   <title>Sympster JMatter Demo Application</title>
   <vendor>JMatterSoft LLC</vendor>
   <homepage href="index.html" />
   <description kind="one-line">Sympster App</description>
   <description kind="short">Sympster App</description>
   <description kind="tooltip">Sympster App</description>
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
   <j2se version="1.5+" />
   <jar href="lib/swingvm.jar" main="true" />
   $jars:{jar|
   <jar href="$jar$" />
   }$
 </resources>

 <application-desc main-class="com.u2d.view.swing.EntryPoint">
   <argument>-Xdock:name=Sympster</argument>
 </application-desc>

</jnlp>

