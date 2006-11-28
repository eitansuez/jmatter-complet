/*
 * Created on Apr 27, 2004
 */
package com.u2d.pattern;

import java.util.Iterator;


/**
 * Sample Usage:
 * 
new OnionPeeler(new Processor()
{
   public void process(Object obj) 
   {
      // do something with obj
   }
   public void pause() { Tracing.tracer().info("going to next layer.."); }
   public void done() { Tracing.tracer().info("done."); }
}).peel();

 *
 *  @author Eitan Suez
 */
public class OnionPeeler
{
   private Processor _processor;
   
   public OnionPeeler(Processor processor)
   {
      _processor = processor;
   }
   
   public void peel(Onion onion)
   {
      for (Iterator itr = onion.iterator(); itr.hasNext(); )
      {
         _processor.process(itr.next());
      }
      if (onion.hasMoreLayers())
      {
         _processor.pause();
         peel(onion.getInnerLayer());
      }
      else
      {
         _processor.done();
      }
   }
}

