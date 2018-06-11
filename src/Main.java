import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main{
	public static void main(String[] args){
        System.out.println("START");
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
			
			//Summary rows
			if(sheetName.endsWith("_SUMMARY")){
				ArrayList<String> rows = filter(table, s -> s.startsWith("R ") && !s.startsWith("R null"));
				
				String variableName = sheetName.substring(0, sheetName.indexOf('_'));
				for (String row : rows) {
					String subq = row.substring(2, row.indexOf('.'));
					String variableNameForSubQ = variableName + subq;
					
					Variable var = getVariable(frenchVariables, variableNameForSubQ);
					if(var == null){
						System.err.println("Could not find variable " + variableNameForSubQ);
						continue;
					}
					
					String newRow = row.replace(row.substring(2, row.indexOf(';')), var.shortLabel);
					int rowIndex = table.indexOf(row);
					table.set(rowIndex, newRow);
				}
				
				continue;
			}
			
			ArrayList<String> labels = filter(table, s -> s.startsWith("T ") && !s.startsWith("T &wt"));
			
			//Set long label
			Variable var = getVariable(frenchVariables, sheetName);
			if(var == null){
				System.err.println("Could not find variable " + sheetName);
				continue;
			}
			
			int indexOfLongLabel = table.indexOf(labels.get(0));
			table.set(indexOfLongLabel, var.longLabel);
			setLongLabel(table, indexOfLongLabel, var.longLabel);
			
			//Set short label
			if(labels.size() == 2){
				int indexOfShortLabel = table.indexOf(labels.get(1));
				table.set(indexOfShortLabel, "T " + var.shortLabel);
			}
			
//			if(!(var.codeWidth == 1 || var.codeWidth == 2)){
//				System.err.println("Bad Code Width in " + var.variableName + " " + var.codeWidth);
//				continue;
//			}
			
			ArrayList<String> choiceLabels = getChoiceLabels(table);
			ArrayList<String> frenchChoiceLabels = new ArrayList<>(var.choices.values());
			ArrayList<String> englishChoiceLabels = new ArrayList<>(englishVariables.get(var.variableName).choices.values());
			
			for(String choiceLabel : choiceLabels) {
				
				
				int choiceLabelIndex = englishChoiceLabels.indexOf(choiceLabel);
				if(choiceLabelIndex == -1){
					System.err.println("could not find " + choiceLabel + "\t" + var.variableName);
					continue;
				}
				String frenchChoiceLabel = frenchChoiceLabels.get(choiceLabelIndex);
				
				//System.out.println(choiceLabel);
				String choiceRow = filter(table, s -> s.contains(choiceLabel)).get(0);
				int choiceRowIndexInTable = table.indexOf(choiceRow);
				
				table.set(choiceRowIndexInTable, choiceRow.replace(choiceLabel, frenchChoiceLabel));
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
		
		if(variableName.equals("TAX2")){
			return map.get("QTAX2H");
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
	
	private static <T> ArrayList<T> filter(ArrayList<T> a, Predicate<T> p){
		return a.stream().filter(p).collect(Collectors.toCollection(ArrayList::new));
	}
	
	private static Map<String, String> getRows(ArrayList<String> table, int codeWidth) {
		ArrayList<String> rawRows = filter(table, s -> s.startsWith("R ") && !s.startsWith("R NET"));
		Map<String, String> choices = new HashMap<>();
		
		rawRows.forEach(row -> {
			int labelEndIndex = row.indexOf(';');
			String choiceLabel = row.substring(2, labelEndIndex);
			String choiceCode;
			int codeStartIndex;
			codeStartIndex = codeWidth > 1 ? row.indexOf(',', labelEndIndex) : row.indexOf('-', labelEndIndex);
			choiceCode = row.substring(codeStartIndex, codeStartIndex + codeWidth);
			
			choices.put(choiceCode, choiceLabel);
		});
		
		return choices;
	}
	
	private static ArrayList<String> getChoiceLabels(ArrayList<String> table){
		ArrayList<String> rawRows = filter(table, s -> s.startsWith("R ") && !s.startsWith("R NET") && !s.startsWith("R Mean"));
		ArrayList<String> choiceLabels = new ArrayList<>();
		rawRows.forEach(rr -> choiceLabels.add(rr.substring(2, rr.indexOf(';'))));
		choiceLabels.removeIf(String::isEmpty);
		return choiceLabels;
	}
}
