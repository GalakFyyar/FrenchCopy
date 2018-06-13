import java.io.File;
import java.util.*;

class Parser{
	//returns true for success, false otherwise
	static boolean parseASC(File ascFile){
		Scanner sc;
		try{
			sc = new Scanner(ascFile, "UTF-8");
		}catch(Exception e){
			Controller.throwErrorMessage("Can't open .ASC file\n" + e.getMessage());
			return false;
		}
		
		boolean french = false;
		boolean nextVarFrench = false;
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		String line = sc.nextLine();
		while(sc.hasNextLine()){
			String variable = "";
			int codeWidth = -1;
			String label = "";
			String shortLabel = "";
			Map<String, String> choices = new LinkedHashMap<>();
			
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
				
				while(!line.startsWith("*LL") && !line.startsWith("*SL") && !line.startsWith("*MA") && !line.startsWith("*SK") && !line.startsWith("*CL")){ //Label continues for multiple lines
					rawLabel.append('\n').append(line);
					line = sc.nextLine();
				}
				
				label = parseLabel(rawLabel.toString());
				variable = parseVariable(rawVariable);
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
				sc.nextLine();                  //do nothing
				sc.nextLine();
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
					String[] choice = parseChoice(rawChoice);
					choices.put(choice[1], choice[0]);
				}
				line = sc.nextLine();
			}
			if(line.startsWith("*BS")){			//Begin Screen Found
				tagConsumed = true;
				do{
					sc.nextLine();                    //do nothing with End Screen tag "*ES"
					line = sc.nextLine();
				}while(!line.startsWith("---") && !line.startsWith("*SE"));
				
				line = sc.nextLine();
			}
			if(line.startsWith("*LA")){			//Reached beginning of Second Language, switch to french or stop parsing
				tagConsumed = true;
				
				nextVarFrench = true;
				sc.nextLine();					//consume "-----"
				line = sc.nextLine();
			}
			if(line.startsWith("*SE") || line.startsWith("*RO") || line.startsWith("*PR") || line.startsWith("*QF")){
				tagConsumed = true;
				line = sc.nextLine();
			}
			
			//System.out.println(variable);
			
			//If no tag was consumed this indicates that the file is not a properly formatted .ASC file
			if(!tagConsumed){
				sc.close();
				Controller.throwErrorMessage("Could not parseASC .ASC file");
				return false;
			}
			
			if(!variable.isEmpty()){
				Controller.addToMap(french, variable, label, shortLabel, codeWidth, choices);
			}
			
			if(nextVarFrench){
				nextVarFrench = false;
				french = true;
			}
		}
		
		sc.close();
		
		return true;
	}
	
	private static String parseVariable(String rawVariable){
		return rawVariable.split(" ")[1];
	}
	
	/**
	 * Parses rawLabel to a correctly formatted one.
	 * This is done by removing the leading and trailing square brackets,
	 * then replacing tabs with a space,
	 * then reducing consecutive spaces to one space,
	 * then removing leading and trailing spaces,
	 * then replacing the weird unicode apostrophes with the ASCII apostrophe.
	 * @param rawLabel the row label to be parsed
	 * @return the parsed version of the rawLabel
	 */
	private static String parseLabel(String rawLabel){
		return rawLabel.substring(1, rawLabel.length() - 1).replace('\t', ' ').replaceAll(" +", " ").trim().replace("\u00B4", "'");
	}
	
	private static String parseShortLabel(String rawShortLabel){
		return rawShortLabel.substring(1, rawShortLabel.length() - 1);
	}
	
	private static String[] parseChoice(String rawChoice){
		int choiceLabelEndPos = rawChoice.indexOf(']');
		String choiceLabel = rawChoice.substring(1, choiceLabelEndPos).replace("\u2013", "-").replace("\u2019", "'").replaceAll("/", "//").replaceAll(" +", " ");
		
		int codeStartPos = rawChoice.indexOf('[', choiceLabelEndPos) + 1;
		String code = rawChoice.substring(codeStartPos, rawChoice.indexOf(']', codeStartPos));
		
		return new String[]{choiceLabel, code};
	}
}
