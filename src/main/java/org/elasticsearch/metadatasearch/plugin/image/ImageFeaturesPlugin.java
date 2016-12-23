package org.elasticsearch.metadatasearch.plugin.image;

import org.elasticsearch.indices.IndicesModule;
import org.elasticsearch.metadatasearch.index.mapper.image.ImageFeaturesMapper;
import org.elasticsearch.metadatasearch.index.query.image.ImageQueryParser;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.threadpool.ThreadPool;


public class ImageFeaturesPlugin extends Plugin {

    @Override
    public String name() {
        return ImageFeaturesMapper.CONTENT_TYPE;
    }

    @Override
    public String description() {
        return "Elasticsearch Image Features Plugin";
    }

    public void onModule(IndicesModule indicesModule) {
        indicesModule.registerMapper(ImageFeaturesMapper.CONTENT_TYPE, new ImageFeaturesMapper.TypeParser(new ThreadPool(ImageFeaturesMapper.CONTENT_TYPE)));
        indicesModule.registerQueryParser(ImageQueryParser.class);
    }
    
    
}
