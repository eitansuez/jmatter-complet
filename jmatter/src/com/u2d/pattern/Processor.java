/*
 * Created on Apr 27, 2004
 */
package com.u2d.pattern;

/**
 * A very generic interface that can be used for a variety of callbacks..(see Onion for example)
 * 
 * @author Eitan Suez
 */
public interface Processor
{
   public void process(Object obj);
   public void pause();
   public void done();
}
