package com.levigo.os.utils.swing.hierarchy.inspection.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import com.levigo.util.swing.flextree.TreeLabelProvider;

public class ComponentTreeLabelProvider implements TreeLabelProvider {

  @Override
  public void updateLabel(TreePath path, StyledDocument doc) throws BadLocationException {

    if (path.getLastPathComponent() instanceof Component) {
      Component comp = (Component) path.getLastPathComponent();

      String classname = comp.getClass().getName();
      // we're using classname.lastIndexOf() instead of getSimpleName, as getSimpleName will return
      // nothing in case of an anonymous inner class.
      doc.insertString(doc.getLength(), classname.substring(classname.lastIndexOf('.') + 1) + " ", null);

      final SimpleAttributeSet lightGray = new SimpleAttributeSet(
          doc.getCharacterElement(doc.getLength()).getAttributes());

      StyleConstants.setForeground(lightGray, new Color(0xaa, 0xaa, 0xaa));

      doc.insertString(doc.getLength(), comp.getClass().getPackage().getName(), lightGray);

    }
  }

}
