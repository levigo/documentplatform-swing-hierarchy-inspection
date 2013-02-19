package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import javax.xml.namespace.QName;

public interface AttributeCallback {

  void addAttribute(QName name, String value);
}
