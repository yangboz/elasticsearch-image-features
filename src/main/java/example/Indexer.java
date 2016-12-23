package example;

/**
 * Created by smartkit on 2016/12/23.
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Base64;
import javax.imageio.ImageIO;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.utils.SerializationUtils;

public class Indexer {
    String inpFolder;
    int n=10000;
    public Indexer(String inpFolder)
    {
        this.inpFolder = inpFolder;
    }
    public Indexer(String inpFolder,int n)
    {
        this.inpFolder = inpFolder;
        this.n=n;
    }
    public void index() throws FileNotFoundException, IOException, InterruptedException, ParseException, Utils.IOException {
        int cnt = 0;
        File inpF = new File(inpFolder);
        File[] files = inpF.listFiles();
        Arrays.sort(files);
        for (final File fileEntry : files)
        {
            if (fileEntry.isFile())
            {
                BufferedImage image = ImageIO.read(new FileInputStream(fileEntry));
                CEDD cedd = new CEDD();
                cedd.extract(image);
                String fName = fileEntry.getName();
                ObjectMapper om = new ObjectMapper();
                LireImage lIm  = new LireImage();
                lIm.setImage(getStr(cedd.getDoubleHistogram()));
                lIm.setName(fName);
                PrintWriter writer = new PrintWriter("document.json", "UTF-8");
                writer.println(om.writeValueAsString(lIm));
                writer.close();
                String pArgs[] = new String[]{"curl","-XPOST","http://localhost:9200/a_index/a_image_item","-d", "@document.json"};
                Utils.executeProcess(pArgs);
                if(++cnt==n)
                    break;
            }

        }
    }

    private String getStr(double[] doubleHistogram) throws ParseException {
        return Base64.getEncoder().encodeToString(SerializationUtils.toByteArray(doubleHistogram));
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException, ParseException, Utils.IOException {
        Indexer indexer = new Indexer("/Users/smartkit/Downloads/256_ObjectCategories/001.ak47/",2);
        indexer.index();
    }

}