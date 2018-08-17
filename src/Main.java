import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {
		System.out.println("START");
		String ascFilePath = "D126.ASC";
		String enterFilePath = "D126_CUMULATIVE.e";
		String enterFileOutput = "Q02F_test.e";
		
		File ascFile = new File(ascFilePath);
		Map<String, Variable> variableMap = Controller.parseASCFile(ascFile);
		
		if (variableMap == null) {
			System.err.println("ASC file parse not successful");
			return;
		}
		
		File enterFile = new File(enterFilePath);
		Scanner sc;
		try {
			sc = new Scanner(enterFile);
		} catch (FileNotFoundException e) {
			System.err.println("Can't open or read Enter File");
			return;
		}
		
		ArrayList<ArrayList<String>> buffer = new ArrayList<>();
		String line;
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.startsWith("TABLE ")) {
				ArrayList<String> table = new ArrayList<>();
				while (sc.hasNextLine() && !line.isEmpty()) {
					table.add(line);
					line = sc.nextLine();
				}
				buffer.add(table);
			}
			if(line.equals("*##"))
				break;
		}
		
		for (ArrayList<String> table : buffer) {
			String sheetNameLine = table.stream().filter(s -> s.startsWith("T &wt")).findAny().orElse(null);
			if (sheetNameLine == null) {
				System.err.println("Could not find sheet name in " + table.get(0));
				continue;
			}
			String sheetName = sheetNameLine.split(" ")[2];
			
			//Summary rows
			if (sheetName.endsWith("_SUMMARY")) {
				ArrayList<String> rows = filter(table, s -> s.startsWith("R ") && !s.startsWith("R null"));
				
				String variableName = sheetName.substring(0, sheetName.indexOf('_'));
				for (String row : rows) {
					String subQuestionLetter = row.substring(2, row.indexOf('.'));
					String subQuestionVariableName = variableName + subQuestionLetter;
					
					Variable var = getVariable(variableMap, subQuestionVariableName);
					if (var == null) {
						System.err.println("Could not find variable for sub question " + subQuestionVariableName);
						continue;
					}
					
					String newRow = row.replace(row.substring(2, row.indexOf(';')), var.shortLabelFrench);
					int rowIndex = table.indexOf(row);
					table.set(rowIndex, newRow);
				}
				
				continue;       //go to next table
			}
			
			ArrayList<String> titles = filter(table, s -> s.startsWith("T "));
			ArrayList<String> labels = filter(titles, s -> !s.startsWith("T &wt"));
			
			//Find relevant variable
			Variable var = getVariable(variableMap, sheetName);
			if (var == null) {
				System.err.println("Could not find variable " + sheetName);
				continue;
			}
			
			//Set long label
			boolean hasShortLabel = !var.shortLabelEnglish.isEmpty();
			int numOfLongLabelLines = hasShortLabel ? labels.size() - 1 : labels.size();
			int startIndexOfLongLabel = table.indexOf(labels.get(0));
			setLongLabel(table, startIndexOfLongLabel, numOfLongLabelLines, var.longLabelFrench);
			
			//Set short label
			if (hasShortLabel) {
				int indexOfShortLabel = table.indexOf(labels.get(labels.size() - 1));       //short label is the last label
				table.set(indexOfShortLabel, "T " + var.shortLabelFrench);
			}
			
			Map<Integer, String> indexesOfChoices = getIndexesOfChoicesInTable(table, var.codeWidth);
			for (Integer index : indexesOfChoices.keySet()) {
				String code = indexesOfChoices.get(index);
				
				boolean codePresent = var.choicesFrench.containsKey(code);
				if(!codePresent){
					System.err.println("Code " + code + " not found in variable " + var.variableName);
					continue;
				}
				@NotNull String frenchChoiceLabel = var.choicesFrench.get(code);
				
				table.set(index, getFrenchChoiceRow(table, index, frenchChoiceLabel));
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (ArrayList<String> table : buffer) {
			for (String s : table) {
				sb.append(s).append('\n');
			}
			sb.append('\n');
		}
		
		Writer.writeEFile(enterFileOutput, sb);
		
		System.out.println("END");
	}
	
	private static Variable getVariable(Map<String, Variable> map, String variableName) {
		if (map.containsKey(variableName)) {
			return map.get(variableName);
		}
		
		String newVariableName = "Q" + variableName;
		if (map.containsKey(newVariableName)) {
			return map.get(newVariableName);
		}
		
		if (variableName.equals("TAX2")) {
			return map.get("QTAX2H");
		}
		
		return null;
	}
	
	//TODO: make pure, don't modify the table parameter
	private static void setLongLabel(ArrayList<String> table, int startIndexOfLongLabel, int longLabelLinesLength, String newTitlesString) {
		for (int i = 0; i < longLabelLinesLength; i++) {
			table.remove(startIndexOfLongLabel);
		}
		
		ArrayList<String> newTitlesList = new ArrayList<>();
		String[] titles = newTitlesString.split("\n");
		for (String title : titles) {
			if (title.isEmpty())
				continue;
			
			newTitlesList.add("T " + title);
		}
		
		table.addAll(startIndexOfLongLabel, newTitlesList);
	}
	
	private static <T> ArrayList<T> filter(ArrayList<T> a, Predicate<T> p) {
		return a.stream().filter(p).collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * Given a ArrayList of Strings, that contain choices with codes,
	 * returns a map of indexes -> codes
	 * the index key is the index of that row in the input table
	 * @param table the table containing the rows
	 * @param codeWidth the codeWidth of the Question
	 * @return a hashMap of indexes mapping to the choice codes
	 */
	@Contract(pure = true)
	private static Map<Integer, String> getIndexesOfChoicesInTable(@NotNull ArrayList<String> table, int codeWidth) {
		Map<Integer, String> choices = new HashMap<>();
		for (int i = 0; i < table.size(); i++) {
			String line = table.get(i);
			int codeStartIndex = (codeWidth > 1 ? line.lastIndexOf(',') : line.indexOf('-', line.indexOf(';'))) + 1;
			if(line.startsWith("R ") && !line.startsWith("R NET") && !line.endsWith("null") && !line.startsWith("R Mean; a(")){
				choices.put(i, line.substring(codeStartIndex, codeStartIndex + codeWidth));
			}
		}
		return choices;
	}
	
	/**
	 * Given a ArrayList of Strings, that contain choice labels with codes,
	 * returns a map of codes -> choiceLabels
	 * @param table the table containing the rows
	 * @param codeWidth the codeWidth of the Question
	 * @return a hashMap of codes mapping to the choiceLabels
	 */
	@Contract(pure = true)
	private static Map<String, String> getChoices(@NotNull ArrayList<String> table, int codeWidth) {
		ArrayList<String> rawRows = filter(table, s -> s.startsWith("R ") && !s.startsWith("R NET") && !s.endsWith("null") && !s.startsWith("R Mean; a("));
		Map<String, String> choices = new HashMap<>();
		
		rawRows.forEach(row -> {
			int labelEndIndex = row.indexOf(';');
			String choiceLabel = row.substring(2, labelEndIndex);
			String choiceCode;
			int codeStartIndex = (codeWidth > 1 ? row.lastIndexOf(',') : row.indexOf('-', labelEndIndex)) + 1;
			choiceCode = row.substring(codeStartIndex, codeStartIndex + codeWidth);
			
			choices.put(choiceCode, choiceLabel);
		});
		
		return choices;
	}
	
	private static String getFrenchChoiceRow(ArrayList<String> table, Integer index, String frenchChoiceLabel) {
		String row = table.get(index);
		int labelEndIndex = row.indexOf(';');
		return "R " + frenchChoiceLabel + row.substring(labelEndIndex);
	}
}
