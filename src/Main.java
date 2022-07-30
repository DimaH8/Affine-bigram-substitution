import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.ArrayList;


public class Main {
    public static final String filePathY = "D:/Projects/Projects(Java)/Cryptolab3/cipher text.txt";
    //public static final String filePathY = "D:/Dropbox/Навчання/КПІ/6 семестр/Сим крипта/Лаби/3 лаба/variants/16.txt";
    
    public static final String alphabet = "абвгдежзийклмнопрстуфхцчшщьыэюя";
    public static final int m = 31;
    public static final int m2 = m * m;

    public static int getX(char ch1, char ch2)
    {
        int ch1In = alphabet.indexOf(ch1);
        int ch2In = alphabet.indexOf(ch2);
        assert ch1In != -1;
        assert ch2In != -1;
        int X = ch1In * m + ch2In;
        return X;
    }    
     public static int gcd(int a, int b) 
     {
    	 if (a % b == 0)
    		 return b;
    	 if (b % a == 0)
    		 return a;
    	 if (a > b)
    		 return gcd(a%b, b);
    	 return gcd(a, b%a);
	 } 
   
     public static int inverted_element(int a, int m)
     {
     	int mod = m;
     	int y = 0, inversed = 1;
     	int q, u;
     	if (m == 1)
     		return 0;

     	while (a > 1)
     	{
     		q = a / m;
     		u = m;
     		m = a % m;
     		a = u;
     		u = y;
     		y = inversed - q * y;
     		inversed = u;
     	}
     	if (inversed < 0)
     		inversed = inversed + mod;

     	return inversed;
     }
   
