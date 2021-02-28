package se.fortnox.intellij.jbehave;

public class ScenarioUtils {
	public static String scenarioFilterFromName(String scenarioName) {
		return scenarioName.replaceAll("[[^\\p{ASCII}][\\-()%]]+", "*");
	}

	public static String formatTrimmedName(String scenario) {
		if (scenario.length() > 80) {
			return scenario.substring(0, 80) + " ...";
		}

		return scenario;
	}
}
