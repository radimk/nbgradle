package org.nbgradle.netbeans.project.ui;

import java.awt.Image;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;


/** 
 * A node that shows filesystem annotations on icons.
 * Copy paste code from Maven support, similar exists in Android copied from J2SE.
 *
 * @author Milos Kleint
 */
public abstract class AnnotatedAbstractNode extends AbstractNode implements FileStatusListener, Runnable {

    private Set<FileObject> files;
    private Map<FileSystem, FileStatusListener> fileSystemListeners;
    private RequestProcessor.Task task;
    private final Object privateLock = new Object();
    private boolean iconChange;
    private boolean nameChange;
    private static final RequestProcessor RP = new RequestProcessor(AnnotatedAbstractNode.class);

    public AnnotatedAbstractNode(Children childs, Lookup lookup) {
        super(childs, lookup);
    }

    //----------------------------------------------------
// icon annotation change related, copied from j2se project.
// eventually this should end up in a sort of SPI and be shared across project types

    protected final void setFiles(Set<FileObject> files) {
        if (fileSystemListeners != null) {
            for (Map.Entry<FileSystem, FileStatusListener> e : fileSystemListeners.entrySet()) {
                e.getKey().removeFileStatusListener(e.getValue());
            }
        }

        fileSystemListeners = new HashMap<FileSystem, FileStatusListener>();
        synchronized (privateLock) {
            this.files = files;
            if (files == null) {
                return;
            }

            Set<FileSystem> hookedFileSystems = new HashSet<FileSystem>();
            for (FileObject fo : files) {
                try {
                    FileSystem fs = fo.getFileSystem();
                    if (hookedFileSystems.contains(fs)) {
                        continue;
                    }
                    hookedFileSystems.add(fs);
                    FileStatusListener fsl = FileUtil.weakFileStatusListener(this, fs);
                    fs.addFileStatusListener(fsl);
                    fileSystemListeners.put(fs, fsl);
                } catch (FileStateInvalidException e) {
                    ErrorManager err = ErrorManager.getDefault();
                    err.annotate(e, ErrorManager.UNKNOWN, "Cannot get " + fo + " filesystem, ignoring...", null, null, null); // NO18N
                    err.notify(ErrorManager.INFORMATIONAL, e);
                }
            }
        }
        fireIconChange();
        fireOpenedIconChange();
    }

    @Override
    public void run() {
        boolean fireIcon;
        boolean fireName;
        synchronized (privateLock) {
            fireIcon = iconChange;
            fireName = nameChange;
            iconChange = false;
            nameChange = false;
        }
        if (fireIcon) {
            fireIconChange();
            fireOpenedIconChange();
        }
        if (fireName) {
            fireDisplayNameChange(null, null);
        }
    }

    @Override
    public void annotationChanged(FileStatusEvent event) {
        if (task == null) {
            task = RP.create(this);
        }

        synchronized (privateLock) {
            if ((iconChange == false && event.isIconChange()) || (nameChange == false && event.isNameChange())) {
                for (FileObject fo : files) {
                    if (event.hasChanged(fo)) {
                        iconChange |= event.isIconChange();
                        nameChange |= event.isNameChange();
                    }
                }
            }
        }

        task.schedule(50); // batch by 50 ms
    }
    //----------------------------------------------------

    protected abstract Image getIconImpl(int param);

    protected abstract Image getOpenedIconImpl(int param);

    @Override
    public final Image getIcon(int param) {
        Image img = getIconImpl(param);
        return annotateImpl(img, param);
    }

    @Override
    public final Image getOpenedIcon(int param) {
        Image img = getOpenedIconImpl(param);
        return annotateImpl(img, param);
    }
    
    private Image annotateImpl(Image img, int param) {
        synchronized (privateLock) {
            if (files != null && files.size() > 0) {
                try {
                    Iterator<FileObject> it = files.iterator();
                    assert it.hasNext();
                    FileObject fo = it.next();
                    assert fo != null;
                    FileSystem fs = fo.getFileSystem();
                    assert fs != null;
                    return fs.getStatus().annotateIcon(img, param, files);
                } catch (FileStateInvalidException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
        }
        return img;
    }
}
