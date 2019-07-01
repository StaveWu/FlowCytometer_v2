package application.dashboard;

import java.util.Arrays;

public enum SampleMode {

    TIME("按时间"),
    CELL_NUMBER("按细胞个数");

    private String modeName;

    SampleMode(String name) {
        modeName = name;
    }

    public static SampleMode fromString(String modeName) {
        return Arrays.stream(SampleMode.values())
                .filter(sm -> sm.modeName.equals(modeName))
                .findFirst()
                .orElse(SampleMode.TIME);
    }

    @Override
    public String toString() {
        return modeName;
    }
}
