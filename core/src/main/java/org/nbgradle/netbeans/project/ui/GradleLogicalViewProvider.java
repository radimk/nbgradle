package org.nbgradle.netbeans.project.ui;

import org.nbgradle.netbeans.project.NbGradleConstants;
import org.nbgradle.netbeans.project.NbGradleProject;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * Factory for project root nodes.
 */
public class GradleLogicalViewProvider implements LogicalViewProvider {
    private final Project project;

    public GradleLogicalViewProvider(Project project) {
        this.project = project;
    }

    @Override
    public Node createLogicalView() {
        Logger.getLogger(GradleLogicalViewProvider.class.getName()).info("createLogicalView");
        return new GradleProjectRootNode(project);
    }

    @Override
    public Node findPath(Node root, Object target) {
        return null;
    }

    private static class GradleProjectRootNode extends AbstractNode {
        private GradleProjectRootNode(final Project project) {
            super(NodeFactorySupport.createCompositeChildren(project, "Projects/" + NbGradleConstants.PROJECT_TYPE +"/Nodes"),
                    Lookups.singleton(project));
            setIconBaseWithExtension("org/nbgradle/netbeans/project/gradle.png");
            super.setName(ProjectUtils.getInformation(project).getDisplayName());
            Logger.getLogger(GradleLogicalViewProvider.class.getName()).info("created root node");
        }

        @Override
        public Action[] getActions(boolean context) {
            Logger.getLogger(GradleLogicalViewProvider.class.getName()).info("create actions");
            return CommonProjectActions.forType(NbGradleConstants.PROJECT_TYPE);
        }
    }
}
