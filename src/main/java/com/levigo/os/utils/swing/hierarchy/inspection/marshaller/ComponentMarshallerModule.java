package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;

public class ComponentMarshallerModule implements TreeContentMarshallingModule {

  @Override
  public QName getTagName(TreePath path) {
    if (path.getLastPathComponent() instanceof JComponent) {
      return Namespace.SWING.createQName("JComponent");
    } else if (path.getLastPathComponent() instanceof Component) {
      return Namespace.AWT.createQName("Component");
    }

    return null;
  }

  @Override
  public void inspect(TreePath path, MarshallerCallback state) {
    if (path.getLastPathComponent() instanceof Container) {
      final Container container = (Container) path.getLastPathComponent();
      for (final Component component : container.getComponents()) {
        state.traverseChild(component);
      }
    }
  }

  @Override
  public void poulateAttributes(TreePath path, AttributeCallback callback) {
    if (path.getLastPathComponent() instanceof Component) {
      final Component c = (Component) path.getLastPathComponent();
      final String name = c.getName();
      if (name != null)
        callback.addAttribute(Namespace.AWT.createQName("name"), name);
    }

  }

}
