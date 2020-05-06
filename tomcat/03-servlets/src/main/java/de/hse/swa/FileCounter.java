package de.hse.swa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileCounter {

    private final String fileName = "FileCounter.initial";

    public int getCount() throws IOException {

        final File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
            try (final PrintWriter pw = new PrintWriter(f)) {
                pw.println(0);
            }
        }

        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            return Integer.parseInt(reader.readLine());
        }
    }

    public void save(final int count) throws IOException {
        try (final PrintWriter pw = new PrintWriter(fileName)) {
            pw.println(count);
        }
    }
}
