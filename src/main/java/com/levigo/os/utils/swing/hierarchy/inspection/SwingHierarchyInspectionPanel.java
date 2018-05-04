package com.levigo.os.utils.swing.hierarchy.inspection;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jadice.util.swing.flextree.FlexibleTree;

public class SwingHierarchyInspectionPanel extends JPanel {

  private static final long serialVersionUID = -1L;

  protected final FlexibleTree tree;

  public SwingHierarchyInspectionPanel(Component component) {

    setLayout(new BorderLayout());

    tree = new FlexibleTree();
    tree.setShowRootHandles(true);
    tree.setInput(component);

    add(new JScrollPane(tree), BorderLayout.CENTER);
  }


  public FlexibleTree getTree() {
    return tree;
  }

}
