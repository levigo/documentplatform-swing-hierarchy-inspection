package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import java.awt.Component;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class ComponentTreeMarshaller {

  private static final class AttributeCallbackImpl implements AttributeCallback {
    private final ProcessingState state;

    private AttributeCallbackImpl(ProcessingState state) {
      this.state = state;
    }

    @Override
    public void addAttribute(QName name, String value) {
      try {
        state.writer.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), value);
      } catch (final XMLStreamException e) {
        throw new RuntimeException("failed to write xml attribute (name:" + name + ", value:" + value + ")", e);
      }
    }
  }

  protected static class ElementReference {
    private final Object target;

    public ElementReference(Object target) {
      super();
      this.target = target;
    }

    public Object getTarget() {
      return target;
    }
  }

  private static final class MarshallerCallbackImpl implements MarshallerCallback {
    private final List<Object> children;

    private MarshallerCallbackImpl(List<Object> children) {
      this.children = children;
    }

    @Override
    public void referenceChild(Object child) {
      children.add(new ElementReference(child));
    }

    @Override
    public void traverseChild(Object child) {
      children.add(child);
    }
  }

  protected static final class ProcessingState {
    protected final XMLStreamWriter writer;
    protected final Set<String> visitedIds;

    public ProcessingState(XMLStreamWriter writer) {
      super();
      this.writer = writer;
      visitedIds = new HashSet<String>();
    }

    public boolean addVisited(String id) {
      return visitedIds.add(id);
    }

    public boolean visited(String id) {
      return visitedIds.contains(id);
    }
  }

  private final List<TreeContentMarshallingModule> helpers;

  public ComponentTreeMarshaller() {
    helpers = new ArrayList<TreeContentMarshallingModule>();
  }

  public void addModule(TreeContentMarshallingModule helper) {
    if (helpers.contains(helper))
      throw new IllegalArgumentException("This helper has already been registered: " + helper);
    helpers.add(helper);
  }

  private QName getTagName(TreePath path) {

    if (path.getLastPathComponent() instanceof ElementReference) {
      // replacing element reference if it is the last path component
      path = path.getParentPath().pathByAddingChild(((ElementReference) path.getLastPathComponent()).getTarget());
    }

    for (final TreeContentMarshallingModule helper : helpers) {
      final QName tagName = helper.getTagName(path);
      if (tagName != null)
        return tagName;
    }

    return Namespace.JAVA.createQName("Object");
  }

  protected String getXmlId(Object element) {
    return Integer.toHexString(System.identityHashCode(element));
  }

  public void marshal(Component rootComponent, Result result) {
    if (rootComponent == null)
      throw new IllegalArgumentException("rootComponent must not be null");

    if (result == null)
      throw new IllegalArgumentException("result must not be null");

    try {

      final XMLOutputFactory factory = XMLOutputFactory.newFactory();
      final XMLStreamWriter w = factory.createXMLStreamWriter(result);

      w.writeStartDocument();
      w.writeStartElement("ComponentTree");

      // write the standard namespaces, regardless whether they will be used or not.
      for (final Namespace ns : Namespace.values()) {
        w.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
      }

      traverse(new TreePath(rootComponent), new ProcessingState(w));

      w.writeEndElement();
      w.writeEndDocument();
      w.close();
    } catch (final Exception e) {
      throw new RuntimeException("Component tree marshalling failed.", e);
    }
  }

  public String marshalToString(Component rootComponent) {

    final StringWriter sw = new StringWriter();
    final StreamResult result = new StreamResult(sw);

    marshal(rootComponent, result);

    return sw.toString();


  }

  private void traverse(TreePath path, final ProcessingState state) throws XMLStreamException {

    final Object element = path.getLastPathComponent();
    final String xmlId = getXmlId(element);
    final Class<? extends Object> elementType;
    final boolean reference = element instanceof ElementReference;
    if (reference) {
      elementType = ((ElementReference) element).getTarget().getClass();
    } else {
      elementType = element.getClass();
    }

    final QName tagName = getTagName(path);

    // if the element has either already been visited or is a ElementReference, write the reference
    // to the document.
    if (state.visited(xmlId) || reference) {
      state.writer.writeStartElement(tagName.getPrefix(), tagName.getLocalPart(), tagName.getNamespaceURI());
      state.writer.writeAttribute("type", elementType.getName());
      state.writer.writeAttribute("ref", xmlId);
      state.writer.writeEndElement();
      return;
    }
    state.addVisited(xmlId);


    state.writer.writeStartElement(tagName.getPrefix(), tagName.getLocalPart(), tagName.getNamespaceURI());
    state.writer.writeAttribute("type", elementType.getName());
    state.writer.writeAttribute("xml", Namespace.XML.getNamespaceURI(), "id", xmlId);

    final List<Object> children = new ArrayList<Object>();

    for (final TreeContentMarshallingModule helper : helpers) {

      // let the marshalling helper provide additional attributes
      helper.poulateAttributes(path, new AttributeCallbackImpl(state));

      // start inspection of the element. This allows the marshalling helper to detect child
      // elements.
      helper.inspect(path, new MarshallerCallbackImpl(children));
    }

    for (final Object child : children) {
      traverse(path.pathByAddingChild(child), state);
    }


    state.writer.writeEndElement();
  }
}
