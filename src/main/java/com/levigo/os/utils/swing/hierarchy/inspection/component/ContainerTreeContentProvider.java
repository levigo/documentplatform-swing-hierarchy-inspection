package com.levigo.os.utils.swing.hierarchy.inspection.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Arrays;

import javax.swing.tree.TreePath;

import org.jadice.util.glazedlists.BasicEventList;
import org.jadice.util.glazedlists.EventList;
import org.jadice.util.swing.flextree.TreeContentProvider;

public class ContainerTreeContentProvider implements TreeContentProvider {

  @Override
  public EventList<? extends Object> getChildren(TreePath path) {

    final Container container = (Container) path.getLastPathComponent();

    final BasicEventList<Component> children = new BasicEventList<Component>();

    // add all existing elements in the container to the event list
    children.addAll(Arrays.asList(container.getComponents()));

    container.addContainerListener(new ContainerListener() {

      @Override
      public void componentRemoved(ContainerEvent e) {

        children.getReadWriteLock().writeLock().lock();
        try {
          children.remove(e.getChild());
        } finally {
          children.getReadWriteLock().writeLock().unlock();
        }
      }

      @Override
      public void componentAdded(ContainerEvent e) {

        children.getReadWriteLock().writeLock().lock();
        try {
          final Component[] components = e.getContainer().getComponents();
          int idx = -1;
          for (int i = 0; i < components.length; i++) {
            if (e.getChild() == components[i])
              idx = i;
          }
          if (idx == -1)
            children.add(e.getChild());
          else
            children.add(idx, e.getChild());
        } finally {
          children.getReadWriteLock().writeLock().unlock();
        }

      }
    });

    return children;

  }

  @Override
  public boolean hasChildren(TreePath path) {
    return path.getLastPathComponent() instanceof Container;
  }

}