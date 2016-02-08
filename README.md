# ElasticSearch Plugin for Feature Vectors
Plugin to support ElasticSearch queries based on feature vectors extracted from images.
Based on https://github.com/kzwang/elasticsearch-image.
The main difference is that in the original plugin it is possible to post an encoded image to ElasticSearch, and the plugin extracts the feature and indexes the image based on the features.
Our plugin assumes that the client code extracts the features from the image and uses ElasticSearch to index the features, but the binaries of the images are stored outside ElasticSearch.

## Installation
This plugin is  compatible with ElasticSearch 2.1.1
1. Package the source code into a jar archive.
```
mvn package
```
This will create the jar file in target/releases folder.

2. Install the plugin on your ElasticSearch server instance
```
sudo <ES_HOME>/bin/plugin install file://<PATH TO JAR ARCHIVE>
```

3. Restart ElasticSearch

## Creating an image type
After installing the plugin, you can create a type in an index that conatins the mapping for the "image" type.
```
curl -XPUT 'localhost:9200/<index_name>/<type_name>/_mapping' -d '{
    "<type_name>": {
        "properties": {
            "name": {
                "type": "string"
            },
            "image": {
                "type": "image",
                "feature": {
                    "CEDD": {
                        "hash": "LSH"
                    }
                }
            }
        }
    }
}'
```

## Indexing an image
```
{
	"name":"002_0002.jpg",
	"image":"...feature vector as a base 64 encoded string..."
}
```

```
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
	public void index() throws FileNotFoundException, IOException, InterruptedException, ParseException
	{
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
				String pArgs[] = new String[]{"curl","-XPOST","http://localhost:9200/caltech/lire","-d", "@document.json"};
				Utils.executeProcess(pArgs);   
				if(++cnt==n)
			     		break;
	    		}
		
        	}
	}
	
	private String getStr(double[] doubleHistogram) throws ParseException {
		return Base64.getEncoder().encodeToString(SerializationUtils.toByteArray(doubleHistogram));
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException, ParseException {
		Indexer indexer = new Indexer("/home/lk/workspace/ISI/CALTECH256",2);
		indexer.index();
	}
	
}
```

## Querying an image
```
{
	"sort": [{
		"_score": "desc"
	}],
	"fields": ["name"],
	"query": {
		"image": {
			"image": {
				"image": "...feature vector as a base 64 encoded string....",
				"feature": "CEDD",
				"hash": "LSH"
			}
		}
	}
}
```


