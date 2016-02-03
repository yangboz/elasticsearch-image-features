# ElasticSearch Plugin for Feature Vectors
Plugin to support ElasticSearch queries based on feature vectors extracted from images.
Based on https://github.com/kzwang/elasticsearch-image.
The main difference is that in the original plugin it is possible to post an encoded image to ElasticSearch, and the plugin extracts the feature and indexes the image based on the features.
Our plugin assumes that the client code extracts the features from the image and uses ElasticSearch to index the features, but the binaries of the images are stored outside ElasticSearch.
