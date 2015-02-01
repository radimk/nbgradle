/*
 */
package org.nbgradle.netbeans.project.lookup;

import com.google.common.base.Preconditions;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.tooling.model.GradleProject;
import org.nbgradle.netbeans.models.adapters.GradleProjectBridge;
import org.nbgradle.netbeans.project.AbstractModelProducer;
import org.nbgradle.netbeans.project.ModelProcessor;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.nbgradle.netbeans.project.lookup.aux.AuxiliaryPropertiesImpl;
import org.nbgradle.netbeans.project.utils.IoUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.queries.SharabilityQueryImplementation2;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={SharabilityQueryImplementation2.class, ModelProcessor.class},
        projectType=NbGradleConstants.PROJECT_TYPE)
public final class GradleSharabilityQuery extends AbstractModelProducer<GradleProject>
        implements SharabilityQueryImplementation2 {
    private static final Logger LOG = Logger.getLogger(GradleSharabilityQuery.class.getName());

    private final Project project;
    private final File prjDir;
    private File buildDirectory;

    public GradleSharabilityQuery(Project project, Lookup baseLookup) {
        super(baseLookup, GradleProject.class);
        this.project = Preconditions.checkNotNull(project);
        prjDir = FileUtil.toFile(project.getProjectDirectory());
    }

    @Override
    protected void updateFromModel(GradleProject model) {
        LOG.log(Level.FINE, "update from model {0}", project);
        if (model == null) {
            // maybe it is better to hold previous state.
            return;
        }
        GradleProject currentGradleProject = new GradleProjectBridge(model).moduleForProject(project);
        if (currentGradleProject == null) {
            return;
        }
        buildDirectory = currentGradleProject.getBuildDirectory();
    }

    @Override
    public SharabilityQuery.Sharability getSharability(URI uri) {
        try {
            File file = Utilities.toFile(uri);
            if (IoUtils.isParentOrSame(buildDirectory, file)) {
                return SharabilityQuery.Sharability.NOT_SHARABLE;
            }
            if (Objects.equals(prjDir, file.getParentFile())) {
                if (NbGradleConstants.NBGRADLE_BUILD_XML.equals(file.getName()) || 
                        NbGradleConstants.NBGRADLE_BUILD_XML.equals(file.getName())) {
                    return SharabilityQuery.Sharability.NOT_SHARABLE;
                }
                if (NbGradleConstants.BUILD_GRADLE_FILENAME.equals(file.getName()) || 
                        NbGradleConstants.SETTINGS_GRADLE_FILENAME.equals(file.getName())) {
                    return SharabilityQuery.Sharability.SHARABLE;
                }
            }
            if (Objects.equals(new File(prjDir, "nbgradle"), file)) {
                return SharabilityQuery.Sharability.MIXED;
            } else if (Objects.equals(new File(prjDir, AuxiliaryPropertiesImpl.PRIVATE_PROPERTIES_PATH), file)) {
                return SharabilityQuery.Sharability.NOT_SHARABLE;
            } else if (Objects.equals(new File(prjDir, AuxiliaryPropertiesImpl.PROJECT_PROPERTIES_PATH), file)) {
                return SharabilityQuery.Sharability.SHARABLE;
            }
        } catch (Exception ex) {
            LOG.log(Level.FINE, "Cannot convert uri to file to provide sharability: " + uri, ex);
        }
        return SharabilityQuery.Sharability.UNKNOWN;
    }

}
