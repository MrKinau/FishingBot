/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    //                                  Event / Listener
    @Getter private Map<Class<? extends Event>, List<Method>> registeredListener = new HashMap<>();
    @Getter private Map<Class, Listener> classToInstanceMapping = new HashMap<>();

    public void registerListener(Listener listener) {
        List<Method> annotatedMethods = new ArrayList<>();
        List<Class<? extends Event>> paramters = new ArrayList<>();
        Method[] methods = listener.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                if(method.getParameterCount() != 1)
                    throw new EventException("An @EventHandler annotated method should have only one parameter");
                if(!Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                    throw new EventException("An @EventHandler annotated method should have an Event as parameter");
                annotatedMethods.add(method);
                paramters.add((Class<? extends Event>) method.getParameterTypes()[0]);
            }
        }

        for(int i = 0; i < annotatedMethods.size(); i++) {
            if(registeredListener.containsKey(paramters.get(i))) {
                List<Method> oldMethods = registeredListener.get(paramters.get(i));
                oldMethods.add(annotatedMethods.get(i));
                registeredListener.put(paramters.get(i), oldMethods);
            } else {
                List<Method> methodList = new ArrayList<>();
                methodList.add(annotatedMethods.get(i));
                registeredListener.put(paramters.get(i), methodList);
            }
        }

        classToInstanceMapping.put(listener.getClass(), listener);
    }

    public void callEvent(Event event) {
        if(!registeredListener.containsKey(event.getClass()))
            return;
        registeredListener.get(event.getClass()).forEach(method -> {
            try {
                method.invoke(classToInstanceMapping.get(method.getDeclaringClass()), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
}
