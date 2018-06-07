import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Controller{
	private static Map<String, Variable> variableHashMapEN = new HashMap<>();
	private static Map<String, Variable> variableHashMapFR = new HashMap<>();
	
	static void throwErrorMessage(String err){
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static boolean parseASCFile(File file) {
		boolean ok = Parser.parseASC(file);
		if(!ok)
			return false;
		
		variableHashMapEN.entrySet().removeIf(v -> v.getValue().longLabel.isEmpty());
		variableHashMapFR.remove("LANG");
		
		//System.out.println(variableHashMapEN.size());
		//System.out.println(variableHashMapFR.size());
		
		return true;
	}
	
	static void addVariable(boolean french, String variableName, String label, String shortLabel, ArrayList<String> choices){
		if(french)
			variableHashMapFR.put(variableName, new Variable(variableName, label, shortLabel, choices));
		else
			variableHashMapEN.put(variableName, new Variable(variableName, label, shortLabel, choices));
	}
	
	static Map<String, Variable> getVariableHashMapEN() {
		return variableHashMapEN;
	}
	
	static Map<String, Variable> getVariableHashMapFR() {
		return variableHashMapFR;
	}
	
	static boolean parseEnterFile(File file){
		boolean ok = Parser.parseEnter(file);
		return ok;
	}
}
