package com.github.davigon.mplus;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgrammeDownloaderMain {
    private static final Logger LOGGER = Logger.getLogger(ProgrammeDownloaderMain.class.getName());

    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "Comenzando proceso de descarga de programación de mplus");
        ProgrammeProvider programmeProvider = new ProgrammeProvider();
        try {
            Location location = Location.create();
            Path downloadDestination = location.join("programmes.xml");
            programmeProvider.download(LocalDate.now(), 3, downloadDestination);
            LOGGER.log(Level.INFO, "Proceso finalizado");
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "No se ha podido descargar el fichero en la ruta especificada");
        }
    }
}
