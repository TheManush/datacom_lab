import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.Scanner;

public class CRCclient_38_34 {
    public static String encodeData(String data, String key) {
        int n = key.length();
        String str = data + String.join("", Collections.nCopies(n - 1, "0"));
        System.out.println("Appended Data: "+ str);
        String remainder = mod2div(str, key);
        System.out.println("Remainder: "+remainder);
        return data + remainder;
    }

    static String mod2div(String dividend, String divisor) {
        int n = dividend.length();
        int pick = divisor.length();
        String tmp = dividend.substring(0, pick);

        while (pick < n) {
            if (tmp.charAt(0) == '1') {
                
                tmp = findXor(divisor, tmp) + dividend.charAt(pick);
            } else {

                tmp = findXor(String.format("%0" + pick + "d", 0), tmp) 
                      + dividend.charAt(pick);
            }
            pick += 1;
        }

        if (tmp.charAt(0) == '1') {
            tmp = findXor(divisor, tmp);
        } else {
            tmp = findXor(String.format("%0" + pick + "d", 0), tmp);
        }

        return tmp;
    }
    
    static String findXor(String a, String b) {
        int n = b.length();
        StringBuilder result = new StringBuilder();
        
    
        for (int i = 1; i < n; i++) {
            if (a.charAt(i) == b.charAt(i)) {
                result.append('0');
            } else {
                result.append('1');
            }
        }
        return result.toString();
    }
    public static String toBinaryWithPadding(char c) {
        String binary = Integer.toBinaryString(c);
        while (binary.length() < 8) {
            binary = "0" + binary;
        }
        return binary;
    }
    public static void main(String[] args) {
        String serverIP = "localhost";  //10.32.2.228
        int port = 8900;
        Scanner sc =new Scanner(System.in);
        try (
            Socket socket = new Socket(serverIP, port);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader fileReader = new BufferedReader(new FileReader("input.txt"))
        ) {
            System.out.println("Client connected to the server on Handshaking port " + port);

        
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = fileReader.readLine()) != null) {
                sb.append(line);
            }

            String fileContent = sb.toString();
            System.out.println("File content: " + fileContent);

    
            String binaryData = "";
            for (char c : fileContent.toCharArray()) {
                binaryData += toBinaryWithPadding(c);
            }

            System.out.println("Converted Binary: " + binaryData.toString());
            System.out.println("Input Generator Polynomial:");
            String gen = sc.nextLine();
            System.out.println("Generator polynomial: " + gen);

            String code = encodeData(binaryData.toString(), gen);
            System.out.println("Transmitted code sent:" + code);
            
            dos.writeUTF(code);
            dos.writeUTF(gen);
            System.out.println("Sent to server.");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        sc.close();
    }
}
