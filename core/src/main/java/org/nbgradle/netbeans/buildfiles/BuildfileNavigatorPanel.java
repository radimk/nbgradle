package org.nbgradle.netbeans.buildfiles;

import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author radim
 */
@NavigatorPanel.Registration(displayName = "Gradle Build", mimeType = "text/x-gradle+x-groovy", position = 100)
public class BuildfileNavigatorPanel implements NavigatorPanel {

    @Override
    public String getDisplayName() {
        return "Gradle Build";
    }

    @Override
    public String getDisplayHint() {
        return "Gradle Build";
    }

    @Override
    public JComponent getComponent() {
        return new BuildfilePanel();
    }

    @Override
    public void panelActivated(Lookup context) {
        
    }

    @Override
    public void panelDeactivated() {
        
    }

    @Override
    public Lookup getLookup() {
        return Lookup.EMPTY;
    }
}
