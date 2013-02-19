package com.levigo.os.utils.swing.hierarchy.inspection.jadice;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import com.levigo.os.utils.swing.hierarchy.inspection.util.ContextInspector;
import com.levigo.util.swing.action.Context;
import com.levigo.util.swing.action.Context.Ancestors;
import com.levigo.util.swing.action.Context.Children;
import com.levigo.util.swing.flextree.TreeLabelProvider;

public class ContextAggregationModeLabelProvider implements TreeLabelProvider {

  @Override
  public void updateLabel(TreePath treePath, StyledDocument doc) throws BadLocationException {

    if (!ContextInspector.INSTANCE.isEnabled())
      return;

    if (treePath.getLastPathComponent() instanceof Context) {
      Context ctx = (Context) treePath.getLastPathComponent();

      Children childAggregation = ContextInspector.INSTANCE.getChildAggregation(ctx);
      Ancestors ancestorAggregation = ContextInspector.INSTANCE.getAncestorAggregation(ctx);

      final SimpleAttributeSet lightGray = new SimpleAttributeSet(
          doc.getCharacterElement(doc.getLength()).getAttributes());
      final SimpleAttributeSet darkGray = new SimpleAttributeSet(
          doc.getCharacterElement(doc.getLength()).getAttributes());

      StyleConstants.setForeground(lightGray, new Color(0xaa, 0xaa, 0xaa));
      StyleConstants.setForeground(darkGray, new Color(0x55, 0x55, 0x55));

      doc.insertString(doc.getLength(), " child aggregation: ", lightGray);
      doc.insertString(doc.getLength(), "" + childAggregation, darkGray);
      doc.insertString(doc.getLength(), " ancestor aggregation: ", lightGray);
      doc.insertString(doc.getLength(), "" + ancestorAggregation, darkGray);
    }

  }

}
