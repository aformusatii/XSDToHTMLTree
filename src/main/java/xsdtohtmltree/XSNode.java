package xsdtohtmltree;

import java.util.List;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.xs.XSObject;

public class XSNode {
    
    private XSObject element;
    private String elementName;
    private String typeName;
    private boolean parentStart;
    private boolean parentEnd;
    private boolean child;
    private boolean other;
    private List<XSNode> parents;

    private XSNode() {
    }
    
    public XSObject getElement() {
        return element;
    }

    public String getElementName() {
        return elementName;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isParentStart() {
        return parentStart;
    }

    public boolean isParentEnd() {
        return parentEnd;
    }

    public boolean isChild() {
        return child;
    }

    public boolean isOther() {
        return other;
    }

    public List<XSNode> getParents() {
        return parents;
    }

    public boolean equals(XSNode item) {
        return this.getElementName().equals(item.getElementName()) && this.getTypeName().equals(item.getTypeName());
    }
    
    public static XSNode createParentStart(XSElementDecl element, XSComplexTypeDecl type, List<XSNode> parents) {
        XSNode node = new XSNode();
        node.parentStart = true;
        node.element = element;
        node.elementName = element.getName();
        node.typeName = type.getTypeName();
        node.parents = parents;
        return node;
    }
    
    public static XSNode createParentEnd(XSElementDecl element, XSComplexTypeDecl type, List<XSNode> parents) {
        XSNode node = new XSNode();
        node.parentEnd = true;
        node.element = element;
        node.elementName = element.getName();
        node.typeName = type.getTypeName();
        node.parents = parents;
        return node;
    }
    
    public static XSNode createChild(XSElementDecl element, XSSimpleTypeDecl type, List<XSNode> parents) {
        XSNode node = new XSNode();
        node.child = true;
        node.element = element;
        node.elementName = element.getName();
        node.typeName = type.getTypeName();
        node.parents = parents;
        return node;
    }
    
    public static XSNode createOther(XSParticleDecl element, XSWildcardDecl type, List<XSNode> parents) {
        XSNode node = new XSNode();
        node.other = true;
        node.element = element;
        node.elementName = element.getName();
        node.typeName = type.getClass().getName();
        node.parents = parents;
        return node;
    }
}
