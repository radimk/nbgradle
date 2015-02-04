/*
 */
package org.nbgradle.netbeans.buildfiles;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author radim
 */
class BuildfilePanel extends JComponent implements ExplorerManager.Provider {

    private final ExplorerManager em = new ExplorerManager();

    public BuildfilePanel() {
        setLayout(new BorderLayout());
        BeanTreeView btv = new BeanTreeView();
        add(btv, BorderLayout.CENTER);
        em.setRootContext(new BuildfileNode());
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

}
