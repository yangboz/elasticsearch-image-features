# ElasticSearch Plugin for Feature Vectors
Plugin to support ElasticSearch queries based on feature vectors extracted from images.
Based on https://github.com/kzwang/elasticsearch-image.
The main difference is that in the original plugin it is possible to post an encoded image to ElasticSearch, and the plugin extracts the feature and indexes the image based on the features.
Our plugin assumes that the client code extracts the features from the image and uses ElasticSearch to index the features, but the binaries of the images are stored outside ElasticSearch.

## Installation
This plugin is  compatible with ElasticSearch 2.1.1 <br/><br/>
1. Package the source code into a zip archive. <br />
```
mvn package
```
This will create the zip file in <b>target/releases</b> folder. <br/><br/>
2. Install the plugin on your ElasticSearch server instance<br />
```
sudo <ES_HOME>/bin/plugin install file://<PATH TO ZIP ARCHIVE>
```
<br/>
3. Restart ElasticSearch<br />

## Example
#### Create Settings

```sh
curl -XPUT 'localhost:9200/a_index' -d '{
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1,
    "index.version.created": 1070499
  }
}'
```

## Creating an image type
After installing the plugin, you can create a type in an index that conatins the mapping for the "image" type.
```
curl -XPUT 'localhost:9200/a_index/a_image_item/_mapping' -d '{
    "a_image_item": {
        "properties": {
            "name": {
                "type": "string"
            },
            "image": {
                "type": "image-features",
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
```sh
curl -XPOST 'localhost:9200/a_index/a_image_item' -d '
{
    "name":"002_0002.jpg",
    "image":"...feature vector as a base 64 encoded string..."
}'
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

```
public class LireImage
{
	public String name;
	//public double[] image;
	public String image;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}

```

```
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Base64;

import net.semanticmetadata.lire.utils.SerializationUtils;

class ProcessResult
{
	String stdInp;
	String stdError;
	public String getStdInp() {
		return stdInp;
	}
	public void setStdInp(String stdInp) {
		this.stdInp = stdInp;
	}
	public String getStdError() {
		return stdError;
	}
	public void setStdError(String stdError) {
		this.stdError = stdError;
	}
	public ProcessResult(String stdInp, String stdError) {
		super();
		this.stdInp = stdInp;
		this.stdError = stdError;
	}
	
}
public class Utils {

	
	public static ProcessResult executeProcess(String procesArgs[]) throws IOException
	{
		
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
}


```
## Querying an image
```sh
curl -XPOST 'localhost:9200/a_index/a_image_item/_search' -d '{
    "sort": [{
    "_score": "desc"
    }],
    "fields": ["name"],
    "query": {
        "image-feature": {
            "image-feature": {
                "image": "...feature vector as a base 64 encoded string....",
                "feature": "CEDD",
                "hash": "LSH"
            }
        }
    }
}'
```


