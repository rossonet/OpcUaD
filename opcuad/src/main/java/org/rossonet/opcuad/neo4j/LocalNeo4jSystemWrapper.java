package org.rossonet.opcuad.neo4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.exceptions.ServiceUnavailableException;
import org.rossonet.utils.FileSystemHelper;
import org.rossonet.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalNeo4jSystemWrapper implements Neo4jSystemWrapper {

	private static final Logger logger = LoggerFactory.getLogger(LocalNeo4jSystemWrapper.class);

	private final static Path NEO4J_DIRECTORY = Paths.get("neo4j-engine");

	private static void decompress(final Path in, final Path out) throws IOException {
		try {
			final FileInputStream inputStream = new FileInputStream(in.toAbsolutePath().toString());
			final GzipCompressorInputStream gzip = new GzipCompressorInputStream(inputStream);
			final TarArchiveInputStream fin = new TarArchiveInputStream(gzip);
			TarArchiveEntry entry;
			while ((entry = fin.getNextTarEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}
				final File curfile = new File(out.toFile(), entry.getName());
				final File parent = curfile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				final FileOutputStream output = new FileOutputStream(curfile);
				IOUtils.copy(fin, output);
				output.flush();
				output.close();

			}
			fin.close();
			gzip.close();
			inputStream.close();
		} catch (final Exception a) {
			logger.error(LogHelper.stackTraceToString(a, 10));
		}
	}

	public final String ORIGINAL_TGZ_URL = "https://www.rossonet.net/dati/neo4j/neo4j-community-4.4.13-unix.tar.gz";

	private final String actualPassword = "Password!123.";

	private final Path archiveFile = Paths.get(NEO4J_DIRECTORY.toAbsolutePath().toString(), "neo4j-community.tar.gz");

	private final Path binaryAdminFile = Paths.get(NEO4J_DIRECTORY.toAbsolutePath().toString(),
			"neo4j-community/neo4j-community-4.4.13/bin/neo4j-admin");
	// public final String ORIGINAL_TGZ_URL =
	// "https://www.rossonet.net/dati/neo4j/neo4j-community-5.1.0-unix.tar.gz";
	// private final Path binaryFile =
	// Paths.get(NEO4J_DIRECTORY.toAbsolutePath().toString(),"neo4j-community/neo4j-community-5.1.0/bin/neo4j");
	private final Path binaryFile = Paths.get(NEO4J_DIRECTORY.toAbsolutePath().toString(),
			"neo4j-community/neo4j-community-4.4.13/bin/neo4j");
	private Driver client = null;

	private final Path workingDirectory = Paths.get(NEO4J_DIRECTORY.toAbsolutePath().toString(), "neo4j-community");

	@Override
	public void dumpDatabase(final String database, final Path targetPath) {
		try {
			final String changePasswordCommand[] = new String[] { binaryAdminFile.toAbsolutePath().toString(), "dump",
					"--database=" + database, "--to=" + targetPath.toAbsolutePath().toString() };
			final ProcessBuilder changePasswordProcessBuilder = new ProcessBuilder(changePasswordCommand);
			changePasswordProcessBuilder.directory(workingDirectory.toFile());
			changePasswordProcessBuilder.inheritIO();
			final Process neo4jChangePassword = changePasswordProcessBuilder.start();
			neo4jChangePassword.waitFor(60, TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error(LogHelper.stackTraceToString(e, 10));
		}
	}

	@Override
	public String getActualPassword() {
		return actualPassword;
	}

	@Override
	public Driver getNeo4jClient() {
		if (client == null) {
			final AuthToken authToken = AuthTokens.basic("neo4j", actualPassword);
			try {
				final Driver driver = GraphDatabase.driver("bolt://127.0.0.1:7687", authToken);
				client = driver;
				boolean connected = false;
				int retry = 0;
				while ((!connected) && retry < 60) {
					connected = verifyConnection();
					retry++;
					Thread.sleep(1000L);
				}
			} catch (final Exception a) {
				logger.error(LogHelper.stackTraceToString(a, 10));
			}
		}
		return client;

	}

	@Override
	public void loadDatabase(final String database, final Path sourcePath) {
		try {
			final String changePasswordCommand[] = new String[] { binaryAdminFile.toAbsolutePath().toString(), "dump",
					"--database=" + database, "--from=" + sourcePath.toAbsolutePath().toString(), "--force" };
			final ProcessBuilder changePasswordProcessBuilder = new ProcessBuilder(changePasswordCommand);
			changePasswordProcessBuilder.directory(workingDirectory.toFile());
			changePasswordProcessBuilder.inheritIO();
			final Process neo4jChangePassword = changePasswordProcessBuilder.start();
			neo4jChangePassword.waitFor(60, TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error(LogHelper.stackTraceToString(e, 10));
		}
	}

	@Override
	public void shutdown() {
		try {
			final String startingCommand[] = new String[] { binaryFile.toAbsolutePath().toString(), "stop" };
			final ProcessBuilder neo4jProcessBuilder = new ProcessBuilder(startingCommand);
			neo4jProcessBuilder.directory(workingDirectory.toFile());
			neo4jProcessBuilder.inheritIO();
			final Process neo4jProcessStop = neo4jProcessBuilder.start();
			neo4jProcessStop.waitFor(60, TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error(LogHelper.stackTraceToString(e, 10));
		}

	}

	@Override
	public void startup() {
		try {
			installIfNeeded();
			final String startingCommand[] = new String[] { binaryFile.toAbsolutePath().toString(), "start" };
			final ProcessBuilder neo4jProcessBuilder = new ProcessBuilder(startingCommand);
			neo4jProcessBuilder.directory(workingDirectory.toFile());
			neo4jProcessBuilder.inheritIO();
			final Process neo4jProcess = neo4jProcessBuilder.start();
			neo4jProcess.waitFor(60, TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error(LogHelper.stackTraceToString(e, 10));
		}

	}

	@Override
	public boolean verifyConnection() {
		boolean connected = false;
		try {
			client.verifyConnectivity();
			connected = true;
		} catch (final ServiceUnavailableException s) {
			logger.error(LogHelper.stackTraceToString(s, 2) + "\nretry...");
		}
		return connected;
	}

	private void addWritePermission(final Path target) {
		try {
			final String changePermission[] = new String[] { "chmod", "+x", target.toAbsolutePath().toString() };
			final ProcessBuilder changePermissionProcessBuilder = new ProcessBuilder(changePermission);
			changePermissionProcessBuilder.directory(workingDirectory.toFile());
			changePermissionProcessBuilder.inheritIO();
			final Process neo4jPermissionChange = changePermissionProcessBuilder.start();
			neo4jPermissionChange.waitFor(60, TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error(LogHelper.stackTraceToString(e, 10));
		}

	}

	private void changePassword() {
		try {
			final String changePasswordCommand[] = new String[] { binaryAdminFile.toAbsolutePath().toString(),
					"set-initial-password", actualPassword };
			final ProcessBuilder changePasswordProcessBuilder = new ProcessBuilder(changePasswordCommand);
			changePasswordProcessBuilder.directory(workingDirectory.toFile());
			changePasswordProcessBuilder.inheritIO();
			final Process neo4jChangePassword = changePasswordProcessBuilder.start();
			neo4jChangePassword.waitFor(60, TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error(LogHelper.stackTraceToString(e, 10));
		}
	}

	private boolean checkDecompressVersionExists() {
		return Files.exists(binaryFile) && Files.isExecutable(binaryFile);
	}

	private void createDirectory() throws IOException {
		Files.createDirectory(NEO4J_DIRECTORY);

	}

	private void decompressArchive() throws IOException {
		Files.createDirectory(workingDirectory);
		decompress(archiveFile, workingDirectory);

	}

	private void deleteDirectoryIfExists() throws IOException {
		FileSystemHelper.deleteDirectory(NEO4J_DIRECTORY.toFile());
		// Files.deleteIfExists(NEO4J_DIRECTORY);
	}

	private void downloadArchive() throws IOException {
		final InputStream in = new URL(ORIGINAL_TGZ_URL).openStream();
		Files.copy(in, archiveFile, StandardCopyOption.REPLACE_EXISTING);

	}

	void installIfNeeded() throws IOException, InterruptedException {
		if (!checkDecompressVersionExists()) {
			deleteDirectoryIfExists();
			createDirectory();
			downloadArchive();
			decompressArchive();
			logger.info("Neo4j installed from " + ORIGINAL_TGZ_URL);
			addWritePermission(binaryFile);
			addWritePermission(binaryAdminFile);
			changePassword();
		} else {
			logger.info("Neo4j is already installed");
		}

	}

}
