import javafx.util.Pair;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

class Controller{
	private static Map<String, Variable> variableHashMapEN = new HashMap<>();
	
	private static Map<String, Variable> variableHashMapFR = new HashMap<>();
	static void throwErrorMessage(String err){
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static Pair<Map<String, Variable>, Map<String, Variable>> parseASCFile(File file) {
		boolean ok = Parser.parseASC(file);
		if(!ok)
			return null;
		
		variableHashMapEN.entrySet().removeIf(v -> v.getValue().longLabel.isEmpty());
		variableHashMapFR.remove("LANG");
		
		//System.out.println(variableHashMapEN.size());
		//System.out.println(variableHashMapFR.size());
		
		return new Pair<>(variableHashMapEN, variableHashMapFR);
	}
	
	static void addVariable(boolean french, String variableName, String label, String shortLabel, int codeWidth, Map<String, String> choices){
		if(french)
			variableHashMapFR.put(variableName, new Variable(variableName, label, shortLabel, codeWidth, choices));
		else
			variableHashMapEN.put(variableName, new Variable(variableName, label, shortLabel, codeWidth, choices));
	}
}
