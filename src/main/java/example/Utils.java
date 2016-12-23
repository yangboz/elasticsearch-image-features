package example;

import net.semanticmetadata.lire.utils.SerializationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by smartkit on 2016/12/23.
 */
public class Utils {


    public static ProcessResult executeProcess(String procesArgs[]) throws IOException, java.io.IOException {

        Process p = Runtime.getRuntime().exec(procesArgs);
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        String sInp = null;
        StringBuffer sb = new StringBuffer();
        while ((sInp = stdInput.readLine()) != null) {
            sb.append(sInp + "\n");
        }
        sInp = sb.toString();
        String sErr = null;
        sb = new StringBuffer();

        while ((sErr = stdError.readLine()) != null) {
            sb.append(sErr + "\n");
        }
        sErr = sb.toString();
        stdError.close();
        stdInput.close();
        return new ProcessResult(sInp, sErr);
    }


    public static void main(String[] args) {

        double[] a = new double[]{1,2,3.0,4};
        String encoded =  Base64.getEncoder().encodeToString(SerializationUtils.toByteArray(a));
        System.out.println("Encoded : " + encoded);
        byte[] decoded = Base64.getDecoder().decode(encoded);
        System.out.println("Decoded  : "   + Arrays.toString(SerializationUtils.toDoubleArray(decoded)));

    }

    static class IOException extends Exception {
    }
}
