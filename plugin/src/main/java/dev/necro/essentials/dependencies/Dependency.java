package dev.necro.essentials.dependencies;

import lombok.Getter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Dependencies required by NecroEssentials
 */
public enum Dependency {

    CLOUD_CORE(
            "cloud.commandframework",
            "cloud-core",
            "1.6.2",
            "aeacff2b6cbea206e91b532f091e183d500f7bc8ed34d9713b47be67c24cad45",
            "SHA-256"
    ),
    CLOUD_ANNOTATIONS(
            "cloud.commandframework",
            "cloud-annotations",
            "1.6.2",
            "3fe301cfed4da403dbb1ebad3aeffc55256171f9efa517d362226b6c92751374",
            "SHA-256"
    ),
    CLOUD_PAPER(
            "cloud.commandframework",
            "cloud-paper",
            "1.6.2",
            "674c0ff57d254ddbc7ad09db5e53810ac1c9c60fd747085ef38675b9d323c66a",
            "SHA-256"
    ),
    CLOUD_BUKKIT(
            "cloud.commandframework",
            "cloud-bukkit",
            "1.6.2",
            "5cdd974c0396e2164a376e9ab77cdf0bbda015031e39723b238d7367f9ce30a8",
            "SHA-256"
    ),
    CLOUD_BRIGADIER(
            "cloud.commandframework",
            "cloud-brigadier",
            "1.6.2",
            "511df123afb712350efc2143136e4015ee8f955ea2129045d86d723098c62167",
            "SHA-256"
    ),
    CLOUD_SERVICES(
            "cloud.commandframework",
            "cloud-services",
            "1.6.2",
            "2d1f5cfc2bbb2ee120ae397b3f6f89171b69c94a455844546bc6695b637c3960",
            "SHA-256"
    ),
    CLOUD_TASKS(
            "cloud.commandframework",
            "cloud-tasks",
            "1.6.2",
            "65b79ed843f503aac0c3fb10b4491e2b3ea9cf59fb0f6911601d32cabb8ad8ab",
            "SHA-256"
    ),
    CLOUD_MINECRAFT_EXTRAS(
            "cloud.commandframework",
            "cloud-minecraft-extras",
            "1.6.2",
            "23000333b85fda563c96d3e006519f9c7cb62dce3e185637bbcd473d355fd4bd",
            "SHA-256"
    ),
    ADVENTURE_API(
            "net.kyori",
            "adventure-api",
            "4.9.3",
            "ab8ed55e48cb77a99ca0d40b3c48913fe75bfa5a2c09ed11fd86c03267e18175",
            "SHA-256"
    ),
    ADVENTURE_KEY(
            "net.kyori",
            "adventure-key",
            "4.9.3",
            "7c9ad8211944a12099d5bd384078b9c0a550114eeeabf4b61d08827f3b325b3a",
            "SHA-256"
    ),
    ADVENTURE_PLATFORM_API(
            "net.kyori",
            "adventure-platform-api",
            "4.0.0",
            "c744142658f06d294b550a9bfc713c464e453cefb8084675e327de17581569e8",
            "SHA-256"
    ),
    ADVENTURE_PLATFORM_BUKKIT(
            "net.kyori",
            "adventure-platform-bukkit",
            "4.0.0",
            "cc422f31515c028dc754b5406f94ee96388d3b870cad77cb701faa6390bef470",
            "SHA-256"
    ),
    ADVENTURE_PLATFORM_FACET(
            "net.kyori",
            "adventure-platform-facet",
            "4.0.0",
            "05071d9cdace5783f7ccf6b92a1aeacdf8a2705f1fb92dfc856e2aa33ba16ddf",
            "SHA-256"
    ),
    EXAMINATION_API(
            "net.kyori",
            "examination-api",
            "1.3.0",
            "c9237ffecb05428f6eff86216246ac70ce0b47b04c08ea7ca35020fde57f8492",
            "SHA-256"
    ),
    GEANTYREF(
            "io.leangen.geantyref",
            "geantyref",
            "1.3.11",
            "bc9c03b53917314d21fe6276aceb08aa84bf80dd",
            "SHA-1"
    );

    @Getter
    private final String mavenRepoPath;
    private final String version;
    @Getter
    private final byte[] checksum;
    private final String algorithm;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";
    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

    Dependency(String groupId, String artifactId, String version, String checksumHex, String algorithm) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT,
                groupId.replace(".", "/"),
                artifactId,
                version,
                artifactId,
                version
        );
        this.version = version;
        this.checksum = hexStringToByteArray(checksumHex);
        this.algorithm = algorithm;
    }

    public String getFileName() {
        String name = name().toLowerCase(Locale.ROOT).replace('_', '-');
        return name + "-" + this.version + ".jar";
    }

    public String getDownloadUrl() {
        return MAVEN_CENTRAL + this.mavenRepoPath;
    }

    public boolean checksumMatches(byte[] hash) {
        return Arrays.equals(this.checksum, hash);
    }

    public MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance(this.algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not supported: " + this.algorithm, e);
        }
    }

    /**
     * Converts a hex string to a byte array
     *
     * @param s the hex string
     * @return the byte array
     */
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}