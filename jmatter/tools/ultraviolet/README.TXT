========================
UltraViolet, Integrated into JMatter Distribution
========================

To Use UltraViolet:

From JMatter's base directory, invoke the ant target "ultraviolet"

  ant ultraviolet

1. Create and save a new .uml file, or open an existing one (e.g. pets.uml)

2. JMatter->JMatter Project Control
    A dialog will come up. The project name should be pet, and the JMatter directory should be where 
    you selected in step 4. The 'Generate Project' button should be enabled.

3. Click 'Generate Project'. You should see the output from the build in the output text. Wait.

4. Click 'Generate Code From UML'. Again wait. This step compiles the .uml using the umlc compiler into the java source

5. Click 'Schema Export' This runs the schema-export ant target in the project. Again wait.

6. Click 'Run Project' . The JMatter app should run.

