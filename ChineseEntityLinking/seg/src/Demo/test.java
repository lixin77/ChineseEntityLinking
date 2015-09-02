package Demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Path filePath = Paths.get("./");
		
		System.out.println(filePath);
		File file = filePath.toFile();
		
		filePath = Paths.get(filePath.toString(),"test.txt");
		System.out.println(filePath);
		System.out.println(file.getCanonicalFile().toString());
	}

}
