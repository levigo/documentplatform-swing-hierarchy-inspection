package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import javax.swing.JMenu;
import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;

public class JMenuContentsMarshallerModule implements TreeContentMarshallingModule {

  @Override
  public QName getTagName(TreePath path) {
    return null;
  }

  @Override
  public void inspect(TreePath path, MarshallerCallback state) {
    if (path.getLastPathComponent() instanceof JMenu) {
      state.traverseChild(((JMenu) path.getLastPathComponent()).getPopupMenu());
    }
  }

  @Override
  public void poulateAttributes(TreePath path, AttributeCallback callback) {
  }

}
