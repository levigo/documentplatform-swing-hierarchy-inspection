package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;

public interface TreeContentMarshallingModule {
  QName getTagName(TreePath path);

  void inspect(TreePath path, MarshallerCallback state);

  void poulateAttributes(TreePath path, AttributeCallback callback);
}
