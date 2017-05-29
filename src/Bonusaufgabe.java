import java.util.HashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;

public class Bonusaufgabe {
	private static int N = 2000;
	private static int K = 2000;
	private static int L = 7; // Länge des Passworts
	private static char[] Z = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	// k -> endpunkt / v -> startpunkt
	private static Map<String, String> rainbowtable = new HashMap<String, String>();
	private static MessageDigest md;

	public static void main(String[] args) throws NoSuchAlgorithmException {	
		md = MessageDigest.getInstance("MD5");
		System.out.println("Start");

		// Timer (in ms) to see how long the creation of the rainbowtable took
		long start = System.currentTimeMillis();
		// creating the rainbowtable
		createRainbowTable();
		long duration = System.currentTimeMillis() - start;
		System.out.println("Rainbowtable in " + duration + "ms created");

		String[] hashesToCheck = { "29c3eea3f305d6b823f562ac4be35217", "12e2feb5a0feccf82a8d4172a3bd51c3",
				"437988e45a53c01e54d21e5dc4ae658a", "c0e9a2f2ae2b9300b6f7ef3e63807e84", "1d56a37fb6b08aa709fe90e12ca59e12" };
		for (int i = 0; i < hashesToCheck.length; i++) {
			// Timer (in ms) to see how long the"hacking" of the hash took
			start = System.currentTimeMillis();
			//checking the given hashes, using the created rainbowtable and the hash- & reductionfunction
			findPlaintext(hashesToCheck[i]);
			duration = System.currentTimeMillis() - start;
			System.out.println("Password in " + duration + "ms hacked"); //TODO: output also when not found

		}
	}

	// creates the rainbowtable
	// starts based on the calculated startValue
	private static void createRainbowTable() {
		for (int n = 0; n < N; n++) {
			String startplaintext = calcStartValue(n);
			String plaintext = startplaintext;
			String hashtext;
			for (int k = 0; k < K; k++) {
				hashtext = hashfunction(plaintext);
				plaintext = reductionfunction(hashtext, k);
			}
			rainbowtable.put(plaintext, startplaintext);
		}
	}

	// calculates the startValue of the rainbowtable
	private static String calcStartValue(int n) {
		StringBuilder result = new StringBuilder();
		for (int i = 1; i <= L; i++) {
			result.insert(0, Z[n % Z.length]);
			n = n / Z.length;
		}
		return result.toString();
	}

	// finds the plaintext
	// iterates from a given hash to find the corresponding plaintext
	// if the corresponding plaintext was found, prints the hash and the plaintext
	// if the corresponding plaintext could not be found, prints a "password not found"-message
	private static void findPlaintext(String hash) {
		System.out.println("\nHash to search: " + hash);
		for (int i = rainbowtable.size() - 1; i >= 0; i--) {
			int j = i;
			String t = hash;
			t = reductionfunction(t, j);

			// go to end value, max 2000 steps
			while (j < K - 1) {
				j++;
				t = hashfunction(t);
				t = reductionfunction(t, j);
			}

			if (rainbowtable.containsKey(t)) {
				t = rainbowtable.get(t); // get start value
				// hash & reduce the start value to the given hash
				for (int k = 0; k < i; k++) {
					t = hashfunction(t);
					t = reductionfunction(t, k);
				}
				// compare given hash with hash of found plaintext
				// if equal prints hash and plaintext
				if (hashfunction(t).equals(hash)) {
					System.out.println("Password to hash \"" + hash + "\" is: \"" + t + "\"");
					return;
				}
			}
		}
		System.out.println("Password not found!");
	}

	// hashfuntion used to hash the plaintext
	private static String hashfunction(String plaintext) {
		BigInteger bi = new BigInteger(1, md.digest(plaintext.getBytes()));
		return bi.toString(16);
	}

	// reductionfunction used to calc the plaintext
	// step: 0 <= x <= 1999
	private static String reductionfunction(String hashtext, int step) {
		BigInteger hash = new BigInteger(hashtext, 16);
		hash = hash.add(BigInteger.valueOf(step));
		StringBuilder result = new StringBuilder();
		for (int i = 1; i <= L; i++) {
			result.insert(0, Z[hash.mod(BigInteger.valueOf(Z.length)).intValue()]);
			hash = hash.divide(BigInteger.valueOf(Z.length));
		}
		return result.toString();
	}
}
