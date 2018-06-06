import java.io.File;

public class Main{
	public static void main(String[] args){
        System.out.println("START");
	    String filePath = "G:\\PCO\\D160\\D160.ASC";
	    //String filePath = "D160.ASC";
		
		File file = new File(filePath);
		boolean succ = Controller.parseASCFile(file);
		
		if(!succ){
			System.out.println("Parse not successful");
			return;
		}
		
		
		//Controller.write(file);
		
		System.out.println("END");
	}
}
