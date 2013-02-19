package com.levigo.os.utils.swing.hierarchy.inspection;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.levigo.os.utils.swing.hierarchy.inspection.component.ComponentNameLabelProvider;
import com.levigo.os.utils.swing.hierarchy.inspection.component.ComponentTreeLabelProvider;
import com.levigo.os.utils.swing.hierarchy.inspection.component.ContainerTreeContentProvider;
import com.levigo.os.utils.swing.hierarchy.inspection.component.JMenuTreeContentProvider;


public class DefaultSwingHierarchyInspectionPanel extends SwingHierarchyInspectionPanel {


  private static final long serialVersionUID = 1L;

  public DefaultSwingHierarchyInspectionPanel(Component component) {
    super(component);

    // configure the default tree modules
    tree.addModule(new GenericTreeIconProvider());
    tree.addModule(new ContainerTreeContentProvider());
    tree.addModule(new JMenuTreeContentProvider());
    tree.addModule(new ComponentTreeLabelProvider());
    tree.addModule(new ComponentNameLabelProvider());
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
        }

        final JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new DefaultSwingHierarchyInspectionPanel(f));
        f.setSize(300, 600);
        f.setVisible(true);
      }
    });
  }
}
