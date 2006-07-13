#!/bin/sh

# typically use "ant run" to run your app while developing.
# however if you want a run.sh script, follow these instructions:

# run your application with
#        ant -v run
#    ant in verborse mode will output the actually commandline
#    command invoking java with the complete classpath and main 
#    class name.  just copy that output from the console and
#    place it below and you'll be all set.
/usr/lib/j2sdk1.5-sun/jre/bin/java -Xmx192m -classpath /home/eitan/projects/u2d/MyTunes/lib/src/AppleJavaExtensions.jar:/home/eitan/projects/u2d/MyTunes/lib/src/Date_selector.jar:/home/eitan/projects/u2d/MyTunes/lib/src/browserlauncher.jar:/home/eitan/projects/u2d/MyTunes/lib/src/commons-pool-1.2.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jmatter.jar:/home/eitan/projects/u2d/MyTunes/lib/src/ds-swing.jar:/home/eitan/projects/u2d/MyTunes/lib/src/ds-wizard.jar:/home/eitan/projects/u2d/MyTunes/lib/src/forms-1.0.5.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/antlr-2.7.5H3.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/asm-attrs.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/asm.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/cglib-2.1.2.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/commons-collections-2.1.1.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/commons-logging-1.0.4.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/dom4j-1.6.1.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/ehcache-1.1.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/jaxen-1.1-beta-7.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/jdbc2_0-stdext.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hb/jta.jar:/home/eitan/projects/u2d/MyTunes/lib/src/hibernate3.jar:/home/eitan/projects/u2d/MyTunes/lib/src/iText.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jdnc/jdnc-0_7-all.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jdnc/jlfgr-1_0.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jfree/bsh-1.3.0.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jfree/jcommon-1.0.0-rc1.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jfree/jcommon-xml-1.0.0-rc1.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jfree/pixie-0.8.4.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jfree/poi-2.5.1-final-20040804.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jfreereport-0.8.5-3.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jibx-extras.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jibx-run.jar:/home/eitan/projects/u2d/MyTunes/lib/src/jl1.0.jar:/home/eitan/projects/u2d/MyTunes/lib/src/l2fprod-common-all.jar:/home/eitan/projects/u2d/MyTunes/lib/src/mp3spi1.9.3.jar:/home/eitan/projects/u2d/MyTunes/lib/src/postgresql-8.0-313.jdbc3.jar:/home/eitan/projects/u2d/MyTunes/lib/src/statemap.jar:/home/eitan/projects/u2d/MyTunes/lib/src/tritonus_share.jar:/home/eitan/projects/u2d/MyTunes/lib/src/xpp3.jar:/home/eitan/projects/u2d/MyTunes/build/classes com.u2d.app.Application


