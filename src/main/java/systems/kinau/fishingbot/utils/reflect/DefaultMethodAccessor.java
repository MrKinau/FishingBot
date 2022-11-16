package systems.kinau.fishingbot.utils.reflect;

import java.lang.reflect.Method;

class DefaultMethodAccessor implements MethodAccessor {

    private final Method method;

    DefaultMethodAccessor(Method method) {
        this.method = method;
    }

    @Override
    public <T> T invoke(Object instance, Object... args) {
        try {
            return (T) method.invoke(instance, args);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Error while invoking method '%s'", method), ex);
        }
    }

    @Override
    public Method getMethod() {
        return method;
    }

}
