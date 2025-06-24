import java.io.*;
import java.net.*;
import java.util.Random;

public class CRCserver_34_38 {
    public static void main(String[] args) {
        try 
        {
            
            ServerSocket serverSocket = new ServerSocket(8900);
            System.out.println("Server is connected at port no: 8900");
            System.out.println("Server started. Waiting for client");

            
            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            
            DataInputStream in = new DataInputStream(socket.getInputStream());

            
            String receivedCodeword = in.readUTF();
            String generator = in.readUTF();

            System.out.println("Received Codeword: " + receivedCodeword);
            System.out.println("Generator Polynomial: " + generator);

        
            String corruptedCodeword = flipRandomBits(receivedCodeword, 1); 
            System.out.println("Corrupted Codeword: " + corruptedCodeword);

            String remainder = CRC(corruptedCodeword , generator);
            System.out.println("Calculated Remainder : " + remainder);

            
            boolean flag = false;

            for (int i = 0; i < remainder.length(); i++) 
            {
                if (remainder.charAt(i) != '0') 
                {
                    flag = true;
                    break;
                }
            }

            if (flag) 
            {
                System.out.println("Error detected in transmission!");
                String corrected = SingleBitErrorCorrection(corruptedCodeword, generator);
                if (corrected != null) {
                    System.out.println("Single-bit error corrected!");
                    System.out.println("Corrected codeword: " + corrected);
                } else {
                 System.out.println("Error is uncorrectable or ambiguous.");
                }
            } 
            else 
            {
                System.out.println("No error detected in transmission.");
            }

            
            in.close();
            socket.close();
            serverSocket.close();
            

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    
    public static String CRC(String data, String generator) 
    {
        int dataLen = data.length();
        int genLen = generator.length();
        char[] dataArray = data.toCharArray();
        char[] genArray = generator.toCharArray();
    
        for (int i = 0; i <= dataLen - genLen; ) {
            if (dataArray[i] == '1') {
                for (int j = 0; j < genLen; j++) {
                    if (dataArray[i + j] == genArray[j]) {
                        dataArray[i + j] = '0';
                    } else {
                        dataArray[i + j] = '1';
                    }
                }
            }
            while (i < dataLen && dataArray[i] != '1') {
                i++;
            }
        }
    
        
        String remainder = "";
        for (int i = dataLen - genLen + 1; i < dataLen; i++) {
            remainder += dataArray[i];
        }
    
        return remainder;
    }
    

   
    public static String flipRandomBits(String codeword, int count) 
    {
        char[] bits = codeword.toCharArray();
        Random rand = new Random();
        int len = bits.length;

        for (int i = 0; i < count; i++) 
        {
            int pos = rand.nextInt(len);
            if (bits[pos] == '0') {
                bits[pos] = '1';
            } else {
                bits[pos] = '0';
            }
        }

        return new String(bits);
    }

    public static String SingleBitErrorCorrection(String corrupted, String generator){
        String fixedStr = null;
        int len= corrupted.length();
        int count=0;
        for(int i=0;i<len;i++){
            char[] bits = corrupted.toCharArray();
            if (bits[i] == '0') {
                bits[i] = '1';
            } else {
                bits[i] = '0';
            }

            String modified = new String(bits);
            String remainder = CRC(modified , generator);
            boolean valid = true;
            for (int j = 0; j < remainder.length(); j++) {
                if (remainder.charAt(j) != '0') {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                fixedStr = modified;
                count++;
            }
        }

        if (count == 1) {
            return fixedStr;  
        } else {
            return null;  
        }
    }


}

    

