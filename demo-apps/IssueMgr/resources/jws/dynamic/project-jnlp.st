<?xml version="1.0" encoding="utf-8"?>

<jnlp spec="1.0+"
      codebase="http://$ip_addr$:$port$/IssueMgr"
      href="project.jnlp">

 <information>
   <title>Project App</title> <!-- TODO: revise title appropriately -->
   <vendor>DS Allergy, Inc.</vendor> <!-- TODO: plug in vendor name here -->
   <homepage href="index.html" />
   <description kind="one-line">IssueMgr App</description>
   <description kind="short">IssueMgr App</description>
   <description kind="tooltip">IssueMgr App</description>
   <icon href="leaf.jpg" />
   <icon kind="splash" href="leaf.jpg" /> <!-- TODO: plug in splash image here -->
 </information>

 <security>
   <all-permissions />
 </security>

 <resources>
   <j2se version="1.4+" />
   <jar href="IssueMgr.jar" main="true" />
   $jars:{jar|
   <jar href="$jar$" />
   }$
 </resources>

 <!-- TODO: plug in main class name here -->
 <application-desc main-class="com.u2d.view.swing.EntryPoint">
   <argument>-Xdock:name=IssueMgr</argument>
   <argument>-Xdock:icon=icons.icns</argument>
 </application-desc>

</jnlp>

