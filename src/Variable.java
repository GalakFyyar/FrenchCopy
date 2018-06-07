import java.util.ArrayList;
class Variable {
	String variableName;
	String longLabel;
	String shortLabel;
	ArrayList<String> choiceLabels;
	
	Variable(String variableName, String longLabel, String shortLabel, ArrayList<String> choiceLabels) {
		this.variableName = variableName;
		this.longLabel = longLabel;
		this.shortLabel = shortLabel;
		this.choiceLabels = choiceLabels;
	}
}
