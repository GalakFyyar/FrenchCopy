import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Controller{
	private static Map<String, Variable> variableHashMap = new HashMap<>();
	
	static void throwErrorMessage(String err){
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static boolean parseASCFile(File file) {
		boolean ok = Parser.parse(file);
		
		for(Variable v : variableHashMap.values()){
			System.out.println(v.variableName + " -- " + v.longLabel);
		}
		
		return ok;
	}
	
	static void addVariable(String variable, String label, String shortLabel, ArrayList<String> choices){
		variableHashMap.put(variable, new Variable(variable, label, shortLabel, choices));
	}
	
	static class Variable{
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
}
