package com.levigo.os.utils.swing.hierarchy.inspection.jadice;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import com.levigo.os.utils.swing.hierarchy.inspection.util.ContextInspector;
import com.levigo.util.base.Disposable;
import com.levigo.util.swing.action.Context;
import com.levigo.util.swing.action.ContextListener;
import com.levigo.util.swing.flextree.DynamicTreeModule;
import com.levigo.util.swing.flextree.FlexibleTree;
import com.levigo.util.swing.flextree.TreeLabelProvider;

/**
 * For each {@link Context} element displayed in a {@link FlexibleTree}, this class enriches the
 * label with information which indicates whether or not this {@link Context} is currently active.
 */
public class ContextActiveStateLabelProvider implements TreeLabelProvider, DynamicTreeModule {


  @Override
  public void updateLabel(TreePath treePath, StyledDocument doc) throws BadLocationException {

    if (!ContextInspector.INSTANCE.isEnabled())
      return;

    if (treePath.getLastPathComponent() instanceof Context) {
      Context ctx = (Context) treePath.getLastPathComponent();

      final SimpleAttributeSet sas = new SimpleAttributeSet(doc.getCharacterElement(doc.getLength()).getAttributes());
      String labelText;
      boolean isActive = ctx.isActive();
      if (isActive) {
        StyleConstants.setForeground(sas, new Color(0xff, 0x22, 0x22));
        labelText = " (active)";
      } else {
        StyleConstants.setForeground(sas, new Color(0x22, 0x22, 0xff));
        labelText = " (inactive)";
      }

      doc.insertString(doc.getLength(), labelText, sas);
    }

  }

  @Override
  public Disposable registerChangeListener(TreePath treePath, ChangeListener callback) {
    if (treePath.getLastPathComponent() instanceof Context) {
      Context ctx = (Context) treePath.getLastPathComponent();
      return new ContextChangedCallback(ctx, callback);
    } else {
      return null;
    }
  }

  private static class ContextChangedCallback implements Disposable, ContextListener {

    private final Context ctx;
    private final ChangeListener callback;

    public ContextChangedCallback(Context ctx, ChangeListener callback) {
      this.ctx = ctx;
      this.callback = callback;
      ctx.addContextChangeListener(this);
    }


    @Override
    public void dispose() {
      ctx.removeContextChangeListener(this);
    }


    @Override
    public void contextChanged(Context source) {
      callback.nodeRefreshRequired();
    }

  }

}
