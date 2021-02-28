package se.fortnox.intellij.jbehave;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class RunSingleScenarioAction extends JbehaveSingleScenarioAction {
	public RunSingleScenarioAction() {
		super("Run This Scenario",
			"Run single scenario",
			AllIcons.Actions.Execute,
			null,
			null);
	}

	public RunSingleScenarioAction(@NotNull String scenarioName, @NotNull VirtualFile storyFile) {
		super("Run '" + ScenarioUtils.formatTrimmedName(scenarioName) + "'",
			"Run single scenario",
			AllIcons.Actions.Execute,
			scenarioName,
			storyFile);
	}

	@Override
	protected Executor getExecutorInstance() {
		return DefaultRunExecutor.getRunExecutorInstance();
	}
}
