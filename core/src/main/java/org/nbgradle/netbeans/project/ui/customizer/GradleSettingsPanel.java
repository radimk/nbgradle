package org.nbgradle.netbeans.project.ui.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import com.google.common.base.Strings;
import org.nbgradle.netbeans.project.model.DefaultDistributionSpec;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.nbgradle.netbeans.project.model.InstallationDistributionSpec;
import org.nbgradle.netbeans.project.model.URIDistributionSpec;
import org.nbgradle.netbeans.project.model.VersionDistributionSpec;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author radim
 */
public class GradleSettingsPanel extends javax.swing.JPanel {

    /**
     * Creates new form GradleSettings
     */
    public GradleSettingsPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new ButtonGroup();
        lblGradleVersion = new JLabel();
        radioDefaultVersion = new JRadioButton();
        radioInstalledVersion = new JRadioButton();
        txtInstallLocation = new JTextField();
        btnInstallLocation = new JButton();
        radioVersion = new JRadioButton();
        cbxVersion = new JComboBox();
        radioURIVersion = new JRadioButton();
        txtURIVersion = new JTextField();
        lblUserDir = new JLabel();
        txtUserDir = new JTextField();
        btnUserDir = new JButton();
        lblVMOptions = new JLabel();
        scrollVMOptions = new JScrollPane();
        txtVMOptions = new JTextArea();

        FormListener formListener = new FormListener();

        lblGradleVersion.setText("Gradle distribution:");

        buttonGroup1.add(radioDefaultVersion);
        radioDefaultVersion.setText("Default (uses wrapper if configured)");
        radioDefaultVersion.addActionListener(formListener);

        buttonGroup1.add(radioInstalledVersion);
        radioInstalledVersion.setText("Local installation:");

        btnInstallLocation.setText("Select");
        btnInstallLocation.addActionListener(formListener);

        buttonGroup1.add(radioVersion);
        radioVersion.setText("Version:");

        cbxVersion.setEditable(true);

        buttonGroup1.add(radioURIVersion);
        radioURIVersion.setText("URI:");

        lblUserDir.setLabelFor(txtUserDir);
        lblUserDir.setText("Gradle user home directory:");

        btnUserDir.setText("Select");
        btnUserDir.addActionListener(formListener);

        lblVMOptions.setLabelFor(lblVMOptions);
        lblVMOptions.setText("Gradle VM options:");

