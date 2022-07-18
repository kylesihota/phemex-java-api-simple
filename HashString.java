import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

public class HashString {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String calculateHMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    // returns valid request signature needed for Phemex authentication header 'x-phemex-request-signature'
    public static String getRequestSignature(String Path, String Query, int expiry, String body, String secret){
        String secretKey = secret;
        String signatureString = Path + Query + expiry + body;

        String hmac = "";
        try {
            hmac = calculateHMAC(signatureString, secretKey);
        } catch(Exception e) {
            hmac = "-1";
            System.out.println("Could not create HMAC SHA256!");
        }

        return hmac;
    }

}
