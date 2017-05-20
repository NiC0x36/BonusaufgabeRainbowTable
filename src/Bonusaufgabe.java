import java.util.HashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.security.*;

public class Bonusaufgabe {
	private static int N = 2000;
	private static int K = 2000;
	private static int L = 7; //L‰nge des Passworts
	private static char[] Z = {'0','1','2','3','4','5','6','7','8','9',
			'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
			'p','q','r','s','t','u','v','w','x','y','z'};
	private static Map rainbowtable = new HashMap<String, String>(); //k -> endpunkt / v -> startpunkt

	public static void main(String[] args) {
		//createRainbowTable();
		System.out.println(hashfunction("0000000"));
		System.out.println(reductionfunction(")√Ó£Û÷∏#ıb¨K„R", 2000));
	}
	
	private static void createRainbowTable() {
		for(int n = 0; n < N; n++) {
			String startplaintext = "0000000"; //TODO: increment start
			String plaintext = startplaintext;
			String hashtext;
			for(int k = 1; k <= K; k++) {
				hashtext = hashfunction(plaintext);
				plaintext = reductionfunction(hashtext, k);
			}
			rainbowtable.put(plaintext, startplaintext);
		}
	}
	
	private static void findPlaintext(String hash) {
		// TODO Write the method to find a plaintext with given hash

	}

	private static String hashfunction(String plaintext) {
		byte[] bytesOfMessage;
		MessageDigest md;
		try {
			bytesOfMessage = plaintext.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			return new String(thedigest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	private static String reductionfunction(String hashtext, int step) {
		int hash = 0; //TODO: hashtext interpretiert als nat¸rliche zahl
		hash += step;
		StringBuilder result = new StringBuilder();
		for(int i = 1; i <= L; i++) {
			result.insert(0,Z[hash % Z.length]);
			hash = hash / Z.length;
		}
		return result.toString();
	}
}
