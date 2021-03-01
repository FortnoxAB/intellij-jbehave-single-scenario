package se.fortnox.intellij.jbehave;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class DebugSingleScenarioAction extends JbehaveSingleScenarioAction {

	public DebugSingleScenarioAction() {
		super("Debug This Scenario",
			"Debug single scenario",
			AllIcons.Actions.StartDebugger,
			null,
			null);
	}

	public DebugSingleScenarioAction(@NotNull String scenarioName, @NotNull VirtualFile storyFile) {
		super("Debug '" + ScenarioUtils.formatTrimmedName(scenarioName) + "'",
			"Debug single scenario",
			AllIcons.Actions.StartDebugger,
			scenarioName,
			storyFile);
	}

	@Override
	protected Executor getExecutorInstance() {
		return DefaultDebugExecutor.getDebugExecutorInstance();
	}
}
