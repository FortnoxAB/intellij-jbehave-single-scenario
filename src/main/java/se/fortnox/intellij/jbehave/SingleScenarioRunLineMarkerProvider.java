package se.fortnox.intellij.jbehave;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleScenarioRunLineMarkerProvider extends RunLineMarkerContributor {

	@Nullable
	@Override
	public Info getInfo(@NotNull PsiElement element) {
		PsiElement parent = element.getParent();
		if (parent instanceof ASTWrapperPsiElement
			&& element instanceof LeafPsiElement
			&& element.getText().startsWith(JbehaveSingleScenarioAction.SCENARIO_PREFIX)
		) {
			String      scenario  = JbehaveSingleScenarioAction.findScenario(parent.getText(), 0);
			VirtualFile storyFile = element.getContainingFile().getVirtualFile();

			if (scenario != null) {
				return new Info(
					AllIcons.Actions.Execute,
					null,
					new RunSingleScenarioAction(scenario, storyFile),
					new DebugSingleScenarioAction(scenario, storyFile)
				);
			}
		}

		return null;
	}
}
