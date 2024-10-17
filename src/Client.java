import java.net.*;
//Input and output streams
import java.io.*;
// Encrypting and decrypting
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class Client {

    // AES Algorithm and key for decrypting
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "2468101214161820".getBytes();

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 5000);
            System.out.println("Client is connected");

            // Create the cipher object for AES 128 Encryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // This creates a new SecretKeySpec object, which is a type of Key.
            // The SecretKeySpec class is used to convert a simple byte array (keyValue)
            // into a key object that can be used by the Cipher
            Key key = new SecretKeySpec(keyValue, ALGORITHM);

            // This initializes the Cipher object to be ready for encryption
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // Get the input stream to receive the file from server
            InputStream in = client.getInputStream();

            // Now to Decrypt our file with our cipher and input stream
            CipherInputStream cipherIn = new CipherInputStream(in, cipher);

            // Save file to desired path
            FileOutputStream fileOut = new FileOutputStream("File_path_you_want_to_send_to\\received_file.pdf");

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
}
