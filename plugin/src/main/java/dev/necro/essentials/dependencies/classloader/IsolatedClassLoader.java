package dev.necro.essentials.dependencies.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Isolated classloader to prevent dependency conflicts
 */
public class IsolatedClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public IsolatedClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }

    @Override
    public void close() throws java.io.IOException {
        super.close();
    }
}