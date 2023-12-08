package com.github.davigon.mplus;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SelectOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgrammeProvider {
    private static final Logger LOGGER = Logger.getLogger(ProgrammeProvider.class.getName());

    private static final String SOURCE_URL = "https://comunicacion.movistarplus.es/programacion/";

    public void download(LocalDate from, int days, Path to) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(
                     // (headless = true) = sin mostrar navegador
                     new BrowserType.LaunchOptions().setHeadless(false));
             BrowserContext browserContext = browser.newContext();
             Page page = browserContext.newPage();
        ) {
            page.waitForNavigation(() -> {
                page.navigate(SOURCE_URL);
            });
            clickAndWaitForExportModal(page);
            selectOptions(page, from, days);
            downloadFile(page, to);
        } catch (TimeoutError | IOException e) {
            LOGGER.log(Level.WARNING, "Descarga fallida. Reintentando descarga...");
            download(from, days, to);
        }
    }

    private void clickAndWaitForExportModal(Page page) {
        page.click("text=\"Exportar programación\"");
        page.waitForSelector("#exportProgramation > div > div > div.modal-header");
    }

    private void selectOptions(Page page, LocalDate from, int days) {
        selectDays(page, from, days);
        selectChannels(page);
        selectFormat(page);
    }

    private void selectDays(Page page, LocalDate from, int days) {
        page.fill("#export-date-from",
                from.toString());
        page.fill("#export-date-to",
                from.plusDays(days - 1L).toString());
    }

    private void selectChannels(Page page) {
        page.waitForSelector("#container-checkbox-categories-export > div:nth-child(1) > small.show");
        int n = page.locator("#container-checkbox-categories-export > div").count();
        for (int i = 1; i < n; i = i + 2) {
            page.dispatchEvent(
                    "#container-checkbox-categories-export > div:nth-child(" + i + ") > label",
                    "click");
        }
    }

    private void selectFormat(Page page) {
        page.selectOption("#export-format",
                new SelectOption().setLabel("Xml"));
    }

    private Path downloadFile(Page page, Path to) throws IOException {
        Download download = page.waitForDownload(() -> {
            page.click("#btn-export-programation");
        });
        Path downloadPath = download.path();
        return Files.copy(downloadPath, to, StandardCopyOption.REPLACE_EXISTING);
    }
}
