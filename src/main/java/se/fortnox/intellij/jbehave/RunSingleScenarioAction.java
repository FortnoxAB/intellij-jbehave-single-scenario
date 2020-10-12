package se.fortnox.intellij.jbehave;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.icons.AllIcons;

public class RunSingleScenarioAction extends JbehaveSingleScenarioAction {
	public RunSingleScenarioAction() {
		super("Run this scenario", "Run single scenario", AllIcons.Actions.Execute);
	}

	@Override
	protected Executor getExecutorInstance() {
		return DefaultRunExecutor.getRunExecutorInstance();
	}
}