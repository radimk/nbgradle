package org.nbgradle.netbeans.project.ui.customizer;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.nbgradle.netbeans.project.NbGradleConstants;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class ProjectCustomizerProvider implements CustomizerProvider {

    public final Project project;

    public static final String CUSTOMIZER_FOLDER_PATH = "Projects/" + NbGradleConstants.PROJECT_TYPE + "/Customizer";

    public ProjectCustomizerProvider(Project project) {
        this.project = project;
    }

    @Override
    public void showCustomizer() {
        Lookup lookup = Lookups.fixed(
                project,
                project.getLookup().lookup(DefaultGradleBuildSettings.class));
        Dialog dialog = ProjectCustomizer.createCustomizerDialog(
                CUSTOMIZER_FOLDER_PATH,
                lookup,
                "", //Preselected category
                new OKOptionListener(),//OK button listener
                null); // HelpCtx
        dialog.setTitle(ProjectUtils.getInformation(project).getDisplayName());
        dialog.setVisible(true);
    }

    private class OKOptionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StatusDisplayer.getDefault().setStatusText("OK button clicked for "
                    + project.getProjectDirectory().getName() + " customizer!");
        }

    }
}
