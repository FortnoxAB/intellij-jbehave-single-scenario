package se.fortnox.intellij.jbehave;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScenarioUtilsTest {
	public static class ScenarioFilterFromName {
		@Test
		public void shallReplaceNonAsciiCharactersWithWildcards() {
			assertEquals("Testing r*ksm*rg*sar", run("Testing räksmörgåsar"));
		}

		@Test
		public void shallReplaceCertainOtherCharactersWithWildcards() {
			assertEquals("Testing * * * *", run("Testing ( ) - %"));
		}

		@Test
		public void shallReplaceConsecutiveCharactersWithSingleWildcard() {
			assertEquals("Testing * Testing", run("Testing åäö()-% Testing"));
		}

		private String run(String str) {
			return ScenarioUtils.scenarioFilterFromName(str);
		}
	}
}