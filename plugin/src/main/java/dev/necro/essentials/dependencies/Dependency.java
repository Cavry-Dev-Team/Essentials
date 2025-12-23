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
            "1.8.4",
            "03115ed7ef1d0bd605ceb29a182fadd8fe3ad0cf1a826cde011fbe015916d059",
            "SHA-256"
    ),
    CLOUD_ANNOTATIONS(
            "cloud.commandframework",
            "cloud-annotations",
            "1.8.4",
            "a32e8b69e1627416d15ba1a06792a6a37f74d1b2ac7daa8626514431c8d743cc",
            "SHA-256"
    ),
    CLOUD_PAPER(
            "cloud.commandframework",
            "cloud-paper",
            "1.8.4",
            "4160478a3a292931cf3841970b6179d9aa9ba9be9352f7a6e4fe4a3aca0587ba",
            "SHA-256"
    ),
    CLOUD_BUKKIT(
            "cloud.commandframework",
            "cloud-bukkit",
            "1.8.4",
            "6d18e4ad2ce6aa45b899ca99062281298206eace8f05f7b797a9070439a570cf",
            "SHA-256"
    ),
    CLOUD_BRIGADIER(
            "cloud.commandframework",
            "cloud-brigadier",
            "1.8.4",
            "fd3d7d4eb155f9a7d3ca4f5f4b891ee7ce172b5e3d22636e5dfbd199119482b5",
            "SHA-256"
    ),
    CLOUD_SERVICES(
            "cloud.commandframework",
            "cloud-services",
            "1.8.4",
            "7e371e28ea7d6e83636660f86d1c157e1da7beb105b5c41c323f3177e15b4a4d",
            "SHA-256"
    ),
    CLOUD_TASKS(
            "cloud.commandframework",
            "cloud-tasks",
            "1.8.4",
            "a733115c40aa87174922c056dd4640a76d2b954ecdecbe5f1c6683b1781fc147",
            "SHA-256"
    ),
    CLOUD_MINECRAFT_EXTRAS(
            "cloud.commandframework",
            "cloud-minecraft-extras",
            "1.8.4",
            "c0c0a5cf2a9bc694241fb35914d647d10ce40594428ce98c21c47e6a85bd08d4",
            "SHA-256"
    ),
    ADVENTURE_API(
            "net.kyori",
            "adventure-api",
            "4.26.1",
            "551e536b9ea868f30e72c7900a309b35124ee7d4889fa3b3aed0910299751a26",
            "SHA-256"
    ),
    ADVENTURE_KEY(
            "net.kyori",
            "adventure-key",
            "4.26.1",
            "eec172d63db77b40eb7abeeb25f65eedea89bd30264d057b68b12fecb731be5e",
            "SHA-256"
    ),
    ADVENTURE_PLATFORM_API(
            "net.kyori",
            "adventure-platform-api",
            "4.4.1",
            "ec604628c2b7c165ea74c1fcb3a2d0f03359c2c77a14960e3a0bc2e379670ac2",
            "SHA-256"
    ),
    ADVENTURE_PLATFORM_BUKKIT(
            "net.kyori",
            "adventure-platform-bukkit",
            "4.4.1",
            "cdcb3a66abbf9e393614db5deb27f841117c5b5b7ef52753a2c846fd46c9cdf1",
            "SHA-256"
    ),
    ADVENTURE_PLATFORM_FACET(
            "net.kyori",
            "adventure-platform-facet",
            "4.4.1",
            "20f8e6db34d722a4acccbedcc9b6c02e8ee6b3cab9350b06ab3d1c0b0b8b454f",
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