import java.math.BigInteger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;

public class JCheckHash {
    private record Arguments(BigInteger verify, File f, String algorithm) {
    }

    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
        Arguments a = readArgs(args);

        MessageDigest md = MessageDigest.getInstance(a.algorithm);
        FileInputStream stream = new FileInputStream(a.f);
        DigestInputStream dstream = new DigestInputStream(stream, md);

        {
            byte[] buffer = new byte[1 << 28];
            for (int read = dstream.read(buffer); read != -1; read = dstream.read(buffer))
                ;
        }

        byte[] digest = dstream.getMessageDigest().digest();
        BigInteger hash = new BigInteger(1, digest);

        dstream.close();
        stream.close();

        System.out.println("File Hash: " + hash.toString(16));
        if (a.verify != null) {
            System.out.println("Your Hash: " + a.verify.toString(16));
            if (hash.equals(a.verify)) {
                System.out.println("These are the same!");
            } else {
                System.out.println("These are NOT the same!");
            }
        }

        System.out.println("Algorithm Used: " + a.algorithm);
    }

    private static Arguments readArgs(String[] args) throws FileNotFoundException {
        String filename = null;
        String verifystr = null;
        String algorithm = "SHA256";

        for (int i = 0; i < args.length; i++)
            if (args[i].charAt(0) == '-')
                if (args[i] == "-i")
                    verifystr = args[i++];
                else
                    algorithm = args[i].substring(1);
            else if (filename == null)
                filename = args[i];
            else if (verifystr == null)
                verifystr = args[i];

        return new Arguments(
                verifystr == null ? null : new BigInteger(verifystr, 16),
                new File(filename),
                algorithm);
    }
}
