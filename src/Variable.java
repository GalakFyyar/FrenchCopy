import java.util.Map;

class Variable {
	String variableName;
	String longLabel;
	String shortLabel;
	int codeWidth;
	Map<String, String> choices;
	
	/*
	String variableName;
	String longLabelEnglish;
	String longLabelFrench;
	String shortLabelEnglish;
	String shortLabelFrench;
	int codeWidth;
	Map<String, String> choicesEnglish;
	Map<String, String> choicesFrench;
	 */
	
	Variable(String variableName, String longLabel, String shortLabel, int codeWidth, Map<String, String> choices) {
		this.variableName = variableName;
		this.longLabel = longLabel;
		this.shortLabel = shortLabel;
		this.codeWidth = codeWidth;
		this.choices = choices;
	}
}
