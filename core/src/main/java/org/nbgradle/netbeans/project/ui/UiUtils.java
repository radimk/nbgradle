package org.nbgradle.netbeans.project.ui;

import java.awt.Image;
import java.beans.BeanInfo;
import javax.swing.Icon;
import javax.swing.UIManager;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author radim
 */
public class UiUtils {

    private static final String ICON_KEY_UIMANAGER = "Tree.closedIcon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER = "Tree.openIcon"; // NOI18N
    private static final String ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.icon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.openedIcon"; // NOI18N
    public static final Image TEST_BADGE = ImageUtilities.loadImage(
            "org/nbgradle/netbeans/project/test_badge.png", true);
    public static final Image LIBRARIES_BADGE = ImageUtilities.loadImage(
            "org/nbgradle/netbeans/project/libraries-badge.png");    //NOI18N

    /**
     * Returns default folder icon as {@link java.awt.Image}. Never returns <code>null</code>.
     *
     * @param opened whether closed or opened icon should be returned.
     * @return requested image
     */
    public static Image getTreeFolderIcon(boolean opened) {
        Image base = (Image) UIManager.get(opened ? OPENED_ICON_KEY_UIMANAGER_NB : ICON_KEY_UIMANAGER_NB); // #70263;
        if (base == null) {
            Icon baseIcon = UIManager.getIcon(opened ? OPENED_ICON_KEY_UIMANAGER : ICON_KEY_UIMANAGER); // #70263
            if (baseIcon != null) {
                base = ImageUtilities.icon2Image(baseIcon);
            } else {
                try {
                    // fallback to our owns
                    return opened
                            ? DataObject.find(FileUtil.getConfigRoot()).getNodeDelegate().getOpenedIcon(BeanInfo.ICON_COLOR_16x16)
                            : DataObject.find(FileUtil.getConfigRoot()).getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16);
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        assert base != null;
        return base;
    }
}
