package org.nbgradle.netbeans.project.lookup;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.SubprojectProvider;

import javax.swing.event.ChangeListener;
import java.util.Collections;
import java.util.Set;

/**
 * Created by radim on 11/9/14.
 */
public class NbSubprojectProvider implements SubprojectProvider {
    private final ProjectTreeInformation projectTree;

    public NbSubprojectProvider(ProjectTreeInformation projectTree) {
        this.projectTree = projectTree;
    }

    @Override
    public Set<? extends Project> getSubprojects() {
        return Collections.emptySet();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {

    }

    @Override
    public void removeChangeListener(ChangeListener listener) {

    }
}
