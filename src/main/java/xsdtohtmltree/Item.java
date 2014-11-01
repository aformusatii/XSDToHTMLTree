package xsdtohtmltree;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSTypeDefinition;

public class Item {
    private XSElementDecl element;
    private String elementName;
    private String typeName;

    public XSElementDecl getElement() {
        return element;
    }

    public void setElement(XSElementDecl element) {
        this.element = element;
        this.elementName = element.getName();

        XSTypeDefinition fType = element.getTypeDefinition();

        if (fType instanceof XSComplexTypeDecl) {
            this.typeName = ((XSComplexTypeDecl) fType).getTypeName();
        } else if (fType instanceof XSSimpleTypeDecl) {
            this.typeName = ((XSSimpleTypeDecl) fType).getTypeName();
        }
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean equals(Item item) {
        return this.getElementName().equals(item.getElementName()) && this.getTypeName().equals(item.getTypeName());
    }
}
