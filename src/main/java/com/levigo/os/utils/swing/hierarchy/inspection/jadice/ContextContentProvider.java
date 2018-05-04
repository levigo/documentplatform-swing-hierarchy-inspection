package com.levigo.os.utils.swing.hierarchy.inspection.jadice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.jadice.util.glazedlists.BasicEventList;
import org.jadice.util.glazedlists.EventList;
import org.jadice.util.swing.action.context.Context;
import org.jadice.util.swing.flextree.TreeContentProvider;
import org.jadice.util.swing.flextree.TreeLabelProvider;

public class ContextContentProvider implements TreeContentProvider, TreeLabelProvider {

  public static final class Updater implements PropertyChangeListener {
    private final BasicEventList<Context> contexts;

    public Updater(BasicEventList<Context> contexts) {
      this.contexts = contexts;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

      contexts.getReadWriteLock().writeLock().lock();
      try {
        doUpdate(evt.getNewValue());
      } finally {
        contexts.getReadWriteLock().writeLock().unlock();
      }

    }

    private void doUpdate(Object newValue) {
      if (newValue == null) {
        // it is a remove. As we will only have a single element in the context list, it is save to
        // simply clear it.

        contexts.clear();

      } else if (newValue instanceof Context) {

        // it is either an add or replace.

        if (contexts.size() > 0) {
          // replace
          contexts.set(0, (Context) newValue);
        } else {
          // add
          contexts.add((Context) newValue);
        }

      }
    }
  }

  @Override
  public void updateLabel(TreePath path, StyledDocument doc) throws BadLocationException {
    if (path.getLastPathComponent() instanceof Context) {

      doc.insertString(doc.getLength(), "Context", null);

    }
  }

  @Override
  public EventList<? extends Object> getChildren(TreePath path) {

    JComponent component = (JComponent) path.getLastPathComponent();

    final BasicEventList<Context> contexts = new BasicEventList<Context>();

    Updater updater = new Updater(contexts);
    updater.doUpdate(component.getClientProperty(Context.PROPERTY_KEY));
    component.addPropertyChangeListener(Context.PROPERTY_KEY, updater);

    return contexts;


  }

  @Override
  public boolean hasChildren(TreePath path) {
    return path.getLastPathComponent() instanceof JComponent;
  }

}
