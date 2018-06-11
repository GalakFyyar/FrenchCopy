import java.io.PrintWriter;

class Writer{
	static void writeEFile(String fileName, StringBuilder sb){
		PrintWriter writer;
		try{
			writer = new PrintWriter(fileName);
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		//Print Enter File
		writer.println(sb.toString());
		writer.println();
		
		writer.close();
	}
}
