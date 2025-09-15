package aifu.project.libraryweb.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdateUtils {
    private UpdateUtils() {
    }

    public static <T> void updateIfChanged(T newValue,
                                           Supplier<T> getter,
                                           Consumer<T> setter) {
        T oldValue = getter.get();
        if (!Objects.equals(newValue, oldValue)) {
            setter.accept(newValue);
        }
    }

    public static void updateIfChanged(String newValue,
                                       Supplier<String> getter,
                                       Consumer<String> setter) {
        String oldValue = getter.get();
        String processedValue = newValue;

        if (processedValue != null) {
            processedValue = processedValue.trim();
            if (processedValue.isEmpty()) {
                processedValue = null;
            }
        }

        if (!Objects.equals(processedValue, oldValue)) {
            setter.accept(processedValue);
        }
    }
}
