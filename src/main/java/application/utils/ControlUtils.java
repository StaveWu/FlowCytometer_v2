package application.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class ControlUtils {

    private static final Logger log = LoggerFactory.getLogger(ControlUtils.class);
    private static final ApplicationContext springContext = ApplicationContextUtils.getContext();

    public static <T> T getController(Class<T> clazz) throws Exception {
        T controller;
        if (springContext == null) {
            controller = clazz.newInstance();
            log.info("using manual constructor as {}", clazz.getName());
        } else {
            log.info("using spring bean as {}", clazz.getName());
            controller = springContext.getBean(clazz);
        }
        return controller;
    }
}
