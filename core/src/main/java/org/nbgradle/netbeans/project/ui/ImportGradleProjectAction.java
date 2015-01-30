package org.nbgradle.netbeans.project.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.nbgradle.netbeans.project.GradleProjectImporter;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

/**
 * An action that starts a wizard to import root Gradle project using specified settings.
 */
@ActionID(
        category = "File",
        id = "org.nbgradle.netbeans.project.ui.ImportGradleProjectAction"
)
@ActionRegistration(
        displayName = "#CTL_ImportGradleProjectAction"
)
@ActionReference(path = "Menu/File/Import", position = 1427)
@Messages("CTL_ImportGradleProjectAction=Gradle Project ...")
public final class ImportGradleProjectAction implements ActionListener {
    private static final RequestProcessor RP = new RequestProcessor("Gradle Importer");

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO replace with wizard that sets Gradle wrapper/version/installation too
        File home = new File (System.getProperty("user.home"));
        final File importedDir = new FileChooserBuilder("libraries-dir")
                .setTitle("Import Gradle Project")
                .setDefaultWorkingDirectory(home)
                .setApproveText("Import")
                .setDirectoriesOnly(true)
                .showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (importedDir != null) {
            // TODO run asynchronously
            RP.post(new Runnable() {
                @Override
                public void run() {
                    runImporter(importedDir);
                }
            });
        }
    }

    private void runImporter(File importedDir) {
        try {
            new GradleProjectImporter().importProject(new DefaultGradleBuildSettings(), importedDir);
            Project project = ProjectManager.getDefault().findProject(FileUtil.toFileObject(importedDir));
            if (project != null) {
                OpenProjects.getDefault().open(new Project[] {project}, false);
            } else {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        "Project was not created. Check the log.", NotifyDescriptor.INFORMATION_MESSAGE));
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }
}
