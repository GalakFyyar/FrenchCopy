import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Controller{
	private static JFrame frame = new JFrame();
	private static Map<String, Variable> variableHashMap = new HashMap<>();
	
	static{
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	static void throwErrorMessage(String err){
		JOptionPane.showMessageDialog(frame, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static boolean parseASCFile(File file) {
		return Parser.parse(file);
		
	}
	
	static void addVariable(String variable, String label, String shortLabel, ArrayList<String[]> choices){
	
	}
	
	static class Variable{
		String variableName;
		String longLabel;
		String shortLabel;
		ArrayList<String> choiceLabels;
		
		public Variable(String variableName, String longLabel, String shortLabel, ArrayList<String> choiceLabels) {
			this.variableName = variableName;
			this.longLabel = longLabel;
			this.shortLabel = shortLabel;
			this.choiceLabels = choiceLabels;
		}
	}
}
