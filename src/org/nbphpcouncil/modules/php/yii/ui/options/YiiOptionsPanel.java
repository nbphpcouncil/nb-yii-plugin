/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.nbphpcouncil.modules.php.yii.ui.options;

import java.io.File;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.nbphpcouncil.modules.php.yii.commands.YiiScript;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

final class YiiOptionsPanel extends JPanel {

    private static final long serialVersionUID = -424018456110276101L;
    private final YiiOptionsPanelController controller;
    private String YII_LAST_FOLDER_SUFFIX = ".yii"; // NOI18N

    YiiOptionsPanel(YiiOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }

    public String getYiiScript() {
        return yiiScriptTextField.getText();
    }

    public void setYiiScript(String yiiScript) {
        yiiScriptTextField.setText(yiiScript);
    }

    public void setWarning(String message) {
        warningLabel.setText(" "); // NOI18N
        warningLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        warningLabel.setText(message); // NOI18N
    }

    public void clearError() {
        warningLabel.setText(" "); // NOI18N
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        yiiScriptTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        yiiScriptLabel = new javax.swing.JLabel();
        yiicLabel = new javax.swing.JLabel();
        warningLabel = new javax.swing.JLabel();

        yiiScriptTextField.setText(org.openide.util.NbBundle.getMessage(YiiOptionsPanel.class, "YiiOptionsPanel.yiiScriptTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(YiiOptionsPanel.class, "YiiOptionsPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(yiiScriptLabel, org.openide.util.NbBundle.getMessage(YiiOptionsPanel.class, "YiiOptionsPanel.yiiScriptLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(yiicLabel, org.openide.util.NbBundle.getMessage(YiiOptionsPanel.class, "YiiOptionsPanel.yiicLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(YiiOptionsPanel.class, "YiiOptionsPanel.warningLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(yiiScriptLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(yiicLabel)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(yiiScriptTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton)
                                .addGap(24, 24, 24))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(warningLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yiiScriptLabel)
                    .addComponent(yiiScriptTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yiicLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningLabel)
                .addContainerGap(110, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File yiiScript = new FileChooserBuilder(YiiOptionsPanel.class.getName() + YII_LAST_FOLDER_SUFFIX)
                .setTitle(NbBundle.getMessage(YiiOptionsPanel.class, "LBL_SelectYii"))
                .setFilesOnly(true)
                .showOpenDialog();
        if (yiiScript != null) {
            yiiScript = FileUtil.normalizeFile(yiiScript);
            if (yiiScript.getName().equals(YiiScript.YII_SCRIPT_NAME_LONG)) {
                setYiiScript(yiiScript.getAbsolutePath());
            } else {
                setYiiScript(""); // NOI18N
            }
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    void load() {
        // TODO read settings and initialize GUI
        // Example:
        // someCheckBox.setSelected(Preferences.userNodeForPackage(YiiOptionsPanel.class).getBoolean("someFlag", false));
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(YiiOptionsPanel.class).getBoolean("someFlag", false));
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
        setYiiScript(getOptions().getYiiScript());
    }

    void store() {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(YiiOptionsPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(YiiOptionsPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
        String yiiScript = getYiiScript();
        if (yiiScript.endsWith(YiiScript.YII_SCRIPT_NAME_LONG)) {
            getOptions().setYiiScript(getYiiScript());
        } else {
            getOptions().setYiiScript(""); // NOI18N
        }
    }

    private YiiOptions getOptions() {
        return YiiOptions.getInstance();
    }

    boolean valid() {
        String warning = YiiScript.validate(getYiiScript());
        if (warning != null) {
            setWarning(warning);
            return true;
        }
        clearError();
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JLabel yiiScriptLabel;
    private javax.swing.JTextField yiiScriptTextField;
    private javax.swing.JLabel yiicLabel;
    // End of variables declaration//GEN-END:variables
}
