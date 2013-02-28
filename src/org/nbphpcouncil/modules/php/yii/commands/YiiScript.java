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
package org.nbphpcouncil.modules.php.yii.commands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.nbphpcouncil.modules.php.yii.ui.options.YiiOptions;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author junichi11
 */
public class YiiScript {

    public static final String YII_SCRIPT_NAME = "yiic"; // NOI18N
    public static final String YII_SCRIPT_NAME_LONG = YII_SCRIPT_NAME + ".php"; // NOI18N
    private static final String WEBAPP_COMMAND = "webapp"; // NOI18N
    private String yiicPath;
    public static final String OPTIONS_SUB_PATH = "Yii"; // NOI18N

    private YiiScript(String yiicPath) {
        this.yiicPath = yiicPath;
    }

    @NbBundle.Messages({
        "# {0} - error message",
        "YiiScript.script.invalid=<html>Project''s Yii script is not valid.<br>({0})"
    })
    public static YiiScript forPhpModule(PhpModule phpModule, boolean warn) throws InvalidPhpExecutableException {
        String yiiPath = YiiOptions.getInstance().getYiiScript();
        String error = validate(yiiPath);
        if (error == null) {
            return new YiiScript(yiiPath);
        }
        if (warn) {
            NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                    Bundle.YiiScript_script_invalid(error),
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(message);
        }
        throw new InvalidPhpExecutableException(error);
    }

    /**
     * Create project with yiic webapp command.
     *
     * @param phpModule
     * @return true if plugin can create a yii project, otherwise false
     */
    public boolean initProject(PhpModule phpModule) {
        FileObject projectDirectory = phpModule.getProjectDirectory();

        List<String> params = new ArrayList<String>();
        params.add(WEBAPP_COMMAND);
        params.add(projectDirectory.getName());
        try {
            createPhpExecutable(phpModule)
                    .additionalParameters(params)
                    .runAndWait(getInitProjectDescriptor(), PhpExecutable.ANSI_STRIPPING_FACTORY, WEBAPP_COMMAND);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return YiiUtils.isYii(phpModule);
    }

    /**
     * ExecutionDescriptor For initProject
     *
     * @return ExecutionDescriptor
     */
    private ExecutionDescriptor getInitProjectDescriptor() {
        String yes = "yes\n"; // NOI18N
        InputStream in = new ByteArrayInputStream(yes.getBytes());

        return PhpExecutable.DEFAULT_EXECUTION_DESCRIPTOR.inputOutput(new YiiScriptInputOutput(new InputStreamReader(in)));
    }

    @NbBundle.Messages("YiiScript.script.label=Yii script")
    public static String validate(String command) {
        return PhpExecutableValidator.validateCommand(command, Bundle.YiiScript_script_label());
    }

    /**
     * Create PhpExecutable. working directory is parent directory of project
     * directory.
     *
     * @param phpModule
     * @return PhpExecutable
     */
    private PhpExecutable createPhpExecutable(PhpModule phpModule) {
        return new PhpExecutable(yiicPath)
                .workDir(FileUtil.toFile(phpModule.getProjectDirectory().getParent()));

    }

    /**
     * Get option path.
     *
     * @return option path
     */
    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + getOptionsSubPath(); // NOI18N
    }

    /**
     * Get option sub path.
     *
     * @return option sub path
     */
    public static String getOptionsSubPath() {
        return OPTIONS_SUB_PATH;
    }

    //~ Inner class
    private class YiiScriptInputOutput implements InputOutput {

        private final InputOutput io;
        private Reader in;

        public YiiScriptInputOutput(Reader in) {
            io = IOProvider.getDefault().getIO("Yii Framework", false); // NOI18N
            this.in = in;
        }

        @Override
        public OutputWriter getOut() {
            return io.getOut();
        }

        @Override
        public Reader getIn() {
            if (in == null) {
                return io.getIn();
            }
            return in;
        }

        @Override
        public OutputWriter getErr() {
            return io.getErr();
        }

        @Override
        public void closeInputOutput() {
            io.closeInputOutput();
        }

        @Override
        public boolean isClosed() {
            return io.isClosed();
        }

        @Override
        public void setOutputVisible(boolean value) {
            io.setOutputVisible(value);
        }

        @Override
        public void setErrVisible(boolean value) {
            io.setErrVisible(value);
        }

        @Override
        public void setInputVisible(boolean value) {
            io.setInputVisible(value);
        }

        @Override
        public void select() {
            io.select();
        }

        @Override
        public boolean isErrSeparated() {
            return io.isErrSeparated();
        }

        @Override
        public void setErrSeparated(boolean value) {
            io.setErrSeparated(value);
        }

        @Override
        public boolean isFocusTaken() {
            return io.isFocusTaken();
        }

        @Override
        public void setFocusTaken(boolean value) {
            io.setFocusTaken(value);
        }

        @Override
        public Reader flushReader() {
            return io.flushReader();
        }

        public void setIn(Reader in) {
            this.in = in;
        }
    }
}
