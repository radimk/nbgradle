package org.nbgradle.netbeans.project.ui;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;

/**
 * shows maven project files.
 * @author  Milos Kleint
 */
@NodeFactory.Registration(projectType=NbGradleConstants.PROJECT_TYPE,position=700)
public class ProjectFilesNodeFactory implements NodeFactory {
    
    /** Creates a new instance of ProjectFilesNodeFactory */
    public ProjectFilesNodeFactory() {
    }
    
    @Override
    public NodeList createNodes(Project project) {
        return NodeFactorySupport.fixedNodeList(new Node[] {
            new ProjectFilesNode(project)
        });
    }
    
    
}
