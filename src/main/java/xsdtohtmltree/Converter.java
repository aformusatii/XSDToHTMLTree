package xsdtohtmltree;

import static xsdtohtmltree.Properties.getProp;
import static xsdtohtmltree.Properties.Keys.HTML_TEMPALTE_FILE;
import static xsdtohtmltree.Properties.Keys.ROOT_XSD_FILE;
import static xsdtohtmltree.Properties.Keys.OUTPUT_DIRECTORY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

public class Converter {

    public static void run() throws Exception {
        new Converter().convert();
    }

    public void convert() throws Exception {
        StringBuilder content = new StringBuilder();
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        XSImplementation impl = (XSImplementation) registry.getDOMImplementation("XS-Loader");
        XSLoader schemaLoader = impl.createXSLoader(null);
        DOMConfiguration config = schemaLoader.getConfig();
        config.setParameter("validate", Boolean.TRUE);
        XSModel model = schemaLoader.loadURI(getProp(ROOT_XSD_FILE));

        if (model != null) {
            XSNamedMap map = model.getComponents(XSConstants.ELEMENT_DECLARATION);
            if (map.getLength() != 0) {
                for (int i = 0; i < map.getLength(); i++) {
                    XSObject item = map.item(i);
                    traversItem(item, null, content);
                }
            }
        }

        String outputFile = getProp(OUTPUT_DIRECTORY) + File.separator + FilenameUtils.removeExtension(new File(getProp(ROOT_XSD_FILE)).getName()) + ".html";
        System.out.println("=> Output file: " + new File(outputFile).getAbsolutePath());

        generateOutputFile(getProp(HTML_TEMPALTE_FILE), outputFile, content);
    }

    private void traversItem(XSObject item, List<Item> parents, StringBuilder content) {
        if (item instanceof XSElementDecl) {
            XSElementDecl element = (XSElementDecl) item;
            XSTypeDefinition fType = element.getTypeDefinition();
            if (fType instanceof XSComplexTypeDecl) {
                XSComplexTypeDecl fTypeDecl = (XSComplexTypeDecl) fType;

                List<Item> parentsList = new ArrayList<Item>();

                if (parents != null) {
                    parentsList.addAll(parents);
                }

                Item newItem = new Item();
                newItem.setElement(element);
                parentsList.add(newItem);

                XSParticle fParticle = fTypeDecl.getParticle();

                int repeat = 0;
                for (Item i : parentsList) {
                    if (newItem.equals(i)) {
                        repeat++;
                    }
                }

                if (repeat < 3) {
                    content.append(String.format("<li><span>%s <span class=\"path\">[%s]</span></span>\n<ul>\n", 
                            item.getName(), 
                            getPath(parents, item.getName())));
                    traversItem(fParticle, parentsList, content);
                    content.append("</ul>\n</li>\n");
                }

            } else if (fType instanceof XSSimpleTypeDecl) {
                content.append(String.format("<li><span>%s <span class=\"path\">[%s]</span></span></li>\n", 
                        item.getName(), 
                        getPath(parents, item.getName())));
            }

        } else if (item instanceof XSParticleDecl) {
            XSParticleDecl fParticleDecl = (XSParticleDecl) item;
            XSTerm fValue = fParticleDecl.fValue;

            if (fValue instanceof XSModelGroupImpl) {
                XSModelGroupImpl modelGroup = (XSModelGroupImpl) fValue;
                XSParticleDecl[] fParticles = modelGroup.fParticles;
                if (fParticles != null) {
                    for (XSParticleDecl fp : fParticles) {
                        traversItem(fp, parents, content);
                    }
                }

            } else if (fValue instanceof XSElementDecl) {
                traversItem((XSElementDecl) fValue, parents, content);

            } else if (fValue instanceof XSWildcardDecl) {
                content.append(String.format("<li> XSWildcardDecl %s</li>\n", item.getName()));
            }

        }
    }

    private void generateOutputFile(String in, String out, StringBuilder content) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(in));
        PrintWriter writer = new PrintWriter(new FileWriter(out));
        String line = null;

        while ((line = reader.readLine()) != null) {
            writer.println(line.replaceAll("_LIST_", content.toString()));
        }

        reader.close();
        writer.close();
    }

    private static String getPath(List<Item> parents, String last) {
        StringBuilder path = new StringBuilder();
        if (parents != null) {
            for (Item i : parents) {
                path.append("&#92;");
                path.append(i.getElementName());
            }
            
            path.append("&#92;");
            path.append(last);
        }

        return path.toString();
    }

}
