/**
 * <pre>
 * Copyright (c) 1995-2013 levigo holding gmbh. All Rights Reserved.
 * 
 * This software is the proprietary information of levigo holding gmbh.
 * Use is subject to license terms.
 * </pre>
 */
package com.levigo.os.utils.swing.hierarchy.inspection.component;

import javax.swing.JMenu;
import javax.swing.tree.TreePath;

import org.jadice.util.glazedlists.EventList;
import org.jadice.util.glazedlists.GlazedLists;
import org.jadice.util.swing.flextree.TreeContentProvider;

public class JMenuTreeContentProvider implements TreeContentProvider {

  @Override
  public EventList<? extends Object> getChildren(TreePath treePath) {
    final JMenu menu = (JMenu) treePath.getLastPathComponent();

    return GlazedLists.eventListOf(menu.getPopupMenu());
  }

  @Override
  public boolean hasChildren(TreePath treePath) {
    return treePath.getLastPathComponent() instanceof JMenu;
  }

}
