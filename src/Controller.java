import javax.swing.JOptionPane;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

class Controller{
	private static Map<String, Variable> variableHashMap = new HashMap<>();
	
	static void throwErrorMessage(String err){
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static Map<String, Variable> parseASCFile(File file) {
		boolean ok = Parser.parseASC(file);
		if(!ok)
			return null;
		
		variableHashMap.entrySet().removeIf(v -> v.getValue().longLabelEnglish.isEmpty());
		variableHashMap.remove("LANG");
		
		System.out.println(variableHashMap.size());
		
		return variableHashMap;
	}
	
	static void addToMap(boolean french, String variableName, String label, String shortLabel, int codeWidth, Map<String, String> choices){
		if(!french)
			variableHashMap.put(variableName, new Variable(variableName, label, shortLabel, codeWidth, choices));
		else{
			System.out.println(variableName);
			Variable var = variableHashMap.get(variableName);
			var.setFrench(label, shortLabel, choices);
		}
	}
}
