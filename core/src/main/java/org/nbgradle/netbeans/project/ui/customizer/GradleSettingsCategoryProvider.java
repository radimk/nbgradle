package org.nbgradle.netbeans.project.ui.customizer;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.common.base.Preconditions;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;

public class GradleSettingsCategoryProvider implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GRADLE = "Gradle";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = NbGradleConstants.PROJECT_TYPE, position = 10)
    public static GradleSettingsCategoryProvider createGeneral() {
        return new GradleSettingsCategoryProvider();
    }

    @Override
    public Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create(GRADLE, "Gradle", null);
    }

    @Override
    public JComponent createComponent(Category category, Lookup lkp) {
        DefaultGradleBuildSettings buildSettings =
                Preconditions.checkNotNull(lkp.lookup(DefaultGradleBuildSettings.class));
        GradleSettingsPanel settingsPanel = new GradleSettingsPanel();
        settingsPanel.attachData(buildSettings);
        return settingsPanel;
    }

}