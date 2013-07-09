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
package org.nbphpcouncil.modules.php.yii.ui.actions;

import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import static org.nbphpcouncil.modules.php.yii.util.YiiUtils.getSubDirectoryPathForController;
import org.nbphpcouncil.modules.php.yii.util.YiiViewPathSupport;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class YiiGoToViewSupport {

    protected FileObject controller;
    protected String controllerId;
    // actionId contains subpath and absolute path
    // e.g. index, sub/index, /site/index, //site/index
    protected String actionId;
    protected String subPathToController;
    protected final PhpModule phpModule;

    public YiiGoToViewSupport(FileObject controller, String actionId) {
        this.controller = controller;
        this.actionId = actionId;
        if (controller != null) {
            this.phpModule = PhpModule.forFileObject(controller);
            this.controllerId = YiiUtils.getViewFolderName(controller.getName());
            this.subPathToController = getSubPathToController();
        } else {
            this.phpModule = null;
        }
    }

    public static YiiGoToViewSupport create(FileObject controller, String actionId) {

        if (YiiUtils.isInModules(controller)) {
            return new YiiGoToModulesViewSupport(controller, actionId);
        }

        return new YiiGoToAppViewSupport(controller, actionId);
    }

    public boolean isEnabled() {
        if (controller == null || phpModule == null || actionId == null) {
            return false;
        }
        return true;
    }

    /**
     * Get view file.
     *
     * @return view file
     */
    public FileObject getView() {
        if (!isEnabled()) {
            return null;
        }

        FileObject view = getAbsoluteView();
        if (view == null) {
            view = getRelativeView();
        }

        return view;
    }

    /**
     * Create view file.
     *
     * @return view file
     */
    public FileObject createView() {
        if (!isEnabled()) {
            return null;
        }

        FileObject view;
        if (YiiViewPathSupport.isAbsoluteViewPath(actionId)) {
            view = createAbsoluteView();
        } else {
            view = createRelativeView();
        }
        return view;
    }

    /**
     * Get relative view file. i.e. actionId is relative path from controllers
     * directory.
     *
     * @return view file
     */
    public abstract FileObject getRelativeView();

    /**
     * Create relative view file.
     *
     * @return view file
     */
    public abstract FileObject createRelativeView();

    /**
     * Get sub path to controller file.
     *
     * @see issue #19 controllers are located in
     * protected/controllers/frontend/{ControllerName}Controller.php views:
     * protected/views/frontend/{ControllerName}/render.php
     * @return subpath to controller
     */
    private String getSubPathToController() {
        return getSubDirectoryPathForController(controller);
    }

    /**
     * Get absolute view file.
     *
     * @return
     */
    public FileObject getAbsoluteView() {
        if (YiiViewPathSupport.isAbsoluteViewPath(actionId)) {
            return YiiViewPathSupport.getAbsoluteViewFile(actionId, controller);
        }
        return null;
    }

    /**
     * Create absolute view file.
     *
     * @return
     */
    public FileObject createAbsoluteView() {
        if (YiiViewPathSupport.isAbsoluteViewPath(actionId)) {
            return YiiViewPathSupport.createAbsoluteViewFile(actionId, controller);
        }
        return null;
    }
}
