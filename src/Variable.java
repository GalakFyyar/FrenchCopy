import java.util.HashMap;
import java.util.Map;

class Variable {
	String variableName;
	String longLabelEnglish;
	String shortLabelEnglish;
	int codeWidth;
	Map<String, String> choicesEnglish;
	
	String longLabelFrench = "";
	String shortLabelFrench = "";
	Map<String, String> choicesFrench = new HashMap<>();
	
	Variable(String variableName, String longLabelEnglish, String shortLabelEnglish, int codeWidth, Map<String, String> choicesEnglish) {
		this.variableName = variableName;
		this.longLabelEnglish = longLabelEnglish;
		this.shortLabelEnglish = shortLabelEnglish;
		this.codeWidth = codeWidth;
		this.choicesEnglish = choicesEnglish;
	}
	
	void updateFrench(String longLabelFrench, String shortLabelFrench, Map<String, String> choicesFrench){
		this.longLabelFrench = longLabelFrench;
		this.shortLabelFrench = shortLabelFrench;
		this.choicesFrench = choicesFrench;
	}
}
