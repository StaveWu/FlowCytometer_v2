package application.channel.featurecapturing;

import java.util.List;

public class FeatureCalculation {

    public static final FeatureCalculationStrategy AREA = new AreaStrategy();
    public static final FeatureCalculationStrategy HEIGHT = new HeightStrategy();
    public static final FeatureCalculationStrategy WIDTH = new WidthStrategy();

    private static class AreaStrategy implements FeatureCalculationStrategy {

        @Override
        public Float getFeature(List<Float> waveData) {
            Float sum = 0f;
            for (Float ele : waveData) {
                sum += ele;
            }
            return sum;
        }
    }

    private static class HeightStrategy implements FeatureCalculationStrategy {

        @Override
        public Float getFeature(List<Float> waveData) {
            Float max = 0f;
            for (Float ele : waveData) {
                if (max < ele) {
                    max = ele;
                }
            }
            return max;
        }
    }

    private static class WidthStrategy implements FeatureCalculationStrategy {

        @Override
        public Float getFeature(List<Float> waveData) {
            return (float) waveData.size();
        }
    }
}
