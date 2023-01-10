package net.getquicker.hook;

public abstract class BaseSubHook {

    protected ClassLoader mClassLoader;

    public BaseSubHook(ClassLoader classLoader) {
        mClassLoader = classLoader;
    }

    public abstract void startHook();

}
