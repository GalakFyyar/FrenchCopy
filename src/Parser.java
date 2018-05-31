import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

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
			if(line.startsWith("---")){				//Reached end, stop parsing
				break;
			}
			
			boolean labelFound = false;
			//This is the order these commands appear in .ASC files
			if(line.startsWith("*ME")){			//Message to the Interviewer Found
				sc.nextLine();//do nothing
				line = sc.nextLine();
			}
			if(line.startsWith("*LL")){			//Long Label Found
				labelFound = true;
				String rawVariable = line;
				StringBuilder labelBuilder = new StringBuilder(sc.nextLine());
				while(!labelBuilder.toString().endsWith("]")){		//if multiple lines
					line = sc.nextLine();
					
					if(line.length() < 8)
						labelBuilder.append(line);
					else
						labelBuilder.append("\n").append(line);
				}
				String rawLabel = labelBuilder.toString();
				label = parseLabel(rawLabel);
				variable = parseVariable(rawVariable);
				codeWidth = parseCodeWidth(rawVariable);
				
				line = sc.nextLine();
			}
			if(line.startsWith("*SL")){			//Short Label Found
				String rawShortLabel = sc.nextLine();
				shortLabel = parseShortLabel(rawShortLabel);
				line = sc.nextLine();
			}
			if(line.startsWith("*MA")){			//Mask Found
				sc.nextLine();//do nothing
				line = sc.nextLine();
			}
			if(line.startsWith("*SK")){			//Skip Found
				String skipDestination = sc.nextLine();
				skipCondition = sc.nextLine();
				String[] destinations = parseSkipDestination(skipDestination);
				ifDestination = destinations[0];
				elseDestination = destinations[1];
				
				line = sc.nextLine();
			}
			if(line.startsWith("*CL")){			//Code List Found
				String rawChoice = sc.nextLine();
				do{
					String[] choice = parseChoice(rawChoice, codeWidth);
					choices.add(choice);
					rawChoice = sc.nextLine();
				}while(!rawChoice.startsWith("---"));
				line = sc.nextLine();
			}
			
			//If no label was found this indicates that the file is not a properly formatted .ASC file
			if(!labelFound){
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
	
	private static String parseLabel(String rawLabel){
		//Remove leading and trailing square brackets,
		//then remove leading and trailing spaces,
		//then replace tabs with a space,
		//reduce consecutive spaces to one space
		//replace weird unicode apostrophe to ASCII apostrophe
		return rawLabel.substring(1, rawLabel.length() - 1).trim().replace('\t', ' ').replaceAll(" +", " ").replace("\u00B4", "'");
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
