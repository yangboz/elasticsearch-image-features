package org.elasticsearch.metadatasearch.lire.feature;

import net.semanticmetadata.lire.imageanalysis.SurfFeature;

public class ImageSURF extends SurfFeature implements ImageLireFeature {

    @Override
    public void setHistogramFromDoubleArray(double[] h) {
        descriptor = new double[h.length];
        System.arraycopy(h, 0, descriptor, 0, h.length);
    }

    @Override
    public float getSimilarity(ImageLireFeature ilf) {
        float distance = getDistance(ilf);
        if (distance <= 0) {
            distance = Integer.MAX_VALUE;
        }
        return 1/distance;
    }
}