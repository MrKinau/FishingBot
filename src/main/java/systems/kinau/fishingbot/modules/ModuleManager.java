package systems.kinau.fishingbot.modules;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {

    @Getter private List<Module> loadedModules = new CopyOnWriteArrayList<>();

    public void disableAll() {
        getLoadedModules().stream()
                .filter(module -> !module.getClass().equals(HandshakeModule.class))
                .forEach(Module::disable);
        getLoadedModules().clear();
    }

    public void enableModule(Module module) {
        if (isLoaded(module))
            return;
        getLoadedModules().add(module);
        module.enable();
    }

    public void disableModule(Class moduleClass) {
        Optional<Module> loaded = getLoadedModule(moduleClass);
        loaded.ifPresent(module -> {
            module.disable();
            getLoadedModules().remove(module);
        });
    }

    public boolean isLoaded(Module module) {
        return getLoadedModules().stream().anyMatch(m -> m.getClass().getName().equals(module.getClass().getName()));
    }

    public Optional<Module> getLoadedModule(Class moduleClass) {
        if (moduleClass == null)
            return Optional.empty();
        if (!Module.class.isAssignableFrom(moduleClass))
            return Optional.empty();
        return getLoadedModules().stream().filter(m -> m.getClass().getName().equals(moduleClass.getName())).findAny();
    }
}
