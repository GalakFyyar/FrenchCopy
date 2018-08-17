import java.io.*;

class Writer{
	static void writeEFile(String fileName, StringBuilder sb){
		BufferedWriter writer;
		try{
			//writer = new PrintWriter(fileName);
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "Windows-1252"));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		//Print Enter File
		try {
			writer.write(sb.toString());
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				writer.close();
			} catch (IOException ignore) {}
		}
	}
}
