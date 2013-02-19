package com.levigo.os.utils.swing.hierarchy.inspection;

import java.awt.Component;

import com.levigo.os.utils.swing.hierarchy.inspection.jadice.ContextAggregationModeLabelProvider;
import com.levigo.os.utils.swing.hierarchy.inspection.jadice.ContextChildrenContentsProvider;
import com.levigo.os.utils.swing.hierarchy.inspection.jadice.ContextContentProvider;
import com.levigo.os.utils.swing.hierarchy.inspection.jadice.ContextContentsContentProvider;
import com.levigo.os.utils.swing.hierarchy.inspection.jadice.ContextOwnerContentProvider;

public class JadiceSwingHierarchyInspectionPanel extends DefaultSwingHierarchyInspectionPanel {

  private static final long serialVersionUID = 1L;

  public JadiceSwingHierarchyInspectionPanel(Component component) {
    super(component);

    // levigo utils swing context inspection
    tree.addModule(new ContextContentProvider());
    tree.addModule(new ContextOwnerContentProvider());
    tree.addModule(new ContextContentsContentProvider());
    tree.addModule(new ContextChildrenContentsProvider());
    tree.addModule(new ContextAggregationModeLabelProvider());
  }
}
