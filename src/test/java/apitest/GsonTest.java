package apitest;

import com.google.gson.Gson;

public class GsonTest {

    public static void main(String[] args) {
        Gson gson = new Gson();
        System.out.println(gson.toJson(new ModeHolder()));
        ModeHolder modeHolder = gson.fromJson(gson.toJson(new ModeHolder()), ModeHolder.class);
        System.out.println(modeHolder.mode);
    }
}

enum Mode {
    TIME("time"),
    NUMBER("number");

    private String name;

    Mode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

class ModeHolder {
    Mode mode = Mode.TIME;
}
