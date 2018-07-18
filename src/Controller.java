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
	
	static void addToMap(boolean french, String variableName, int codeWidth, String label, String shortLabel, Map<String, String> choices){
		if(!french)
			variableHashMap.put(variableName, new Variable(variableName, codeWidth, label, shortLabel, choices));
		else{
			System.out.println(variableName);
			Variable var = variableHashMap.get(variableName);
			var.setFrench(label, shortLabel, choices);
		}
	}
}
