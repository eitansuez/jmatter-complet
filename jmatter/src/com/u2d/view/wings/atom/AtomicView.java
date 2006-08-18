package com.u2d.view.wings.atom;

import com.u2d.view.AtomicEView;
import com.u2d.view.wings.CardPanel;
import com.u2d.view.wings.list.CommandsContextMenuView;
import com.u2d.model.*;
import javax.swing.event.ChangeEvent;
//import java.awt.event.FocusAdapter;
//import java.awt.event.FocusEvent;
import org.wings.SComponent;
//import org.wings.STextComponent;

/**
 * Date: Jun 8, 2005
 * Time: 1:05:45 PM
 *
 * @author Eitan Suez
 */
public class AtomicView extends CardPanel implements AtomicEView, Editor
{
   protected AtomicEObject _eo;
   protected AtomicRenderer _renderer;
   protected AtomicEditor _editor;
//   protected FocusAdapter _focusAdapter;
   protected transient CommandsContextMenuView _cmdsView;

   public AtomicView()
   {
//      _focusAdapter = new FocusAdapter()
//      {
//         public void focusLost(FocusEvent e)
//         {
//            int errors = _editor.bind(_eo);
//            if (errors == 0)
//               _eo.fireValidationException(""); // reset any previous
//         }
//      };
      _cmdsView = new CommandsContextMenuView();
   }

   public void bind(AtomicEObject eo)
   {
      _eo = eo;
      _renderer = eo.getRenderer();
      _editor = eo.getEditor();

      SComponent rendererComponent = (SComponent) _renderer;
      SComponent editorComponent = (SComponent) _editor;

//      rendererComponent.setOpaque(false);
//      if (!isTextComponent(editorComponent))
//         editorComponent.setOpaque(false);

      add(rendererComponent, "view");
      add(editorComponent, "edit");

      setEditable(false);  // start out read-only by default

      _cmdsView.bind(_eo, this);

      if (_eo.parentObject() != null)
         _eo.parentObject().addChangeListener(this);
      _eo.addChangeListener(this);

      stateChanged(null);

//      editorComponent.addFocusListener(_focusAdapter);
   }


   public void detach()
   {
      _eo.removeChangeListener(this);
      if (_eo.parentObject() != null)
         _eo.parentObject().removeChangeListener(this);

//      ((SComponent) _editor).removeFocusListener(_focusAdapter);

      _cmdsView.detach();
   }


//   private boolean isTextComponent(SComponent editorComponent)
//   {
//      return (editorComponent instanceof STextComponent);
//   }

   public void stateChanged(ChangeEvent evt)
   {
      _renderer.render(_eo);
      _editor.render(_eo);
   }

   public int transferValue()
   {
      int errorCount = _editor.bind(_eo);
      _renderer.render(_eo);
      return errorCount;
   }

   private boolean _editable = false;
   public void setEditable(boolean editable)
   {
      _editable = editable;
      show((editable) ? "edit" : "view");
   }
   public boolean isEditable() { return _editable; }

   public EObject getEObject() { return _eo; }

   public AtomicEditor getEditor() { return _editor; }

}