        txtVMOptions.setColumns(20);
        txtVMOptions.setRows(5);
        scrollVMOptions.setViewportView(txtVMOptions);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(radioDefaultVersion)
                                .addGap(0, 98, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(radioURIVersion)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtURIVersion))
                            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radioVersion)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbxVersion, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radioInstalledVersion)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtInstallLocation)))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnInstallLocation))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblUserDir)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUserDir)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUserDir))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblGradleVersion)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblVMOptions)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollVMOptions, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblGradleVersion)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioDefaultVersion)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(radioInstalledVersion)
                    .addComponent(txtInstallLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInstallLocation))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(radioVersion)
                    .addComponent(cbxVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(radioURIVersion)
                    .addComponent(txtURIVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserDir)
                    .addComponent(txtUserDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUserDir))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblVMOptions)
                    .addComponent(scrollVMOptions, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == radioDefaultVersion) {
                GradleSettingsPanel.this.radioDefaultVersionActionPerformed(evt);
            }
            else if (evt.getSource() == btnInstallLocation) {
                GradleSettingsPanel.this.btnInstallLocationActionPerformed(evt);
            }
            else if (evt.getSource() == btnUserDir) {
                GradleSettingsPanel.this.btnUserDirActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void radioDefaultVersionActionPerformed(ActionEvent evt) {//GEN-FIRST:event_radioDefaultVersionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioDefaultVersionActionPerformed

    private void btnInstallLocationActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnInstallLocationActionPerformed
        File gradleInstallDir = new FileChooserBuilder(GradleSettingsPanel.class).
                setDefaultWorkingDirectory(new File(System.getProperty("user.home"))).
                setDirectoriesOnly(true).
                setTitle("Gradle Installation Directory").
                showOpenDialog();
        if (gradleInstallDir != null) {
            txtInstallLocation.setText(gradleInstallDir.getAbsolutePath());
        }
    }//GEN-LAST:event_btnInstallLocationActionPerformed

    private void btnUserDirActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnUserDirActionPerformed
        File gradleInstallDir = new FileChooserBuilder(GradleSettingsPanel.class).
                setDefaultWorkingDirectory(new File(System.getProperty("user.home"), ".gradle")).
                setDirectoriesOnly(true).
                setTitle("Gradle User Home Directory").
                showOpenDialog();
        if (gradleInstallDir != null) {
            txtUserDir.setText(gradleInstallDir.getAbsolutePath());
        }
    }//GEN-LAST:event_btnUserDirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnInstallLocation;
    private JButton btnUserDir;
    private ButtonGroup buttonGroup1;
    private JComboBox cbxVersion;
    private JLabel lblGradleVersion;
    private JLabel lblUserDir;
    private JLabel lblVMOptions;
    private JRadioButton radioDefaultVersion;
    private JRadioButton radioInstalledVersion;
    private JRadioButton radioURIVersion;
    private JRadioButton radioVersion;
    private JScrollPane scrollVMOptions;
    private JTextField txtInstallLocation;
    private JTextField txtURIVersion;
    private JTextField txtUserDir;
    private JTextArea txtVMOptions;
    // End of variables declaration//GEN-END:variables

    public void attachData(DefaultGradleBuildSettings buildSettings) {
        if (buildSettings.getDistributionSettings() == null) {
            radioDefaultVersion.setSelected(true);
        } else {
            switch (buildSettings.getDistributionSettings().type) {
            case DEFAULT:
                radioDefaultVersion.setSelected(true);
                break;
            case FILE:
                radioInstalledVersion.setSelected(true);
                txtInstallLocation.setText(buildSettings.getDistributionSettings().getValue());
                break;
            case VERSION:
                radioVersion.setSelected(true);
                cbxVersion.getModel().setSelectedItem(buildSettings.getDistributionSettings().getValue());
                break;
            case URI:
                radioURIVersion.setSelected(true);
                txtURIVersion.setText(buildSettings.getDistributionSettings().getValue());
                break;
            default:
                throw new IllegalStateException("Unknown distribution type " + buildSettings.getDistributionSettings().type);
            }
        }
        File gradleUserHomeDir = buildSettings.getGradleUserHomeDir();
        txtUserDir.setText(gradleUserHomeDir != null ? gradleUserHomeDir.getAbsolutePath() : "");
        String jvmOptions = buildSettings.getJvmOptions();
        txtVMOptions.setText(jvmOptions != null ? jvmOptions : "");
    }

    public void updateData(DefaultGradleBuildSettings buildSettings) {
        if (radioDefaultVersion.isSelected()) {
            buildSettings.setDistributionSettings(new DefaultDistributionSpec());
        } else if (radioInstalledVersion.isSelected()) {
            InstallationDistributionSpec spec = new InstallationDistributionSpec();
            spec.setValue(txtInstallLocation.getText());
            buildSettings.setDistributionSettings(spec);
        } else if (radioVersion.isSelected()) {
            VersionDistributionSpec spec = new VersionDistributionSpec();
            spec.setValue((String) cbxVersion.getSelectedItem());
            buildSettings.setDistributionSettings(spec);
        } else if (radioURIVersion.isSelected()) {
            URIDistributionSpec spec = new URIDistributionSpec();
            spec.setValue(txtURIVersion.getText());
            buildSettings.setDistributionSettings(spec);
        } else {
            throw new IllegalStateException("Cannot set Gradle distribution.");
        }
        String userDir = txtUserDir.getText();
        buildSettings.setGradleUserHomeDir(Strings.isNullOrEmpty(userDir) ? null : new File(userDir));
        String jvmOptions = txtVMOptions.getText();
        buildSettings.setJvmOptions(Strings.isNullOrEmpty(jvmOptions) ? null : jvmOptions);
    }
}
