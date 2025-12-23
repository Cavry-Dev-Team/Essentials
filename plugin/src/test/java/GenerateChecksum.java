import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GenerateChecksum {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get("yourlib.jar"));
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(fileBytes);
        String checksum = Base64.getEncoder().encodeToString(hash);
        System.out.println(checksum);  // Base64-encoded SHA-256
    }
}