    private static ArrayList<String> getTop5BigramsY() 
    {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePathY)));
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        content = content.replaceAll("\\s","");
        HashMap<String, Integer> bigrams = new HashMap<String, Integer>();
        for (int i = 0; i < content.length() - 2; i += 2) {
            String bigram = "" + content.charAt(i) + content.charAt(i + 1);
            if (bigrams.get(bigram) == null) {
                bigrams.put(bigram, 1);
            } else {
                bigrams.put(bigram, bigrams.get(bigram) + 1);
            }
        }
        
        ArrayList<String> maxBigrams = new ArrayList<String>(); 
        int maxFreq = 0;
        String maxBigram = null;
        for (int i = 0; i < 5; i++) {
            for (String bigram : bigrams.keySet()) {  
                if (bigrams.get(bigram) > maxFreq) {
                    maxFreq = bigrams.get(bigram);
                    maxBigram = bigram;
                }
            }
            maxBigrams.add(maxBigram);
            System.out.println("TOP5 birgam is " + maxBigram + " = " + bigrams.get(maxBigram));
            bigrams.put(maxBigram, 0);
            maxFreq = 0;
        }
        return maxBigrams;
    }
    
    public static boolean validateDecodedFile(String path)
    {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(path)));
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
        HashMap<Character, Integer> monograms = new HashMap<Character, Integer>();
        for (int i = 0; i < content.length() - 1; i++) {
            char ch = content.charAt(i);
            if (monograms.get(ch) == null) {
                monograms.put(ch, 1);
            } else {
                monograms.put(ch, monograms.get(ch) + 1);
            }
        }

        char[] topMonogramsRus = {'о', 'е', 'и', 'а', 'н', 'т'}; // 5 variant
       // char[] topMonogramsRus = {'о', 'а'}; // 16 variant

        ArrayList<Character> maxMonograms = new ArrayList<Character>(); 
        int maxFreq = 0;
        char maxMonogram = 0;

        for (int i = 0; i < topMonogramsRus.length; i++) {
            for (Character monogram : monograms.keySet()) {
                if (monograms.get(monogram) > maxFreq) {
                    maxFreq = monograms.get(monogram);
                    maxMonogram = monogram;
                }
            }
            maxMonograms.add(maxMonogram);
            System.out.println("TOP monograms is " + maxMonogram + " = " + monograms.get(maxMonogram));
            monograms.put(maxMonogram, 0);
            maxFreq = 0;
        }

        // validation
        for (int i = 0; i < topMonogramsRus.length; i++) {
            char monoRus = topMonogramsRus[i];
            boolean found = false;
            for (int j = 0; j < maxMonograms.size(); j++) {
                if (maxMonograms.get(j) == monoRus) {
                    found = true;
                    break;
                }
            }

            if (found == false)  {
                System.out.println("TOP rus monogram '" + monoRus + "' is not found in deceded text " + path);
                return false;
            }
        }
        
        // rename file
        File decodedOrig = new File(path);
        File decodedTrue = new File("true_" + path);
        decodedOrig.renameTo(decodedTrue);
        return true;
    }
    
    
    public static boolean decode(int a, int b, String outFileName)
    {
        if (gcd(a, m2) != 1) {
            System.out.println("The number a is not mutually prime with the length of the alphabet");
            return false;
        }
        
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePathY)));
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        content = content.replaceAll("\\s","");
        try {
            FileWriter fw = new FileWriter(outFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            
            for (int i = 0; i < content.length() - 2; i += 2) {
                char y1 = content.charAt(i);
                char y2 = content.charAt(i + 1);            
                int Y = getX(y1, y2);
                
                int a_1 = inverted_element(a, m2);
                int X = ((Y - b) * a_1) % m2;
                while (X > m2) {
                    X = X - m2;
                }
                while (X < 0) {
                    X = X + m2;
                }
                
                int x1 = X / m;
                int x2 = X % m;
                
                out.print(alphabet.charAt(x1));
                out.print(alphabet.charAt(x2));
            }
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public static boolean decryptForBigrams(int X1, int X2, int Y1, int Y2)
    {
        int X = X1 - X2;
        while (X < 0) {
            X = X + m2;
        }
        
        int Y = Y1 - Y2;
        while (Y < 0) {
            Y = Y + m2;
        }
        
        int d = gcd(X, m2);
        if (d == 1) {
            int X_1 = inverted_element(X, m2);
            int a = (Y * X_1) % m2;
            int b = (Y1 - a * X1) % m2;
            if (a < 0) {
            	a = a + m2;
            }
            if (b < 0) {
            	b = b + m2;
            }
            System.out.println("Found key: a=" + a + " b= " + b);
            String decFile = "decoded_text_" + a + "_" + b + ".txt";
            boolean res = decode(a, b, decFile); 
            if (res == true) {
                res = validateDecodedFile(decFile);
                return res;
            } else {
            	System.out.println("No roots: #1");
            	return false;
            }
        } else if (d > 1) {
        	// ax = b (mod n)
        	//  b = Y
        	//  a = X 
            //  x = a
        	//
        	//
        	// #2 - b not givide by d
        	if (Y % d != 0) {
        		// #2.1 - no roots
        		System.out.println("No roots: #2");
        		return false;
        	} else {
        		// #2.2 - many roots
        		int n1 = m2 / d;
        		int b1 = Y / d;
        		int a1 = X / d;
        		
        		int x0 = (b1 * inverted_element(a1, n1)) % n1;
        		for (int i = 0; i < d; i++) {
        			int a = x0 + i * d;
        			int b = (Y1 - a * X1) % m2;
        			System.out.println("d>1: Found key: a=" + a + " b= " + b);
                	String decFile = "decoded_text_" + a + "_" + b + ".txt";
                	boolean res = decode(a, b, decFile);
                	if (res == true) {
                    	res = validateDecodedFile(decFile);
                    	return res;
                    } else {
                    	System.out.println("No roots: #3");
                    	return false;
                    }
        		}
        	}
        }
        else {
            System.out.println("No keys: #4");
            return false;
        }
        return false;
    }
    
    public static void decrypt(ArrayList<String> topBigramsY)
    {
        String[] topBigramRus = {"ст", "но", "то", "на", "ен"};
        
        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                if (i != j)
                {
                    for (int k = 0; k < 5; k++)
                    {
                        for (int f = 0; f < 5; f++)
                        {
                            if (k != f)
                            {
                               
                                int X1 = getX(topBigramRus[i].charAt(0), topBigramRus[i].charAt(1));
                                int X2 = getX(topBigramRus[j].charAt(0), topBigramRus[j].charAt(1));
                                int Y1 = getX(topBigramsY.get(k).charAt(0), topBigramsY.get(k).charAt(1));
                                int Y2 = getX(topBigramsY.get(f).charAt(0), topBigramsY.get(f).charAt(1));
                                decryptForBigrams(X1, X2, Y1, Y2);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ArrayList<String> topBigramsY = getTop5BigramsY();
        assert topBigramsY.size() == 5;
        decrypt(topBigramsY);

    }

}
