package com.levigo.os.utils.swing.hierarchy.inspection.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import com.levigo.util.swing.flextree.TreeLabelProvider;

public class ComponentNameLabelProvider implements TreeLabelProvider {

  @Override
  public void updateLabel(TreePath treePath, StyledDocument doc) throws BadLocationException {

    if (treePath.getLastPathComponent() instanceof Component) {

      final Component comp = (Component) treePath.getLastPathComponent();

      final String name = comp.getName();


      final SimpleAttributeSet darkGray = new SimpleAttributeSet(
          doc.getCharacterElement(doc.getLength()).getAttributes());

      StyleConstants.setForeground(darkGray, new Color(0x55, 0x55, 0x55));

      if (name != null) {
        doc.insertString(doc.getLength(), " [name: " + name + "]", darkGray);
      }
    }

  }

}
