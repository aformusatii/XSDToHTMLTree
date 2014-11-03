package xsdtohtmltree;

import static xsdtohtmltree.Properties.getProp;
import static xsdtohtmltree.Properties.Keys.HTML_TEMPALTE_FILE;
import static xsdtohtmltree.Properties.Keys.OUTPUT_DIRECTORY;
import static xsdtohtmltree.Properties.Keys.ROOT_XSD_FILE;
import static xsdtohtmltree.XSNode.createChild;
import static xsdtohtmltree.XSNode.createOther;
import static xsdtohtmltree.XSNode.createParentEnd;
import static xsdtohtmltree.XSNode.createParentStart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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

        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        XSImplementation impl = (XSImplementation) registry.getDOMImplementation("XS-Loader");
        XSLoader schemaLoader = impl.createXSLoader(null);
        DOMConfiguration config = schemaLoader.getConfig();
        config.setParameter("validate", Boolean.TRUE);
        XSModel model = schemaLoader.loadURI(getProp(ROOT_XSD_FILE));
        List<XSNode> allNodes = new LinkedList<XSNode>();
        
        if (model != null) {
            XSNamedMap map = model.getComponents(XSConstants.ELEMENT_DECLARATION);
            if (map.getLength() != 0) {
                for (int i = 0; i < map.getLength(); i++) {
                    XSObject node = map.item(i);
                    traversItem(node, null, allNodes);
                }
            }
        }

        String templateFile = getProp(HTML_TEMPALTE_FILE);
        String outputFile = getProp(OUTPUT_DIRECTORY) + File.separator + FilenameUtils.removeExtension(new File(getProp(ROOT_XSD_FILE)).getName()) + ".html";
        System.out.println("=> Output file: " + new File(outputFile).getAbsolutePath());

        generateOutputFile(allNodes, templateFile, outputFile);
    }

    private void traversItem(XSObject node, List<XSNode> parents, List<XSNode> allNodes) {
        if (node instanceof XSElementDecl) {
            XSElementDecl element = (XSElementDecl) node;
            XSTypeDefinition fType = element.getTypeDefinition();
            if (fType instanceof XSComplexTypeDecl) {
                XSComplexTypeDecl fTypeDecl = (XSComplexTypeDecl) fType;

                List<XSNode> parentsClone = new ArrayList<XSNode>();

                if (parents != null) {
                    parentsClone.addAll(parents);
                }

                XSNode newNode = createParentStart(element, fTypeDecl, null);
                parentsClone.add(newNode);

                XSParticle fParticle = fTypeDecl.getParticle();

                int repeat = 0;
                for (XSNode i : parentsClone) {
                    if (newNode.equals(i)) {
                        repeat++;
                    }
                }

                if (repeat < 3) {
                    allNodes.add(createParentStart(element, fTypeDecl, parents));
                    traversItem(fParticle, parentsClone, allNodes);
                    allNodes.add(createParentEnd(element, fTypeDecl, parents));
                    
                }

            } else if (fType instanceof XSSimpleTypeDecl) {
                allNodes.add(createChild(element, (XSSimpleTypeDecl) fType, parents));
                
            }

        } else if (node instanceof XSParticleDecl) {
            XSParticleDecl fParticleDecl = (XSParticleDecl) node;
            XSTerm fValue = fParticleDecl.fValue;

            if (fValue instanceof XSModelGroupImpl) {
                XSModelGroupImpl modelGroup = (XSModelGroupImpl) fValue;
                XSParticleDecl[] fParticles = modelGroup.fParticles;
                if (fParticles != null) {
                    for (XSParticleDecl fp : fParticles) {
                        traversItem(fp, parents, allNodes);
                    }
                }

            } else if (fValue instanceof XSElementDecl) {
                traversItem((XSElementDecl) fValue, parents, allNodes);

            } else if (fValue instanceof XSWildcardDecl) {
                allNodes.add(createOther(fParticleDecl, (XSWildcardDecl) fValue, parents));
                
            }
        }
    }
    
    private void generateOutputFile(List<XSNode> allNodes, String template, String output) {
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(output));
            VelocityEngine ve = new VelocityEngine();
            ve.init();
            Template t = ve.getTemplate( template );
            VelocityContext context = new VelocityContext();
            context.put("nodes", allNodes);
            t.merge( context, outputWriter );
            outputWriter.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputWriter != null) {
                outputWriter.close(); 
            }
        }
    }

}
