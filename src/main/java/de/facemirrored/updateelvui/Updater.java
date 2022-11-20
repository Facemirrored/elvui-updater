package de.facemirrored.updateelvui;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;

public class Updater {

    private static final String WOW_FILE_LOCATION = "C:\\Program Files (x86)\\World of Warcraft\\_retail_\\Interface\\AddOns";
    private static final String BASE_OPERATION_PATH = WOW_FILE_LOCATION + "\\temptToDelete";
    private static final String DOWNLOAD_FILE = BASE_OPERATION_PATH + "\\ElvUI.zip";
    private static final String EXTRACTION_PATH = BASE_OPERATION_PATH + "\\ElvUI_Unzipped";
    private static final String CUT_PATH_BASE_FILE = "ElvUI";
    private static final String CUT_PATH_OPTIONS_FILE = "ElvUI_OptionsUI";
    public static final String U_FUCKED_UP_MSG = "Hoppla, da habe ich wohl misst gebaut... :^[";
    public static final String COULDNT_CREATE_TEMP_DIR_MSG = "Couldn't create temp dir";

    private static String getDownloadLink() throws IOException {

        final var url = new URL("https://www.tukui.org/download.php?ui=elvui");
        final var is = url.openStream();
        final var buffer = new StringBuilder();

        var ptr = 0;

        while ((ptr = is.read()) != -1) {

            buffer.append((char) ptr);
        }

        final var html = buffer.toString();

        return "https://www.tukui.org" + html.substring(html.indexOf("/downloads/elvui"), html.indexOf(".zip")) + ".zip";
    }

    private static void downloadFile() throws IOException {

        try (final var readableByteChannel = Channels.newChannel(new URL(getDownloadLink()).openStream());
             final var fileOutputStream = new FileOutputStream(DOWNLOAD_FILE);
             final var fileChannel = fileOutputStream.getChannel()) {

            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static void main(String[] args) {

        try {

            if (!new File(BASE_OPERATION_PATH).exists()) {

                final var mk = new File(BASE_OPERATION_PATH).mkdirs();

                if (!mk) {

                    throw new IOException(COULDNT_CREATE_TEMP_DIR_MSG);
                }
            }

            downloadFile();

            final var zf = new ZipFile(DOWNLOAD_FILE);
            zf.extractAll(EXTRACTION_PATH);
            Files.move(Path.of(EXTRACTION_PATH + "\\" + CUT_PATH_BASE_FILE), Path.of(WOW_FILE_LOCATION + "\\" + CUT_PATH_BASE_FILE));
            Files.move(Path.of(EXTRACTION_PATH + "\\" + CUT_PATH_OPTIONS_FILE), Path.of(WOW_FILE_LOCATION + "\\" + CUT_PATH_OPTIONS_FILE));

        } catch (final IOException e) {

            e.printStackTrace();
            System.out.println(U_FUCKED_UP_MSG);
            System.out.println("Fehler:\t" + e.getMessage());

        }
    }
}
