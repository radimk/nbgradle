package org.nbgradle.netbeans.project.lookup;

import java.util.Collections;
import java.util.logging.Logger;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.modules.editor.indent.project.api.Customizers;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.NbBundle;

/**
 *
 * @author radim
 */
public class ProjectCustomizers {
  private static final Logger LOG = Logger.getLogger(ProjectCustomizers.class.getName());
  
  @ProjectCustomizer.CompositeCategoryProvider.Registration(
      projectType=NbGradleConstants.PROJECT_TYPE, position=1000,
      category="Formatting", categoryLabel="#LBL_CategoryFormatting")
  @NbBundle.Messages("LBL_CategoryFormatting=Formatting")
  public static ProjectCustomizer.CompositeCategoryProvider formatting() {
    return Customizers.createFormattingCategoryProvider(Collections.emptyMap());
  }
}
