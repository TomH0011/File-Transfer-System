import java.net.*;
//Input and output streams
import java.io.*;
// Encrypting and decrypting
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;

public class Client {

    // AES Algorithm and key for decrypting
    private static final String ALGORITHM = "AES";
    // private static final byte[] keyValue = "2468101214161820".getBytes();


    public Client() throws NoSuchAlgorithmException {
    }

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 5000);
            System.out.println("Client is connected");

            SecretKey aesKey = receiveKey(client); // Get the key from server

            // Create the cipher object for AES 128 Encryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // This initializes the Cipher object to be ready for encryption
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            // Get the input stream to receive the file from server
            InputStream in = client.getInputStream();

            // Now to Decrypt our file with our cipher and input stream
            CipherInputStream cipherIn = new CipherInputStream(in, cipher);

            // Save file to desired path
            FileOutputStream fileOut = new FileOutputStream("Some File Path\\received_file.pdf");

            // Buffer for reading the data in chunks
            byte[] buffer = new byte[4096];
            int bytesRead;

            // Read data from server and write to a file
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }

            fileOut.close();
            // Now to close the decryption-ator?
            cipherIn.close();
            client.close();
            System.out.println("File received and saved.");

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    // Method to receive the AES key from the server
    private static SecretKey receiveKey(Socket client) throws IOException {
        InputStream keyIn = client.getInputStream(); // Get input stream
        byte[] keyBytes = new byte[16]; // Buffer for the key (128 bits = 16 bytes)

        int bytesRead = keyIn.read(keyBytes); // Read the key bytes
        // Incase it doesnt read the key
        if (bytesRead < 16) {
            throw new IOException("Key received is too short");
        }

        return new SecretKeySpec(keyBytes, ALGORITHM); // Create a SecretKey from the bytes
    }
}
