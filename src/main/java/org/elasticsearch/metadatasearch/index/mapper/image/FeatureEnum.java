package org.elasticsearch.metadatasearch.index.mapper.image;
import org.elasticsearch.metadatasearch.lire.feature.ImageCEDD;
import org.elasticsearch.metadatasearch.lire.feature.ImageLireFeature;
import org.elasticsearch.metadatasearch.lire.feature.ImageSURF;

public enum FeatureEnum {
    CEDD(ImageCEDD.class),
    SURF(ImageSURF.class);

    private Class<? extends ImageLireFeature> featureClass;

    FeatureEnum(Class<? extends ImageLireFeature> featureClass) {
        this.featureClass = featureClass;
    }

    public Class<? extends ImageLireFeature> getFeatureClass() {
        return featureClass;
    }

    public static FeatureEnum getByName(String name) {
        return valueOf(name.toUpperCase());
    }

}