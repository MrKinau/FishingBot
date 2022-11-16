package systems.kinau.fishingbot.utils.reflect;

import java.lang.reflect.Method;

public class Reflect {

    public static MethodAccessor getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Method method;
        try {
            try {
                method = clazz.getDeclaredMethod(methodName, args);
            } catch (NoSuchMethodException e) {
                method = clazz.getMethod(methodName, args);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No method '" + methodName + "' found in '" + clazz.getCanonicalName() + "'", e);
        }
        method.setAccessible(true);

        return new DefaultMethodAccessor(method);
    }

}
