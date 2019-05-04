import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GetAlphabet {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        String fileName = args[0];

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8));) {
				ArrayList<Character> alphabet = new ArrayList<Character>();
				String line;
                while((line = br.readLine()) != null) {
					int i =0;
					while(i<line.length()){
					if(!alphabet.contains(line.charAt(i))){
						alphabet.add(line.charAt(i));
					}
					i++;
					}
				}
				
        System.out.println(alphabet);
        }

    }
}