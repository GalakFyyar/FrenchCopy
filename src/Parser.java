import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Predicate;

class Parser{
	//returns true for success, false otherwise
	static boolean parse(File ascFile){
		Scanner sc;
		try{
			sc = new Scanner(ascFile, "UTF-8");
		}catch(Exception e){
			Controller.throwErrorMessage("Can't open .ASC file\n" + e.getMessage());
			return false;
		}
		
		
		int quePosition = 0;
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		String line = sc.nextLine();
		while(true){
			String variable = "";
			int codeWidth = -1;
			String label = "";
			String shortLabel = "";
			String skipCondition = "";
			String ifDestination = "";
			String elseDestination = "";
			ArrayList<String[]> choices = new ArrayList<>();
			
			boolean tagConsumed = false;
			//This is the order these tags appear in .ASC files
			if(line.startsWith("*ME")){			//Message to the Interviewer Found
				sc.nextLine();//do nothing
				line = sc.nextLine();
			}
			if(line.startsWith("*LL")){			//Long Label Found
				tagConsumed = true;
				String rawVariable = line;
				StringBuilder rawLabel = new StringBuilder(sc.nextLine());
				line = sc.nextLine();
				
				while(!line.startsWith("*SL") && !line.startsWith("*MA") && !line.startsWith("*SK") && !line.startsWith("*CL")){ //Label continues for multiple lines
					rawLabel.append(line);
					line = sc.nextLine();
				}
				
				label = parseLabel(rawLabel.toString());
				variable = parseVariable(rawVariable);
				codeWidth = parseCodeWidth(rawVariable);
				
			}
			if(line.startsWith("*SL")){			//Short Label Found
				tagConsumed = true;
				String rawShortLabel = sc.nextLine();
				shortLabel = parseShortLabel(rawShortLabel);
				line = sc.nextLine();
			}
			if(line.startsWith("*MA")){			//Mask Found
				tagConsumed = true;
				sc.nextLine();//do nothing
				line = sc.nextLine();
			}
			if(line.startsWith("*SK")){			//Skip Found
				tagConsumed = true;
				String skipDestination = sc.nextLine();
				skipCondition = sc.nextLine();
				String[] destinations = parseSkipDestination(skipDestination);
				ifDestination = destinations[0];
				elseDestination = destinations[1];
				
				line = sc.nextLine();
			}
			if(line.startsWith("*CL")){			//Code List Found
				tagConsumed = true;
				line = sc.nextLine();
				ArrayList<String> rawChoices = new ArrayList<>();
				do{
					rawChoices.add(line);
					line = sc.nextLine();
				}while(!line.startsWith("---"));
				
				if(rawChoices.get(rawChoices.size() - 1).startsWith("*RC"))		//Rotate Choices found, remove
					rawChoices.remove(rawChoices.size() - 1);
				
				for(String rawChoice : rawChoices){
				    choices.add(parseChoice(rawChoice, codeWidth));
				}
				line = sc.nextLine();
			}
			if(line.startsWith("*BS")){
				sc.nextLine();			//?
			}
			if(line.startsWith("*LA")){				//Reached beginning of Second Language, stop parsing
				break;
			}
			
			
			//If no tag was consumed this indicates that the file is not a properly formatted .ASC file
			if(!tagConsumed){
				sc.close();
				Controller.throwErrorMessage("Could not parse .ASC file");
				return false;
			}
			
			Controller.addVariable(variable, label, shortLabel, choices);
			System.out.println(variable);
			quePosition++;
		}
		
		sc.close();
		
		return true;
	}
	
	private static String parseVariable(String rawVariable){
		return rawVariable.split(" ")[1];
	}
	
	private static int parseCodeWidth(String rawVariable){
		return Integer.parseInt(rawVariable.split(" ")[2].substring(2));
	}
	
	/**
	 * Parses rawLabel to a correctly formatted one.
	 * This is done by removing the leading and trailing square brackets,
	 * then replacing tabs with a space,
	 * then reducing consecutive spaces to one space
	 * then removing leading and trailing spaces,
	 * then replacing the weird unicode apostrophes with the ASCII apostrophe
	 * @param rawLabel the row label to be parsed
	 * @return the parsed version of the rawLabel
	 */
	private static String parseLabel(String rawLabel){
		return rawLabel.substring(1, rawLabel.length() - 1).replace('\t', ' ').replaceAll(" +", " ").trim().replace("\u00B4", "'");
	}
	
	private static String parseShortLabel(String rawShortLabel){
		return rawShortLabel.substring(1, rawShortLabel.length() - 1);
	}
	
	private static String[] parseSkipDestination(String skipDestination){
		String ifDestination;
		String elseDestination = "";
		
		int arrowOffset = 2;		//need because of arrow in .ASC files "->"
		int elseOffset = 6;			//need because of else text in .ASC files " ELSE "
		int elseSkipStartPos = skipDestination.indexOf(' ');
		if(elseSkipStartPos != -1){
			elseDestination = skipDestination.substring(elseSkipStartPos + elseOffset);
			ifDestination = skipDestination.substring(arrowOffset, elseSkipStartPos);
		}else{
			ifDestination = skipDestination.substring(arrowOffset);
		}
		
		return new String[]{ifDestination, elseDestination};
	}
	
	private static String[] parseChoice(String rawChoice, int codeWidth){
		if(rawChoice.startsWith("*RC"))		//Rotate Choice tag found.
			return null;
		
		int choiceLabelEndPos = rawChoice.indexOf(']');
		String choiceLabel = rawChoice.substring(1, choiceLabelEndPos).replace("\u2019", "'");	//remove surrounding brackets and fix apostrophe
		
		int codeStartPos = rawChoice.indexOf('[', choiceLabelEndPos) + 1;
		String code = rawChoice.substring(codeStartPos, codeStartPos + codeWidth);
		
		String skipToQuestion = "";
		int skipStartPos = rawChoice.indexOf('>', codeStartPos);
		if(skipStartPos != -1)
			skipToQuestion = rawChoice.substring(skipStartPos + 1, rawChoice.length());
		
		return new String[]{code, choiceLabel, skipToQuestion};
	}
}
