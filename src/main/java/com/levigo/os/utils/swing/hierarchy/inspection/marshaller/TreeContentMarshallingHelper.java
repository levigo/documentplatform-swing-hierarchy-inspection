package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import javax.swing.tree.TreePath;

public interface TreeContentMarshallingHelper {
  String getTagName(TreePath path);

  void inspect(TreePath path, MarshallerCallback state);
}
