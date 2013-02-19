package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import javax.xml.namespace.QName;

public enum Namespace {
  XML("xml", "http://www.w3.org/XML/1998/namespace"),
  AWT("awt", "http://xml.levigo.org/ns/tools/inspection/awt"),
  JAVA("java", "http://xml.levigo.org/ns/tools/inspection/java"),
  SWING("swing", "http://xml.levigo.org/ns/tools/inspection/swing"),
  JADICE("jad", "http://xml.levigo.org/ns/tools/inspection/jadice-documentplatform");

  private final String namespaceURI;
  private final String prefix;

  private Namespace(String prefix, String namespaceURI) {
    this.prefix = prefix;
    this.namespaceURI = namespaceURI;
  }

  public QName createQName(String localName) {
    return new QName(namespaceURI, localName, prefix);
  }

  public String getNamespaceURI() {
    return namespaceURI;
  }

  public String getPrefix() {
    return prefix;
  }
}