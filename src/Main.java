import java.io.File;
import java.util.Map;

public class Main{
	public static void main(String[] args){
        System.out.println("START");
	    //String filePath = "G:\\PCO\\D160\\D160.ASC";
	    String ascFilePath = "D160.ASC";
	    String enterFilePath = "D113.e";
		
		File ascFile = new File(ascFilePath);
		boolean parseASCFileOk = Controller.parseASCFile(ascFile);
		
		if(!parseASCFileOk){
			System.out.println("ASC file parse not successful");
			return;
		}
		
		Map<String, Variable> englishVariables = Controller.getVariableHashMapEN();
		Map<String, Variable> frenchVariables = Controller.getVariableHashMapFR();
		
		File enterFile = new File(enterFilePath);
		boolean parseEnterFileOk = Controller.parseEnterFile(enterFile);
		
		if(!parseEnterFileOk){
			System.out.println("Enter file parse not successful");
			return;
		}
		
		//Controller.write(file);
		
		System.out.println("END");
	}
}
