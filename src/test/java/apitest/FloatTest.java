package apitest;

public class FloatTest {

    public static void main(String[] args) {
        float a = 1.24536f;
        System.out.println(a * 10);

        System.out.println(Math.round(a * 1000f));
        System.out.println(Math.round(a * 1000f) / 1000f);

        System.out.println(Double.valueOf(String.format("%.3f", a)));
    }
}
