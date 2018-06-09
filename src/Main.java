import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

public class Main{
	public static void main(String[] args){
        System.out.println("START");
	    //String filePath = "G:\\PCO\\D160\\D160.ASC";
	    String ascFilePath = "D160.ASC";
	    String enterFilePath = "D113.e";
		
		File ascFile = new File(ascFilePath);
		Pair<Map<String, Variable>, Map<String, Variable>> maps = Controller.parseASCFile(ascFile);
		
		if(maps == null){
			System.out.println("ASC file parse not successful");
			return;
		}
		
		Map<String, Variable> englishVariables = maps.getKey();
		Map<String, Variable> frenchVariables = maps.getValue();
		
		
		File enterFile = new File(enterFilePath);
		Scanner sc;
		try {
			sc = new Scanner(enterFile);
		} catch (FileNotFoundException e) {
			System.out.println("Can't open or read Enter File");
			return;
		}
		
		int tableCount = 0;
		int variableCount = 0;
		
		while (sc.hasNextLine()){
			String line = sc.nextLine();
			if(line.startsWith("T &wt")){
				String sheetName = line.split(" ")[2];
				tableCount++;
				
				if(englishVariables.containsKey(sheetName)) {
					variableCount++;
					//System.out.println(sheetName);
				}else if(sheetName.endsWith("_SUMMARY")){
					tableCount--;
				}else if(englishVariables.containsKey("Q" + sheetName)){
					variableCount++;
				}else{
					System.out.println(sheetName);
					//break;
				}
			}
			
		}
		System.out.println(tableCount);
		System.out.println(variableCount);
		
		
		//Controller.write(file);
		
		System.out.println("END");
	}
}
