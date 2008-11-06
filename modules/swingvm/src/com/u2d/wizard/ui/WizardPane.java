package com.u2d.wizard.ui;

import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.details.Wizard;
import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.u2d.view.EView;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.l2fprod.common.swing.PercentLayout;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 26, 2005
 * Time: 10:34:02 AM
 */
public class WizardPane extends JPanel
{
   private Wizard _wizard;
   private Step _step;

   private TitlePane _titlePane = new TitlePane();
   private CenterPane _centerPane;
   private NavPane _navPane = new NavPane();
   private StepsPane _stepsPane;


   public WizardPane(Wizard wizard)
   {
      _centerPane = new CenterPane(wizard.getContentPaneSize());
      _wizard = wizard;
      _step = wizard;

      _stepsPane = new StepsPane();

      setLayout(new BorderLayout());

      JPanel mainPane = new JPanel(new BorderLayout());
      mainPane.add(_titlePane, BorderLayout.PAGE_START);
      mainPane.add(_centerPane, BorderLayout.CENTER);

      add(_stepsPane, BorderLayout.LINE_START);
      add(mainPane, BorderLayout.CENTER);
      add(_navPane, BorderLayout.PAGE_END);

      setupActions();
      showCurrentView();
   }

   private void setupActions()
   {
      _navPane.setNextAction(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            if (!transferAndValidate())
               return;
            _step = _step.nextStep();
            showCurrentView();
         }
      });
      _navPane.setFinishAction(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            if (!transferAndValidate())
               return;
            _step = _step.nextStep();
            
            CloseableJInternalFrame.close(WizardPane.this);
            _centerPane.detach();
         }
      });

      _navPane.setPreviousAction(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _step = _step.previousStep();
            showCurrentView();
         }
      });
      _navPane.setCancelAction(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            CloseableJInternalFrame.close(WizardPane.this);
            _centerPane.detach();
         }
      });
   }

   private void showCurrentView()
   {
      _stepsPane.updateStep(_step);
      _titlePane.updateStep(_step);
      _centerPane.updateStep(_step);
      _navPane.updateStep(_step);
   }


   private boolean transferAndValidate()
   {
      JComponent currentView = _centerPane.getCurrentView();
      if ((currentView != null) && (currentView instanceof Editor))
      {
         Editor editor = (Editor) currentView;
         EObject eo = ((EView) currentView).getEObject();

         int errorCount = editor.transferValue();
         if (errorCount > 0)
         {
            String plural = (errorCount == 1) ? "" : "s";
            eo.fireValidationException("[Syntax errors in "+errorCount+" form field"+plural+".]", true);
            return false;
         }

         if (eo.field() == null)
         {
            errorCount = editor.validateValue();
         }
         else
         {
            errorCount = eo.field().validate(eo.parentObject());
         }
         
         if (errorCount > 0)
         {
            String plural = (errorCount == 1) ? "" : "s";
            eo.fireValidationException("["+errorCount+" validation error"+plural+".]", true);
            return false;
         }

         eo.fireValidationException("");  // reset any validation messages from last attempt
      }

      return true;
   }

   class StepsPane extends JPanel
   {
      StepLabel[] _labels = null;
      Font _plainFont, _boldFont;

      public StepsPane()
      {
         setBackgroundAndBorder();

         List flattenedSteps = _wizard.innerStep().steps();  // notice: excluding
                                                          // wizard wrapper steps
         _labels = new StepLabel[flattenedSteps.size()];
         setLayout(new PercentLayout(PercentLayout.VERTICAL, 1));

         StepLabel label = null;
         Step step = null;
         for (int i=0; i<flattenedSteps.size(); i++)
         {
            step = (Step) flattenedSteps.get(i);
            label = new StepLabel(step, i+1);
            _labels[i] = label;
            add(label);
         }

         _plainFont = label.getFont();
         _boldFont = _plainFont.deriveFont(Font.BOLD);
      }

      private void setBackgroundAndBorder()
      {
         Border border = BorderFactory.createTitledBorder(_wizard.compositeTitle());

         Border padding = BorderFactory.createEmptyBorder(7, 7, 7, 7);
         border = BorderFactory.createCompoundBorder(padding, border);
         border = BorderFactory.createCompoundBorder(border, padding);

         setBorder(border);
      }

      StepLabel _currentStepLabel;

      public void updateStep(Step step)
      {
         if (_currentStepLabel != null)
         {
            _currentStepLabel.normalStyle();
         }

         int index = _wizard.stepNumber();
         if (index > 0)
         {
            _currentStepLabel = _labels[index-1];
            _currentStepLabel.highlight();
         }
      }

      class StepLabel extends JLabel
      {
         Color originalBg = getBackground();

         public StepLabel(Step step, int stepNum)
         {
            super();
            setOpaque(true);

            setText(stepNum + ". " + step.title());
         }

         public void highlight()
         {
            setFont(_boldFont);
            setBackground(Color.white);
         }

         public void normalStyle()
         {
            setFont(_plainFont);
            setBackground(originalBg);
         }
      }
   }

}
