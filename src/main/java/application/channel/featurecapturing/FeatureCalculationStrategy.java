package application.channel.featurecapturing;

import java.util.List;

public interface FeatureCalculationStrategy {

    Float getFeature(List<Float> waveData);
}
