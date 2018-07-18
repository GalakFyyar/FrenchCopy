import java.util.HashMap;
import java.util.Map;

class Variable {
	String variableName;
	int codeWidth;
	String longLabelEnglish;
	String shortLabelEnglish;
	Map<String, String> choicesEnglish;
	
	String longLabelFrench = "";
	String shortLabelFrench = "";
	Map<String, String> choicesFrench = new HashMap<>();
	
	Variable(String variableName, int codeWidth, String longLabelEnglish, String shortLabelEnglish, Map<String, String> choicesEnglish) {
		this.variableName = variableName;
		this.codeWidth = codeWidth;
		this.longLabelEnglish = longLabelEnglish;
		this.shortLabelEnglish = shortLabelEnglish;
		this.choicesEnglish = choicesEnglish;
	}
	
	void setFrench(String longLabelFrench, String shortLabelFrench, Map<String, String> choicesFrench){
		this.longLabelFrench = longLabelFrench;
		this.shortLabelFrench = shortLabelFrench;
		this.choicesFrench = choicesFrench;
	}
}
