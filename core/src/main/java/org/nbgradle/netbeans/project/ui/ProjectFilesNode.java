package org.nbgradle.netbeans.project.ui;

import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;

/**
 * Gradle project related aggregator node.
 * @author Milos Kleint
 */
public class ProjectFilesNode extends AnnotatedAbstractNode {
    
    private Project project;

    @Messages("LBL_Project_Files=Project Files")
    public ProjectFilesNode(Project project) {
        super(Children.create(new ProjectFilesChildren(project), true), Lookups.fixed());
        setName("projectfiles"); //NOI18N
        setDisplayName(Bundle.LBL_Project_Files());
        this.project = project;
        setMyFiles();
    }
    
    private Image getIcon(boolean opened) {
        Image badge = ImageUtilities.loadImage("org/nbgradle/netbeans/project/projectfiles-badge.png", true); //NOI18N
        Image img = ImageUtilities.mergeImages(UiUtils.getTreeFolderIcon(opened), badge, 8, 8);
        return img;
    }
    
    @Override
    protected Image getIconImpl(int param) {
        return getIcon(false);
    }

    @Override
    protected Image getOpenedIconImpl(int param) {
        return getIcon(true);
    }
    
    private void setMyFiles() {
        Set<FileObject> fobs = new HashSet<FileObject>();
        FileObject fo = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
        if (fo != null) {
            //#119134 for some unknown reason, the pom.xml might be missing from the project directory in some cases.
            // prevent passing null to the list that causes problems down the stream.
            fobs.add(fo);
        }
        setFiles(fobs);
    }
    
    private static class ProjectFilesChildren extends ChildFactory.Detachable<FileObject> /* implements PropertyChangeListener*/ {

        private final Project project;
        private final FileChangeAdapter fileChangeListener;
        
        ProjectFilesChildren(Project proj) {
            project = proj;
            fileChangeListener = new FileChangeAdapter() {
                @Override public void fileDataCreated(FileEvent fe) {
                    refresh(false);
                }
                @Override public void fileDeleted(FileEvent fe) {
                    refresh(false);
                }
            };
        }

        @Override protected Node createNodeForKey(FileObject key) {
            try {
                return DataObject.find(key).getNodeDelegate().cloneNode();
            } catch (DataObjectNotFoundException e) {
                return null;
            }
        }
        
//        public @Override void propertyChange(PropertyChangeEvent evt) {
//            if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
//                refresh(false);
//            }
//        }

        @Override protected void addNotify() {
            // NbMavenProject.addPropertyChangeListener(project, this);
            project.getProjectDirectory().addFileChangeListener(fileChangeListener);
        }

        @Override protected void removeNotify() {
            // NbMavenProject.removePropertyChangeListener(project, this);
            project.getProjectDirectory().removeFileChangeListener(fileChangeListener);
        }

        @Override protected boolean createKeys(List<FileObject> keys) {
            FileObject d = project.getProjectDirectory();
            addIfExists(keys, d.getFileObject(NbGradleConstants.BUILD_GRADLE_FILENAME));
            addIfExists(keys, d.getFileObject(NbGradleConstants.SETTINGS_GRADLE_FILENAME));
            return true;
        }

        private void addIfExists(List<FileObject> keys, FileObject fileObject) {
            if (fileObject != null) {
                keys.add(fileObject);
            }
        }
    }
}
