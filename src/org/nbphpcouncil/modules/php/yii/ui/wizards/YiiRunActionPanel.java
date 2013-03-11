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
package org.nbphpcouncil.modules.php.yii.ui.wizards;

import java.awt.LayoutManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FormalParameter;

/**
 *
 * @author junichi11
 */
public class YiiRunActionPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 6490795753675911906L;
    private final List<FormalParameter> params;
    private Map<JLabel, JTextField> componentsMap;

    /**
     * Creates new form YiiRunActionPanel
     */
    public YiiRunActionPanel(List<FormalParameter> params) {
        this.params = params;

        initComponents();
        createComponents();
        addComponents();
    }

    /**
     * Create components.
     */
    private void createComponents() {
        componentsMap = new LinkedHashMap<JLabel, JTextField>();
        for (FormalParameter param : params) {
            String name = CodeUtils.getParamDisplayName(param);
            name = name.replace("$", ""); // NOI18N
            JLabel nameLabel = new JLabel(name);
            JTextField valueTextField = new JTextField();
            componentsMap.put(nameLabel, valueTextField);
        }
    }

    /**
     * Add components to Layout.
     */
    private void addComponents() {
        LayoutManager layout = getLayout();
        if (layout instanceof GroupLayout) {
            GroupLayout groupLayout = (GroupLayout) layout;
            // Horizontal
            GroupLayout.ParallelGroup hParallel = groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.SequentialGroup hSequential = groupLayout.createSequentialGroup();
            hSequential.addContainerGap();
            GroupLayout.ParallelGroup hParallel2 = groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
            for (JLabel key : componentsMap.keySet()) {
                JTextField value = componentsMap.get(key);
                hParallel2.addGroup(
                        groupLayout.createSequentialGroup()
                        .addComponent(key, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(value, GroupLayout.PREFERRED_SIZE, 315, Short.MAX_VALUE));
            }
            hSequential.addContainerGap()
                    .addGroup(hParallel2)
                    .addContainerGap();
            hParallel.addGroup(hSequential);
            groupLayout.setHorizontalGroup(hParallel);

            // Vertical
            GroupLayout.ParallelGroup vParallel = groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.SequentialGroup vSequential = groupLayout.createSequentialGroup();
            vSequential.addContainerGap();
            for (JLabel key : componentsMap.keySet()) {
                JTextField value = componentsMap.get(key);
                vSequential.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(key)
                        .addComponent(value, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
                vSequential.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
            }
            vSequential.addContainerGap(226, Short.MAX_VALUE);
            vParallel.addGroup(vSequential);
            groupLayout.setVerticalGroup(vParallel);
        }
    }

    /**
     * Get GET requests.
     *
     * @return
     */
    public Map<String, String> getGetRequest() {
        Map<String, String> getRequests = new LinkedHashMap<String, String>();
        int count = 0;
        for (JLabel key : componentsMap.keySet()) {
            JTextField value = componentsMap.get(key);
            String valueText = value.getText();
            if (valueText.isEmpty()) {
                FormalParameter param = params.get(count);
                Expression defaultValue = param.getDefaultValue();
                if (defaultValue != null) {
                    count++;
                    continue;
                }
            }
            getRequests.put(key.getText(), valueText);
            count++;
        }
        return getRequests;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
