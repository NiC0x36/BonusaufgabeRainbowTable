import java.util.HashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;

public class Bonusaufgabe {
	private static int N = 2000;
	private static int K = 2000;
	private static int L = 7; // L�nge des Passworts
	private static char[] Z = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	// k -> endpunkt / v -> startpunkt
	private static Map<String, String> rainbowtable = new HashMap<String, String>();

	public static void main(String[] args) {
		System.out.println("Start");
		long start = System.currentTimeMillis();
		createRainbowTable();
		long duration = System.currentTimeMillis() - start;
		System.out.println("Rainbowtable in " + duration + "ms created");

		String[] hashesToCheck = { "29c3eea3f305d6b823f562ac4be35217", "12e2feb5a0feccf82a8d4172a3bd51c3",
				"437988e45a53c01e54d21e5dc4ae658a", "c0e9a2f2ae2b9300b6f7ef3e63807e84", "1d56a37fb6b08aa709fe90e12ca59e12" };
		for (int i = 0; i < hashesToCheck.length; i++) {
			start = System.currentTimeMillis();
			findPlaintext(hashesToCheck[i]);
			duration = System.currentTimeMillis() - start;
			System.out.println("Password in " + duration + "ms hacked");

		}
	}

	private static void createRainbowTable() {
		for (int n = 0; n < N; n++) {
			String startplaintext = calcStartValue(n);
			String plaintext = startplaintext;
			String hashtext;
			for (int k = 0; k < K; k++) { // TODO: ACHTUNG! War "<=" so werden
											// es jedoch 2001 berechnungen pro
											// kette...
				hashtext = hashfunction(plaintext);
				plaintext = reductionfunction(hashtext, k);
			}
			rainbowtable.put(plaintext, startplaintext);
		}
	}

	private static String calcStartValue(int n) {
		StringBuilder result = new StringBuilder();
		for (int i = 1; i <= L; i++) {
			result.insert(0, Z[n % Z.length]);
			n = n / Z.length;
		}
		return result.toString();
	}

	private static void findPlaintext(String hash) {
		System.out.println("\nHash to search: " + hash);
		for (int i = rainbowtable.size() - 1; i >= 0; i--) {
			int j = i;
			String t = hash;
			t = reductionfunction(t, j);

			// go to end value
			while (j < K - 1) {
				j++;
				t = hashfunction(t);
				t = reductionfunction(t, j);
			}

			if (rainbowtable.containsKey(t)) {
				t = rainbowtable.get(t); // get start value
				for (int k = 0; k < i; k++) {
					t = hashfunction(t);
					t = reductionfunction(t, k);
				}
				if (hashfunction(t).equals(hash)) {
					System.out.println("Password to hash \"" + hash + "\" is: \"" + t + "\"");
					return;
				}
			}
		}
		System.out.println("Password not found!");
	}

	private static String hashfunction(String plaintext) {
		MessageDigest md;
		try {
			md = java.security.MessageDigest.getInstance("MD5");
			byte[] bytesOfMessage = md.digest(plaintext.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bytesOfMessage.length; ++i) {
				//TODO: anpassen
				sb.append(Integer.toHexString((bytesOfMessage[i] & 0xFF) | 0x100).substring(1, 3)); // /TODO:
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

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
