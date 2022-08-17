package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.event.Listener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {
    public static List<Class<?>> loadClasses(String basePackage, final String... subPackages) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        assert subPackages != null;
        final JarFile jar;
        try {
            jar = new JarFile(new File(ClassUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return classes;
        }
        for (int i = 0; i < subPackages.length; i++)
            subPackages[i] = subPackages[i].replace('.', '/') + "/";
        basePackage = basePackage.replace('.', '/') + "/";
        try {
            Enumeration<JarEntry> entry = jar.entries();
            while (entry.hasMoreElements()) {
                JarEntry e = entry.nextElement();
                if (e.getName().startsWith(basePackage) && e.getName().endsWith(".class")) {
                    boolean load = subPackages.length == 0;
                    for (final String sub : subPackages) {
                        if (e.getName().startsWith(sub, basePackage.length())) {
                            load = true;
                            break;
                        }
                    }
                    if (load) {
                        final String name = e.getName().replace('/', '.').substring(0, e.getName().length() - ".class".length());
                        try {
                            Class<?> c = Class.forName(name, true, RyuZUInfiniteShop.getPlugin().getClass().getClassLoader());
                            classes.add(c);
                        } catch (final ClassNotFoundException | ExceptionInInitializerError ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } finally {
            try {
                jar.close();
            } catch (final IOException ignored) {
            }
        }
        return classes;
    }
}
