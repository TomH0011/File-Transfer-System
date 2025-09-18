import java.net.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import java.util.Scanner;

public class Client {

    // AES Algorithm and key for decrypting
    private static final String ALGORITHM = "AES";

    public Client() throws NoSuchAlgorithmException {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("File Transfer Client");
        System.out.print("Enter server IP (localhost for local): ");
        String serverIP = scanner.nextLine();
        if (serverIP.trim().isEmpty()) {
            serverIP = "localhost";
        }
        
        System.out.print("Enter save directory path: ");
        String savePath = scanner.nextLine();
        if (savePath.trim().isEmpty()) {
            savePath = System.getProperty("user.home") + File.separator + "Downloads";
        }
        
        try {
            Socket client = new Socket(serverIP, 5000);
            System.out.println("Client connected to server");

            SecretKey aesKey = receiveKey(client); // Get the key from server
            System.out.println("AES key received from server");

            // Create the cipher object for AES 256 Decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            // Get the input stream to receive the file from server
            InputStream in = client.getInputStream();

            // Now to Decrypt our file with our cipher and input stream
            CipherInputStream cipherIn = new CipherInputStream(in, cipher);

            // Generate unique filename with timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "received_file_" + timestamp;
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            
            FileOutputStream fileOut = new FileOutputStream(saveDir + File.separator + fileName);

            // Buffer for reading the data in chunks
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = 0;

            System.out.println("Receiving file...");

            // Read data from server and write to a file
            while ((bytesRead = cipherIn.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            System.out.println("File received and saved: " + saveDir + File.separator + fileName + " (" + totalBytes + " bytes)");

            fileOut.close();
            cipherIn.close();
            client.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // Method to receive the AES key from the server
    private static SecretKey receiveKey(Socket client) throws IOException {
        InputStream keyIn = client.getInputStream(); // Get input stream
        byte[] keyBytes = new byte[32]; // Buffer for the key (256 bits = 32 bytes)

        int bytesRead = keyIn.read(keyBytes); // Read the key bytes
        // In case it doesn't read the key correctly
        if (bytesRead < 32) {
            throw new IOException("Key received is too short");
        }

        return new SecretKeySpec(keyBytes, ALGORITHM); // Create a SecretKey from the bytes
    }
}
