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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbphpcouncil.modules.php.yii.YiiModule;
import org.nbphpcouncil.modules.php.yii.YiiModule.PATH_ALIAS;
import org.nbphpcouncil.modules.php.yii.YiiModuleFactory;
import org.nbphpcouncil.modules.php.yii.ui.options.YiiOptions;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.api.extexecution.input.LineProcessor;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
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
    private String yiicPath;
    public static final String OPTIONS_SUB_PATH = "Yii"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(YiiScript.class.getName());
    // commands
    private static final String SUBCOMMAND_PREFIX = "action"; // NOI18N
    private static final String COMMAND_SUFFIX = "Command"; // NOI18N
    private static final String HELP_COMMAND = "help"; // NOI18N
    private static final String MESSAGE_COMMAND = "message"; // NOI18N
    private static final String MIGRATE_COMMAND = "migrate"; // NOI18N
    private static final String SHELL_COMMAND = "shell"; // NOI18N
    private static final String WEBAPP_COMMAND = "webapp"; // NOI18N
    private static final List<String> DEFAULT_COMMANDS = Arrays.asList(MESSAGE_COMMAND, MIGRATE_COMMAND, SHELL_COMMAND, WEBAPP_COMMAND);
    // default params
    private static final List<String> DEFAULT_PARAMS = Collections.emptyList();

    private YiiScript(String yiicPath) {
        this.yiicPath = yiicPath;
    }

    @NbBundle.Messages({
        "# {0} - error message",
        "YiiScript.script.invalid=<html>Project''s Yii script is not valid.<br>({0})"
    })
    public static YiiScript forPhpModule(PhpModule phpModule, boolean warn) throws InvalidPhpExecutableException {
        return forPhpModule(phpModule, warn, false);
    }

    /**
     * Create instance.
     *
     * @param phpModule
     * @param warn
     * @param useProjectOption whether plguin uses the path of options panel
     * @return
     * @throws InvalidPhpExecutableException
     */
    public static YiiScript forPhpModule(PhpModule phpModule, boolean warn, boolean useProjectOption) throws InvalidPhpExecutableException {
        String yiiPath = ""; // NOI18N
        if (useProjectOption) {
            yiiPath = YiiOptions.getInstance().getYiiScript();
        } else {
            YiiModule yiiModule = YiiModuleFactory.create(phpModule);
            FileObject webroot = yiiModule.getWebroot();
            if (webroot != null) {
                FileObject yiic = webroot.getFileObject("protected/" + YII_SCRIPT_NAME_LONG);
                if (yiic != null) {
                    try {
                        yiiPath = FileUtil.toFile(yiic).getCanonicalPath();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
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
     * Run command.
     *
     * @param phpModule
     * @param parameters
     * @param postExecution
     */
    public void runCommand(PhpModule phpModule, List<String> parameters, Runnable postExecution) {
        createPhpExecutable(phpModule)
            .displayName(getDisplayName(phpModule, parameters.get(0)))
            .additionalParameters(getAllParams(parameters))
            .run(getDescriptor(postExecution));
    }

    /**
     * Get commands.
     *
     * @param phpModule
     * @return command list
     */
    public List<FrameworkCommand> getCommands(PhpModule phpModule) {
        List<FrameworkCommand> commands = new ArrayList<FrameworkCommand>();
        commands.add(new YiiFrameworkCommand(phpModule, HELP_COMMAND, HELP_COMMAND, HELP_COMMAND));
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        List<FileObject> commandFiles = new LinkedList<FileObject>();

        // get core commands
        FileObject coreCommandsDirectory = yiiModule.getFileObject(PATH_ALIAS.SYSTEM, "cli/commands"); // NOI18N
        if (coreCommandsDirectory != null) {
            addCommands(coreCommandsDirectory, commandFiles);
        } else {
            // add default commands
            for (String command : DEFAULT_COMMANDS) {
                commands.add(new YiiFrameworkCommand(phpModule, command, command, command));
            }
        }

        // get application commands
        FileObject appCommandsDirectory = yiiModule.getFileObject(PATH_ALIAS.APPLICATION, "commands"); // NOI18N
        if (appCommandsDirectory != null) {
            addCommands(appCommandsDirectory, commandFiles);
        }

        // sort
        YiiUtils.sort(commandFiles);
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (FileObject commandFile : commandFiles) {
            // add command
            String commandName = commandFile.getName().replace(COMMAND_SUFFIX, "").toLowerCase(); // NOI18N
            commands.add(new YiiFrameworkCommand(phpModule, commandName, commandName, commandName));

            // add sub commands
            Collection<PhpClass> phpClasses = editorSupport.getClasses(commandFile);
            for (PhpClass phpClass : phpClasses) {
                Collection<PhpClass.Method> methods = phpClass.getMethods();
                PhpClass.Method[] methodArray = methods.toArray(new PhpClass.Method[]{});
                Arrays.sort(methodArray, new Comparator<PhpClass.Method>() {
                    @Override
                    public int compare(PhpClass.Method m1, PhpClass.Method m2) {
                        return m1.getName().compareToIgnoreCase(m2.getName());
                    }
                });
                for (PhpClass.Method method : methodArray) {
                    String methodName = method.getName();
                    if (!methodName.startsWith(SUBCOMMAND_PREFIX)) {
                        continue;
                    }
                    String subCommand = methodName.replace(SUBCOMMAND_PREFIX, "").toLowerCase(); // NOI18N
                    String fullCommand = commandName + " " + subCommand; // NOI18N
                    commands.add(new YiiFrameworkCommand(phpModule, new String[]{commandName, subCommand}, fullCommand, fullCommand));
                }
                break;
            }

        }

        return commands;
    }

    /**
     * Add command files of commands directory to list.
     *
     * @param folder
     * @param commandFiles
     */
    private void addCommands(FileObject folder, List<FileObject> commandFiles) {
        if (!folder.isFolder()) {
            return;
        }
        for (FileObject child : folder.getChildren()) {
            if (!child.isFolder() && FileUtils.isPhpFile(child) && child.getName().endsWith(COMMAND_SUFFIX)) {
                commandFiles.add(child);
            }
        }
    }

    /**
     * Get descriptor.
     *
     * @param postExecution
     * @return ExecutionDescriptor
     */
    private ExecutionDescriptor getDescriptor(Runnable postExecution) {
        ExecutionDescriptor executionDescriptor = PhpExecutable.DEFAULT_EXECUTION_DESCRIPTOR
            .optionsPath(getOptionsPath());
        if (postExecution != null) {
            executionDescriptor = executionDescriptor.postExecution(postExecution);
        }
        return executionDescriptor;
    }

    /**
     * Get help.
     *
     * @param phpModule
     * @param params
     * @return help text
     */
    public String getHelp(PhpModule phpModule, String[] params) {
        assert phpModule != null;

        // no help commands
        if (params.length < 1) {
            return ""; // NOI18N
        }

        List<String> allParams = new ArrayList<String>();
        allParams.add(HELP_COMMAND);
        allParams.add(params[0]);

        HelpLineProcessor lineProcessor = new HelpLineProcessor();
        Future<Integer> result = createPhpExecutable(phpModule)
            .displayName(getDisplayName(phpModule, allParams.get(0)))
            .additionalParameters(getAllParams(allParams))
            .run(getSilentDescriptor(), getOutProcessorFactory(lineProcessor));
        try {
            if (result != null) {
                result.get();
            }
        } catch (CancellationException ex) {
            // canceled
        } catch (ExecutionException ex) {
            UiUtils.processExecutionException(ex, OPTIONS_SUB_PATH);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return lineProcessor.getHelp();
    }

    /**
     * Get all params.
     *
     * @param params
     * @return
     */
    private List<String> getAllParams(List<String> params) {
        List<String> allParams = new ArrayList<String>();
        allParams.addAll(DEFAULT_PARAMS);
        allParams.addAll(params);
        return allParams;
    }

    /**
     * Get display name.
     *
     * @param phpModule
     * @param command
     * @return
     */
    @NbBundle.Messages({
        "# {0} - project name",
        "# {1} - command",
        "YiiScript.command.title={0} ({1})"
    })
    private String getDisplayName(PhpModule phpModule, String command) {
        return Bundle.YiiScript_command_title(phpModule.getDisplayName(), command);
    }

    /**
     * Get InputProcessFactory.
     *
     * @param lineProcessor
     * @return
     */
    private ExecutionDescriptor.InputProcessorFactory getOutProcessorFactory(final LineProcessor lineProcessor) {
        return new ExecutionDescriptor.InputProcessorFactory() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return InputProcessors.ansiStripping(InputProcessors.bridge(lineProcessor));
            }
        };
    }

    /**
     * Get silent descriptor.
     *
     * @return ExecutionDescriptor
     */
    private ExecutionDescriptor getSilentDescriptor() {
        return new ExecutionDescriptor()
            .inputOutput(InputOutput.NULL);
    }

    /**
     * Create project with yiic webapp command.
     *
     * @param phpModule
     * @return true if plugin can create a yii project, otherwise false
     */
    public boolean initProject(PhpModule phpModule) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            LOGGER.log(Level.WARNING, "Not found source directory!");
            return false;
        }

        List<String> params = new ArrayList<String>();
        params.add(WEBAPP_COMMAND);
        params.add(sourceDirectory.getName());
        try {
            createPhpExecutableForNewProject(phpModule)
                .additionalParameters(params)
                .runAndWait(getInitProjectDescriptor(), PhpExecutable.ANSI_STRIPPING_FACTORY, WEBAPP_COMMAND);
        } catch (ExecutionException ex) {
            LOGGER.log(Level.WARNING, "Failed to excute php, please check yiic path or php interpriter on option panel");
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
     * directory. Use when user creates new project.
     *
     * @param phpModule
     * @return PhpExecutable
     */
    private PhpExecutable createPhpExecutableForNewProject(PhpModule phpModule) {
        return new PhpExecutable(yiicPath)
            .workDir(FileUtil.toFile(phpModule.getSourceDirectory().getParent()));
    }

    /**
     * Create PhpExecutable. working directory is webroot directory.
     *
     * @param phpModule
     * @return
     */
    private PhpExecutable createPhpExecutable(PhpModule phpModule) {
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        FileObject webroot = yiiModule.getWebroot();
        return new PhpExecutable(yiicPath)
            .workDir(FileUtil.toFile(webroot));
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

    //~ Inner classes
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

    private static class HelpLineProcessor implements LineProcessor {

        private StringBuilder sb = new StringBuilder();

        @Override
        public void processLine(String line) {
            sb.append(line);
            sb.append("\n"); // NOI18N
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

        public String getHelp() {
            return sb.toString();
        }
    }
}
