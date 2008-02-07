<?xml version="1.0" encoding="utf-8"?>

<jnlp spec="1.0+"
      codebase="http://$ip_addr$:$port$/ContactMgr"
      href="project.jnlp">

 <information>
   <title>Contact Mgr</title>
   <vendor>JMatterSoft, LLC</vendor>
   <homepage href="index.html" />
   <description kind="one-line">ContactMgr App</description>
   <description kind="short">ContactMgr App</description>
   <description kind="tooltip">ContactMgr App</description>
   <icon href="splash.png" />
   <icon kind="splash" href="splash.png" />
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
   <argument>-Xdock:name=ContactMgr</argument>
 </application-desc>

</jnlp>

