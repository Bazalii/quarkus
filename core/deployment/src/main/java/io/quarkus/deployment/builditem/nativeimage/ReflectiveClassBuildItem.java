package io.quarkus.deployment.builditem.nativeimage;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * Used to register a class for reflection in native mode
 */
public final class ReflectiveClassBuildItem extends MultiBuildItem {

    private final List<String> className;
    private final boolean methods;
    private final boolean fields;
    private final boolean constructors;
    private final boolean weak;
    private final boolean serialization;

    public ReflectiveClassBuildItem(boolean methods, boolean fields, Class<?>... className) {
        this(true, methods, fields, className);
    }

    public ReflectiveClassBuildItem(boolean constructors, boolean methods, boolean fields, Class<?>... className) {
        this(constructors, methods, fields, false, false, className);
    }

    private ReflectiveClassBuildItem(boolean constructors, boolean methods, boolean fields, boolean weak, boolean serialization,
            Class<?>... className) {
        List<String> names = new ArrayList<>();
        for (Class<?> i : className) {
            if (i == null) {
                throw new NullPointerException();
            }
            names.add(i.getName());
        }
        this.className = names;
        this.methods = methods;
        this.fields = fields;
        this.constructors = constructors;
        this.weak = weak;
        this.serialization = serialization;
        if (weak && serialization) {
            throw new RuntimeException("Weak reflection not supported with serialization");
        }
    }

    public ReflectiveClassBuildItem(boolean methods, boolean fields, String... className) {
        this(true, methods, fields, className);
    }

    public ReflectiveClassBuildItem(boolean constructors, boolean methods, boolean fields, String... className) {
        this(constructors, methods, fields, false, false, className);
    }

    public ReflectiveClassBuildItem(boolean constructors, boolean methods, boolean fields, boolean serialization,
            String... className) {
        this(constructors, methods, fields, false, serialization, className);
    }

    public static ReflectiveClassBuildItem weakClass(String... className) {
        return weakClass(true, true, true, className);
    }

    public static ReflectiveClassBuildItem weakClass(boolean constructors, boolean methods, boolean fields,
            String... className) {
        return new ReflectiveClassBuildItem(constructors, methods, fields, true, false, className);
    }

    public static ReflectiveClassBuildItem serializationClass(String... className) {
        return new ReflectiveClassBuildItem(false, false, false, false, true, className);
    }

    private ReflectiveClassBuildItem(boolean constructors, boolean methods, boolean fields, boolean weak, boolean serialization,
            String... className) {
        for (String i : className) {
            if (i == null) {
                throw new NullPointerException();
            }
        }
        this.className = Arrays.asList(className);
        this.methods = methods;
        this.fields = fields;
        this.constructors = constructors;
        this.weak = weak;
        this.serialization = serialization;
    }

    public List<String> getClassNames() {
        return className;
    }

    public boolean isMethods() {
        return methods;
    }

    public boolean isFields() {
        return fields;
    }

    public boolean isConstructors() {
        return constructors;
    }

    /**
     * @deprecated As of GraalVM 21.2 finalFieldsWritable is no longer needed when registering fields for reflection. This will
     *             be removed in a future verion of Quarkus.
     */
    @Deprecated
    public boolean areFinalFieldsWritable() {
        return false;
    }

    public boolean isWeak() {
        return weak;
    }

    public boolean isSerialization() {
        return serialization;
    }

    public static Builder builder(Class<?>... className) {
        String[] classNameStrings = stream(className)
                .map(aClass -> {
                    if (aClass == null) {
                        throw new NullPointerException();
                    }
                    return aClass.getName();
                })
                .toArray(String[]::new);

        return new Builder()
                .className(classNameStrings);
    }

    public static Builder builder(String... className) {
        return new Builder()
                .className(className);
    }

    public static class Builder {
        private String[] className;
        private boolean constructors = true;
        private boolean methods;
        private boolean fields;
        private boolean weak;
        private boolean serialization;

        private Builder() {
        }

        public Builder className(String[] className) {
            this.className = className;
            return this;
        }

        public Builder constructors(boolean constructors) {
            this.constructors = constructors;
            return this;
        }

        public Builder methods(boolean methods) {
            this.methods = methods;
            return this;
        }

        public Builder fields(boolean fields) {
            this.fields = fields;
            return this;
        }

        /**
         * @deprecated As of GraalVM 21.2 finalFieldsWritable is no longer needed when registering fields for reflection. This
         *             will be removed in a future verion of Quarkus.
         */
        @Deprecated
        public Builder finalFieldsWritable(boolean finalFieldsWritable) {
            return this;
        }

        public Builder weak(boolean weak) {
            this.weak = weak;
            return this;
        }

        public Builder serialization(boolean serialize) {
            this.serialization = serialize;
            return this;
        }

        public ReflectiveClassBuildItem build() {
            return new ReflectiveClassBuildItem(constructors, methods, fields, weak, serialization, className);
        }
    }
}
