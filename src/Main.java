import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main{
	public static void main(String[] args){
        System.out.println("START");
	    //String filePath = "G:\\PCO\\D160\\D160.ASC";
	    //String ascFilePath = "D160.ASC";
	    String ascFilePath = "D113.ASC";
	    String enterFilePath = "D113.e";
	    //String enterFilePath = "D1132.e";
		
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
		
		ArrayList<ArrayList<String>> buffer = new ArrayList<>();
		String line;
		while(sc.hasNextLine()){
			line = sc.nextLine();
			if(line.startsWith("TABLE ")){
				ArrayList<String> table = new ArrayList<>();
				while(sc.hasNextLine() && !line.isEmpty()) {
					table.add(line);
					line = sc.nextLine();
				}
				buffer.add(table);
			}
		}
		
		for (ArrayList<String> table : buffer) {
			String sheetNameLine = table.stream().filter(s -> s.startsWith("T &wt")).findAny().orElse(null);
			if(sheetNameLine == null){
				System.out.println("Could not find sheet name in " + table.get(0));
				continue;
			}
			String sheetName = sheetNameLine.split(" ")[2];
			
			ArrayList<String> labels = table.stream().filter(s -> s.startsWith("T ") && !s.startsWith("T &wt")).collect(Collectors.toCollection(ArrayList::new));
			
			//System.out.println(labels.size());
			
			if(labels.size() == 1) {
				int indexOfLongLabel = table.indexOf(labels.get(0));
				
				Variable var = getVariable(frenchVariables, sheetName);
				if(var == null){
					System.err.println("Could not find variable " + sheetName);
					continue;
				}
				//System.out.println(var.longLabel);
				
				table.set(indexOfLongLabel, var.longLabel);
				setLongLabel(table, indexOfLongLabel, var.longLabel);
				
			}else if(labels.size() == 2){
				int indexOfLongLabel = table.indexOf(labels.get(0));
				int indexOfShortLabel = table.indexOf(labels.get(1));
				
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (ArrayList<String> table : buffer) {
			for (String s : table) {
				sb.append(s).append('\n');
			}
			sb.append('\n');
		}
		
		int extIndex = enterFilePath.lastIndexOf('.');
		String frenchEnterFilePath = enterFilePath.substring(0, extIndex) + "fr.e";
		Writer.writeEFile(frenchEnterFilePath, sb);
		
		System.out.println("END");
	}
	
	private static Variable getVariable(Map<String, Variable> map, String variableName){
		if(map.containsKey(variableName)) {
			return map.get(variableName);
		}
		
		String newVariableName = "Q" + variableName;
		if(map.containsKey(newVariableName)){
			return map.get(newVariableName);
		}
		
		return null;
	}
	
	private static void setLongLabel(ArrayList<String> table, int indexOfLongLabel, String longLabel) {
		String[] titles = longLabel.split("\n");
		table.remove(indexOfLongLabel);
		
		int currentLongLabelIndex = indexOfLongLabel;
		for (String title : titles) {
			if (title.isEmpty())
				continue;
			
			table.add(currentLongLabelIndex, "T " + title);
			currentLongLabelIndex++;
		}
	}
}
