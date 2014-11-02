package org.nbgradle.netbeans.project.ui.customizer;

import com.google.common.base.Preconditions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.nbgradle.netbeans.project.GradleProjectImporter;

import org.nbgradle.netbeans.project.NbGradleConstants;
import org.nbgradle.netbeans.project.NbGradleProject;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public class GradleSettingsCategoryProvider implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GRADLE = "Gradle";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = NbGradleConstants.PROJECT_TYPE, position = 10)
    public static GradleSettingsCategoryProvider createGeneral() {
        return new GradleSettingsCategoryProvider();
    }

    @Override
    public Category createCategory(Lookup lkp) {
        Category gradleCategory = ProjectCustomizer.Category.create(GRADLE, "Gradle", null);
        DefaultGradleBuildSettings buildSettings =
                Preconditions.checkNotNull(lkp.lookup(DefaultGradleBuildSettings.class));
        gradleCategory.setStoreListener(new StoreListener(buildSettings, lkp.lookup(Project.class)));
        return gradleCategory;
    }

    @Override
    public JComponent createComponent(Category category, Lookup lkp) {
        DefaultGradleBuildSettings buildSettings =
                Preconditions.checkNotNull(lkp.lookup(DefaultGradleBuildSettings.class));
        GradleSettingsPanel settingsPanel = new GradleSettingsPanel();
        settingsPanel.attachData(buildSettings);
        StoreListener storeListener = (StoreListener) category.getStoreListener();
        storeListener.setComponent(settingsPanel);
        return settingsPanel;
    }

    private static class StoreListener implements ActionListener {
        private final DefaultGradleBuildSettings buildSettings;
        private final Project project;
        private GradleSettingsPanel settingsPanel;

        public StoreListener(DefaultGradleBuildSettings buildSettings, Project project) {
            this.buildSettings = buildSettings;
            this.project = project;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            settingsPanel.updateData(buildSettings);
            new GradleProjectImporter().importProject(buildSettings, FileUtil.toFile(project.getProjectDirectory()));
        }

        private void setComponent(GradleSettingsPanel settingsPanel) {
            this.settingsPanel = settingsPanel;
        }
    }
}