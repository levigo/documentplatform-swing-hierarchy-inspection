package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import java.awt.Component;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class ComponentTreeMarshaller {

  private final List<TreeContentMarshallingHelper> helpers;

  public ComponentTreeMarshaller() {
    helpers = new ArrayList<TreeContentMarshallingHelper>();
  }

  public void addMarshallingHelper(TreeContentMarshallingHelper helper) {
    if (helpers.contains(helper))
      throw new IllegalArgumentException("This helper has already been registered: " + helper);
    helpers.add(helper);
  }

  protected static final class ProcessingState {
    protected final XMLStreamWriter writer;
    protected final Set<String> visitedIds;

    public ProcessingState(XMLStreamWriter writer) {
      super();
      this.writer = writer;
      visitedIds = new HashSet<String>();
    }

    public boolean visited(String id) {
      return visitedIds.contains(id);
    }

    public boolean addVisited(String id) {
      return visitedIds.add(id);
    }
  }

  public String marshalToString(Component rootComponent) {


    try {

      Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      DOMResult result = new DOMResult(doc);
      XMLOutputFactory factory = XMLOutputFactory.newFactory();
      XMLStreamWriter w = factory.createXMLStreamWriter(result);

      w.writeStartDocument();
      w.writeStartElement("ComponentTree");

      traverse(new TreePath(rootComponent), new ProcessingState(w));

      w.writeEndElement();
      w.writeEndDocument();

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      StringWriter sw = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(sw));


      return sw.toString();
    } catch (Exception e) {
      throw new RuntimeException("Component tree marshalling failed.", e);
    }


  }

  private static final class MarshallerCallbackImpl implements MarshallerCallback {
    private final List<Object> children;

    private MarshallerCallbackImpl(List<Object> children) {
      this.children = children;
    }

    @Override
    public void traverseChild(Object child) {
      children.add(child);
    }

    @Override
    public void referenceChild(Object child) {
      children.add(new ElementReference(child));
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

  private void traverse(TreePath path, ProcessingState state) throws XMLStreamException {

    String xmlId = Integer.toHexString(System.identityHashCode(path.getLastPathComponent()));
    Class<? extends Object> elementType;
    final boolean reference = path.getLastPathComponent() instanceof ElementReference;
    if (reference) {
      elementType = ((ElementReference) path.getLastPathComponent()).getTarget().getClass();
    } else {
      elementType = path.getLastPathComponent().getClass();
    }

    String tagName = getTagName(path);

    // if the element has either already been visited or is a ElementReference, write the reference
    // to the document.
    if (state.visited(xmlId) || reference) {
      state.writer.writeStartElement(tagName);
      state.writer.writeAttribute("type", elementType.getName());
      state.writer.writeAttribute("ref", xmlId);
      state.writer.writeEndElement();
      return;
    }
    state.addVisited(xmlId);


    state.writer.writeStartElement(tagName);
    state.writer.writeAttribute("type", elementType.getName());
    state.writer.writeAttribute("xml", "http://www.w3.org/XML/1998/namespace", "id", xmlId);

    final List<Object> children = new ArrayList<Object>();

    for (TreeContentMarshallingHelper helper : helpers) {
      helper.inspect(path, new MarshallerCallbackImpl(children));
    }

    for (Object child : children) {
      TreePath childPath = path.pathByAddingChild(child);
      traverse(childPath, state);
    }


    state.writer.writeEndElement();
  }

  private String getTagName(TreePath path) {

    if (path.getLastPathComponent() instanceof ElementReference) {
      // replacing element reference if it is the last path component
      path = path.getParentPath().pathByAddingChild(((ElementReference) path.getLastPathComponent()).getTarget());
    }

    for (TreeContentMarshallingHelper helper : helpers) {
      String tagName = helper.getTagName(path);
      if (tagName != null)
        return tagName;
    }

    if (path.getLastPathComponent() instanceof JComponent) {
      return "JComponent";
    } else if (path.getLastPathComponent() instanceof Component) {
      return "Component";
    }

    return "Object";

  }
}
