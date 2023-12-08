package com.github.davigon.mplus;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Location {
    private Path jarPath;

    private Location(Path jarPath) {
        this.jarPath = jarPath;
    }

    public static Location create() throws IOException {
        try {
            return new Location(Paths.get((
                    new File(
                            Location.class
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .toURI()))
                    .getParentFile()
                    .getPath()));
        } catch (URISyntaxException e) {
            return new Location(Files.createTempDirectory("mplus"));
        }
    }

    public Path join(String strPath) {
        return this.jarPath.resolve(strPath);
    }
}
