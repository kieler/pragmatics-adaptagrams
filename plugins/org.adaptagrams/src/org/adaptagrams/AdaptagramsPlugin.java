/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2013 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package org.adaptagrams;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class of this plugin, is responsible to properly load the dynamic
 * libraries of the adaptagrams constrained layout project.
 *
 * @author uru
 */
public class AdaptagramsPlugin implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /**
     * (non-Javadoc).
     *
     * @param bundleContext
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     * @throws Exception e
     */
    public void start(final BundleContext bundleContext) throws Exception {
        AdaptagramsPlugin.context = bundleContext;

        loadSharedAdaptagramsLibrary();
    }

    /**
     * (non-Javadoc).
     *
     * @param bundleContext
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     * @throws Exception e
     */
    public void stop(final BundleContext bundleContext) throws Exception {
        AdaptagramsPlugin.context = null;
    }

    /**
     * A helper enumeration for identifying the operating system.
     */
    private enum OS {
        LINUX32, LINUX64, WIN32, WIN64, OSX32, OSX64, UNKNOWN
    }

    /*
     * In the upcoming definitions the array order reflects the lib dependencies.
     */

    /** the path for the library bin directory. */
    public static final String LIBRARY_PATH = "/lib/";

    /** the relative path for the linux32 library. */
    public static final String[] LIBS_LINUX32 = new String[] { "linux32/adaptagrams.so" };
    /** the relative path for the linux64 library. */
    public static final String[] LIBS_LINUX64 = new String[] { "linux64/adaptagrams.so" };
    /** the relative path for the win32 library. */
    public static final String[] LIBS_WIN32 = new String[] {
            "win32/libgcc_s_dw2-1.dll",
            "win32/libstdc++-6.dll",
            "win32/adaptagrams.dll" };
    /** the relative path for the win64 library. */
    public static final String[] LIBS_WIN64 = new String[] {
            "win64/libwinpthread-1.dll",
            "win64/libgcc_s_seh-1.dll",
            "win64/libstdc++-6.dll",
            "win64/adaptagrams.dll" };
    /** the relative path for the osx32 library. */
    public static final String[] LIBS_OSX32 = new String[] { "osx32/adaptagrams.dylib" };
    /** the relative path for the osx64 library. */
    public static final String[] LIBS_OSX64 = new String[] { "osx64/adaptagrams.dylib" };

    /**
     * Detect the operating system from system properties.
     *
     * @return the operating system
     */
    private static OS detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        String jvm = System.getProperty("sun.arch.data.model");
        if (os.contains("linux")) {
            if (jvm.equals("64")) {
                return OS.LINUX64;
            } else if (jvm.equals("32")) {
                return OS.LINUX32;
            }
        } else if (os.contains("win")) {
            if (jvm.equals("64")) {
                return OS.WIN64;
            } else if (jvm.equals("32")) {
                return OS.WIN32;
            }
        } else if (os.contains("mac")) {
            if (jvm.equals("64")) {
                return OS.OSX64;
            } else if (jvm.equals("32")) {
                return OS.OSX32;
            }
        }
        return OS.UNKNOWN;
    }

    /**
     * Loading the actual libraries into the runtime environment.
     */
    private static void loadSharedAdaptagramsLibrary() {
        try {

            String[] libs = null;
            OS os = detectOS();
            switch (os) {
            case LINUX32:
                libs = LIBS_LINUX32;
                break;
            case LINUX64:
                libs = LIBS_LINUX64;
                break;
            case WIN32:
                libs = LIBS_WIN32;
                break;
            case WIN64:
                libs = LIBS_WIN64;
                break;
            case OSX32:
                libs = LIBS_OSX32;
                break;
            case OSX64:
                libs = LIBS_OSX64;
                break;
            default:
                throw new RuntimeException("Unsupported operating system.");
            }

            Bundle bundle = AdaptagramsPlugin.getContext().getBundle();
            for (String lib : libs) {
                loadLibrary(bundle, LIBRARY_PATH + lib);
            }

        } catch (IOException e) {
            throw new RuntimeException("Adaptagrams dynamic binary could not be loaded.");
        }
    }

    private static void loadLibrary(final Bundle bundle, final String path) throws IOException {
        URL url = FileLocator.find(bundle, new Path(path), null);
        if (url == null) {
            throw new RuntimeException("Library '" + path + "' could not be located.");
        }
        File execFile = new File(FileLocator.resolve(url).getFile());
        System.load(execFile.getAbsolutePath());
    }
}
