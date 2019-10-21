package se.fortnox.intellij.jbehave;

import com.google.common.base.CaseFormat;
import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import static java.util.Arrays.asList;

public class JbehaveSingleScenarioAction extends AnAction implements FileEditorProvider {

	private static final String SCENARIO_PREFIX = "Scenario:";

	public JbehaveSingleScenarioAction() {
		super("Debug this scenario");
	}

	public void actionPerformed(AnActionEvent e) {

		Project project = e.getProject();
		VirtualFile storyFile = getStoryFile(project);
		if (storyFile == null) {
			return;
		}
		String scenario = getScenarioName(e);
		if (scenario == null) {
			return;
		}
		PsiClass mainClass = getStoryClass(project, storyFile);

		JUnitConfiguration configuration = createJunitConfiguration(project, storyFile, scenario, mainClass);
		addRunnerConfiguration(project, configuration);

		executeJunit(project, configuration);
	}

	private void executeJunit(Project project, JUnitConfiguration configuration) {
		try {
			ExecutionEnvironmentBuilder.create(project, DefaultDebugExecutor.getDebugExecutorInstance(), configuration).buildAndExecute();
		} catch (ExecutionException ex) {
			Messages.showMessageDialog(project, "Failed debugging scenario: "+ex.getMessage()
					, getTemplatePresentation().getText(), Messages.getInformationIcon());
		}
	}

	private void addRunnerConfiguration(Project project, JUnitConfiguration configuration) {
		RunManager runManager = RunManager.getInstance(project);
		RunnerAndConfigurationSettingsImpl runnerAndConfigurationSettings = new RunnerAndConfigurationSettingsImpl((RunManagerImpl) runManager, configuration);
		runManager.addConfiguration(runnerAndConfigurationSettings, false);
	}

	@NotNull
	private JUnitConfiguration createJunitConfiguration(Project project, VirtualFile storyFile, String scenario, PsiClass mainClass) {
		JUnitConfiguration configuration = new JUnitConfiguration(storyFile.getPresentableName()+":"+scenario, project);
		configuration.setMainClass(mainClass);
		String filter = scenarioToFilter(scenario);
		configuration.setVMParameters(configuration.getVMParameters() + " -DmetaFilters=\"+scenario_title "+ filter +"\"");
		configuration.setBeforeRunTasks(asList(new CompileStepBeforeRun.MakeBeforeRunTask()));
		return configuration;
	}

	private PsiClass getStoryClass(Project project, VirtualFile storyFile) {
		PsiManager psiManager = PsiManager.getInstance(project);
		VirtualFile storyClassFile = LocalFileSystem.getInstance().findFileByPath(getStoryClassFile(storyFile));
		PsiFile psiFile = psiManager.findFile(storyClassFile);
		PsiJavaFile javaFile = (PsiJavaFile) psiFile;

		PsiClass[] classes = javaFile.getClasses();
		return classes[0];
	}

	private VirtualFile getStoryFile(Project project) {
		FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
		VirtualFile[] selectedFiles = fileEditorManager.getSelectedFiles();
		if (selectedFiles.length == 0) {
			return null;
		}
		if (!"story".equals(selectedFiles[0].getExtension())) {
			return null;
		}
		return selectedFiles[0];
	}

	private String getScenarioName(AnActionEvent e) {
		Editor editor = e.getData(CommonDataKeys.EDITOR);
		if (editor == null) {
			return null;
		}

		Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
		if (primaryCaret == null) {
			return null;
		}
		int start = primaryCaret.getSelectionStart();

		String text = editor.getDocument().getText();
		if (text == null) {
			return null;
		}

		return findScenario(text, start);
	}

	private String scenarioToFilter(String scenario) {
		return scenario.replaceAll("[åäöÅÄÖ\\-()]+", "*");
	}

	private String getStoryClassFile(VirtualFile file) {
		String path = file.getParent().getPath();
		path = path.replace("/src/test/resources","/src/test/java");
		path = path + "/" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, file.getName().replace(".story", ".java"));
		return path;
	}

	private String findScenario(String text, int start) {
		int scenarioStart = text.lastIndexOf(SCENARIO_PREFIX, start);
		if (scenarioStart == -1) {
			return null;
		}
		int end = text.indexOf("\n", scenarioStart);
		if (end == -1) {
			return null;
		}
		return text.substring(scenarioStart + SCENARIO_PREFIX.length(), end).trim();
	}

	@Override
	public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
		if (file.getExtension().equals("story")) {
			DefaultActionGroup editorMenu = (DefaultActionGroup) ActionManager.getInstance().getAction("EditorPopupMenu");
			editorMenu.add(this);
		}
		return false;
	}

	@Override
	public void update(@NotNull AnActionEvent e) {
		// define visibility
		e.getPresentation().setVisible(shouldShow(e));
	}

	private boolean shouldShow(AnActionEvent e) {
		Project project = e.getProject();
		if (project == null) {
			return false;
		}

		VirtualFile storyFile = getStoryFile(project);
		if (storyFile == null) {
			return false;
		}

		String scenario = getScenarioName(e);
		return scenario != null;
	}

	@NotNull
	@Override
	public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
		return null;
	}

	@NotNull
	@Override
	public String getEditorTypeId() {
		return null;
	}

	@NotNull
	@Override
	public FileEditorPolicy getPolicy() {
		return null;
	}
}
