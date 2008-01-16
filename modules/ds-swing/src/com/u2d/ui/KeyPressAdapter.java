package com.u2d.ui;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 16, 2008
 * Time: 11:04:57 PM
 */
public class KeyPressAdapter extends KeyAdapter
{
   private Set<Integer> _keys = new HashSet<Integer>(3);
   private KeyListener _listener;
   
   public KeyPressAdapter(KeyListener listener, int... keys)
   {
      _listener = listener;
      for (int key : keys) {
         _keys.add(key);
      }
   }

   public void keyTyped(KeyEvent e)
   {
      if (_keys.contains(e.getKeyCode())) {
         _listener.keyTyped(e);
      }
   }

   public void keyPressed(KeyEvent e)
   {
      if (_keys.contains(e.getKeyCode())) {
         _listener.keyPressed(e);
      }
   }

   public void keyReleased(KeyEvent e)
   {
      if (_keys.contains(e.getKeyCode())) {
         _listener.keyReleased(e);
      }
   }
}
