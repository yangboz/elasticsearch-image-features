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


