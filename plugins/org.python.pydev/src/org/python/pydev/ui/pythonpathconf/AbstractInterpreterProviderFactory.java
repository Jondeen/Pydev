/******************************************************************************
* Copyright (C) 2013  Jonah Graham
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Jonah Graham <jonah@kichwacoders.com> - initial API and implementation
******************************************************************************/
package org.python.pydev.ui.pythonpathconf;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.python.pydev.shared_core.io.FileUtils;

public abstract class AbstractInterpreterProviderFactory implements IInterpreterProviderFactory {

    public AbstractInterpreterProviderFactory() {
        super();
    }

    private static class InterpreterFileFilter implements FileFilter {
        private String expectedFilename;

        public InterpreterFileFilter(String expectedFilenameHead) {
            this.expectedFilename = expectedFilenameHead;
        }

        @Override
        public boolean accept(File pathname) {
            if (!Pattern.matches(expectedFilename, pathname.getName().toLowerCase())) {
                return false;
            }
            return true;
        }

    }

    /**
     * Searches a set of paths for files whose names match any of the provided patterns.
     * @param pathsToSearch The paths to search for files.
     * @param expectedPatterns A list of regex patterns that the filenames to find must match.
     * The patterns are in order of decreasing priority, meaning that filenames matching the
     * pattern at index i will appear earlier in the returned array than filenames matching
     * patterns at index i+1.
     * @return An array of all matching filenames found, in order of decreasing priority.
     */
    public String[] searchPaths(java.util.Set<String> pathsToSearch, String[] expectedPatterns) {
        LinkedList<String> allPaths = new LinkedList<String>();

        for (String expectedPattern : expectedPatterns) {
            SortedSet<String> paths = new TreeSet<String>();
            InterpreterFileFilter filter = new InterpreterFileFilter(expectedPattern);
            for (String s : pathsToSearch) {
                if (s.trim().length() > 0) {
                    File file = new File(s.trim());
                    if (file.isDirectory()) {
                        File[] available = file.listFiles(filter);
                        for (File afile : available) {
                            paths.add(FileUtils.getFileAbsolutePath(new File(file, afile.getName())));
                        }
                    }
                }
            }
            for (String path : paths) {
                if (!allPaths.contains(path)) {
                    allPaths.add(path);
                }
            }
        }
        return allPaths.toArray(new String[allPaths.size()]);
    }
}