<?xml version="1.0" encoding="utf-8"?>

<jnlp spec="1.0+"
      codebase="\$\$codebase"
      href="project.jnlp">

 <information>
   <title>Project App</title> <!-- TODO: revise title appropriately -->
   <vendor>UptoData, Inc.</vendor> <!-- TODO: plug in vendor name here -->
   <homepage href="index.html" />
   <description kind="one-line">@PROJECTNAME@ App</description>
   <description kind="short">@PROJECTNAME@ App</description>
   <description kind="tooltip">@PROJECTNAME@ App</description>
   <icon kind="shortcut" href="App32.png" /> <!-- TODO: copy App32.png to 'static' folder -->
   <icon kind="splash" href="splash.png" /> <!-- TODO: reference proper splash img url -->
   <offline-allowed />
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

</jnlp>

