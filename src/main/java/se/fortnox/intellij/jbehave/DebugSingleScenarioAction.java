package se.fortnox.intellij.jbehave;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.icons.AllIcons;

public class DebugSingleScenarioAction extends JbehaveSingleScenarioAction {

	public DebugSingleScenarioAction() {
		super("Debug This Scenario", "Debug single scenario", AllIcons.Actions.StartDebugger);
	}

	@Override
	protected Executor getExecutorInstance() {
		return DefaultDebugExecutor.getDebugExecutorInstance();
	}
}