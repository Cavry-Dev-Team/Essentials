package dev.necro.essentials.dependencies;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles downloading dependencies from Maven repositories
 */
public class DependencyRepository {

    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

    /**
     * Downloads a dependency
     *
     * @param dependency the dependency to download
     * @param outputPath the output file path
     * @throws IOException if download fails
     */
    public static void download(Dependency dependency, Path outputPath) throws IOException {
        if (Files.exists(outputPath)) {
            return; // Already downloaded
        }

        try {
            URL url = new URL(MAVEN_CENTRAL + dependency.getMavenRepoPath());
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(in);

                if (bytes.length == 0) {
                    throw new IOException("Downloaded file is empty");
                }

                // Verify checksum using the dependency's algorithm (SHA-1 or SHA-256)
                byte[] hash = dependency.createDigest().digest(bytes);
                if (!dependency.checksumMatches(hash)) {
                    throw new IOException("Checksum mismatch for " + dependency.getFileName());
                }

                Files.write(outputPath, bytes);
            }
        } catch (IOException e) {
            throw new IOException("Failed to download " + dependency.getFileName(), e);
        }
    }
}