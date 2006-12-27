package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import javax.swing.event.ListDataEvent;
import java.util.Iterator;
import org.wings.SPanel;
import org.wings.SFlowDownLayout;
import org.wings.SComponent;

/**
 * @author Eitan Suez
 */
public class ListView extends SPanel implements ListEView
{
   protected AbstractListEO _leo;

   public ListView(AbstractListEO leo)
   {
      _leo = leo;
      setLayout(new SFlowDownLayout());
      addItems();
      _leo.addListDataListener(this);
   }

   protected void addItems()
   {
      Iterator itr = _leo.getItems().iterator();
      ComplexEObject ceo = null;
      while (itr.hasNext())
      {
         ceo = (ComplexEObject) itr.next();
         add((SComponent) ceo.getListItemView());
      }
   }

   public void contentsChanged(ListDataEvent evt)
   {
      removeItems();
      addItems();
   }
   public void intervalAdded(final ListDataEvent evt)
   {
      AbstractListEO source = (AbstractListEO) evt.getSource();
      ComplexEObject ceo = (ComplexEObject) source.getElementAt(evt.getIndex0());
      EView view = ceo.getListItemView();
      add((SComponent) view, evt.getIndex0());
   }
   public void intervalRemoved(final ListDataEvent evt)
   {
      int index = evt.getIndex0();
      if (getComponent(index) instanceof EView)
         ((EView) getComponent(index)).detach();
      removeItemAtIndex(index);
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt) {}

   public EObject getEObject() { return _leo; }


   public void detach()
   {
      _leo.removeListDataListener(this);
      detachItems();
   }

   private void detachItems()
   {
      SComponent item = null;
      for (int i=0; i<getComponentCount(); i++)
      {
         item = getComponent(i);
         if (item instanceof EView)
            ((EView) item).detach();
      }
   }

   protected void removeItems()
   {
     detachItems();
     removeAll();
   }

   private void removeItemAtIndex(int index)
   {
      SComponent c = getComponent(index);
      if (c instanceof EView)
        ((EView) c).detach();
      remove(index);
   }

   public boolean isMinimized() { return false; }

}
