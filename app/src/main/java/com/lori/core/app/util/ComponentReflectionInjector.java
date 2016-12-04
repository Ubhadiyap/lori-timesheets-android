package com.lori.core.app.util;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class allows to inject into objects through a base class,
 * so we don't have to repeat injection code everywhere.
 * <p>
 * The performance drawback is about 0.013 ms per injection on a very slow device,
 * which is negligible in most cases.
 * <p>
 * Example:
 * <pre>{@code
 * Component {
 *     void inject(B b);
 * }
 *
 * class A {
 *     void onCreate() {
 *         componentReflectionInjector.inject(this);
 *     }
 * }
 *
 * class B extends A {
 *     @Inject MyDependency dependency;
 * }
 *
 * new B().onCreate() // dependency will be injected at this point
 *
 * class C extends B {
 *
 * }
 *
 * new C().onCreate() // dependency will be injected at this point as well
 * }</pre>
 *
 * @param <T> a type of dagger 2 component.
 */
public final class ComponentReflectionInjector<T> implements Injector {

    private static final ConcurrentMap<Class<?>, Map<Class<?>, Method>> CACHE = new ConcurrentHashMap<>();

    private final Class<T> componentClass;
    private final T component;
    private final Map<Class<?>, Method> methods;

    public ComponentReflectionInjector(Class<T> componentClass, T component) {
        this.componentClass = componentClass;
        this.component = component;
        this.methods = getInjectMethods(componentClass);
    }

    public T getComponent() {
        return component;
    }

    @Override
    public void inject(Object target) {

        Class targetClass = target.getClass();
        Method method = methods.get(targetClass);
        while (method == null && targetClass != null) {
            targetClass = targetClass.getSuperclass();
            method = methods.get(targetClass);
        }

        if (method == null) {
            throw new RuntimeException(String.format("No %s injecting method exists in %s component", target.getClass(), componentClass));
        }

        try {
            method.invoke(component, target);
        } catch (Exception e) {
            throw new RuntimeException("Error while performing injection", e);
        }
    }

    private static Map<Class<?>, Method> getInjectMethods(Class componentClass) {
        Map<Class<?>, Method> methods = CACHE.get(componentClass);
        if (methods == null) {
            synchronized (CACHE) {
                methods = CACHE.get(componentClass);
                //noinspection Java8ReplaceMapGet
                if (methods == null) {
                    methods = doGetInjectMethods(componentClass);
                    CACHE.put(componentClass, methods);
                }
            }
        }
        return methods;
    }

    private static Map<Class<?>, Method> doGetInjectMethods(Class componentClass) {
        Map<Class<?>, Method> methods = new HashMap<>();
        for (Method method : componentClass.getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1) {
                methods.put(params[0], method);
            }
        }
        return methods;
    }
}