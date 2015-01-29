/*
 */
package org.nbgradle.netbeans.java.ui;

import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;

// TODO fix to use custom model and show dependencies for real configurations
// possibly filter some known configurations to hide extra stuff
/**
 *
 * @author radim
 */
@NodeFactory.Registration(projectType=NbGradleConstants.PROJECT_TYPE,position=400)
public class DependenciesNodesFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project p) {
        return NodeFactorySupport.fixedNodeList(DependenciesNode.createCompileDependenciesNode("Dependencies", p),
                DependenciesNode.createTestDependenciesNode("Test Dependencies", p));
    }
}
