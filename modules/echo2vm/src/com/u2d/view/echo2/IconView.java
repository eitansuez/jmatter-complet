package com.u2d.view.echo2;

import nextapp.echo2.app.*;
import com.u2d.view.ComplexEView;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import javax.swing.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 29, 2006
 * Time: 5:17:52 PM
 */
public class IconView extends Label implements ComplexEView
{
   protected ComplexEObject _ceo;
//   private transient CommandsContextMenuView _cmdsView = new CommandsContextMenuView();
//   private MouseListener _defaultActionListener;

   public IconView()
   {
//      setHorizontalAlignment(SwingConstants.CENTER);
//      setVerticalAlignment(SwingConstants.TOP);
//      setHorizontalTextPosition(SwingConstants.CENTER);
//      setVerticalTextPosition(SwingConstants.BOTTOM);
      setTextPosition(new Alignment(Alignment.DEFAULT, Alignment.BOTTOM));
//      setAlignmentX(0.5f);
//      setAlignmentY(0.5f);
//      setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
//      setBreakPosition(15);
   }

      public void bind(ComplexEObject ceo)
      {
         _ceo = ceo;
         _ceo.addPropertyChangeListener(this);
         _ceo.addChangeListener(this);

//         _cmdsView.bind(ceo, this);

         setText(_ceo.title().toString());
         // this mechanism of getting an image reference is discouraged / inefficient.. TODO: revisit
         setIcon(new ResourceImageReference(_ceo.iconLgResourceRef()));
//         setIcon(_ceo.iconLg());

         // TODO: NullComplexEObject should have its own iconview.
         //  i shouldn't have to put conditions in like this..
//         if (_ceo instanceof NullComplexEObject)
//         {
//            setTransferHandler(new DropTargetHandler());
//         }
//         else
//         {
//            setTransferHandler(new EOTransferHandler(this));  // setup as drag source
//         }

//         Command defaultCmd = _ceo.defaultCommand();
//         CommandAdapter defaultAction = new CommandAdapter(defaultCmd, _ceo, this);
//         _defaultActionListener = UIUtils.doubleClickActionListener(defaultAction);
//         addMouseListener(_defaultActionListener);
      }

      public void detach()
      {
         _ceo.removeChangeListener(this);
         _ceo.removePropertyChangeListener(this);

//         _cmdsView.detach();
//         removeMouseListener(_defaultActionListener);
      }

      public EObject getEObject() { return _ceo; }

      public void propertyChange(PropertyChangeEvent evt)
      {
         if ("icon".equals(evt.getPropertyName()))
         {
            ImageReference imgRef = new AwtImageReference(((ImageIcon)_ceo.iconLg()).getImage());
            setIcon(imgRef);
//            setIcon(_ceo.iconLg());
         }
      }

      public void stateChanged(javax.swing.event.ChangeEvent evt)
      {
         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  setText(_ceo.title().toString());
               }
            });
      }

//      private Insets _insets = new Insets(3, 4, 3, 4);
//      public Insets getInsets() { return _insets; }

      public boolean isMinimized() { return true; }

   }
