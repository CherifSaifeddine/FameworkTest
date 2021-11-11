package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Méthodes utilitaires communes à AGL et PTA<br>
 * Pour les opérations sur les fichiers
 *
 */
public class AFFileUtils {

	/**
	 * Home de téléchargement de fichiers
	 */
	public static final String DEFAULT_DOWNLOAD_DIR_WIN = System.getProperty("user.home") + "\\Downloads";

	/**
	 * Types de fichiers téléchargeables, utilisé notamment dans la config du
	 * driver FF
	 */
	public static final String DOWNLOAD_ACCEPTED_MIMETYPES = "application/zip,application/octet-stream,image/jpeg,application/vnd.ms-outlook,application/vnd.ms-excel,text/html,application/pdf,"
			+ "text/plain,application/binary,text/csv,application/csv,application/excel,text/comma-separated-values,text/xml,application/xml,application/download";

	/**
	 * Timeout pour l'attente de téléchargement d'un fichier
	 */
	public static final long DEFAULT_DOWNLOAD_TIMEOUT_SEC = 15;

	/**
	 * Encoding du fichier passé dans readFileAsString
	 */
	private static Charset readFileAsString_Encoding = StandardCharsets.UTF_8;

	/**
	 * Méthode permettant de supprimer des fichiers dans un répertoire
	 * 
	 * @param _sRepertoire chemin avec soit des / soit des \\
	 * 
	 * @param _sFichier    nom du fichier pouvant contenir des étoiles (*)
	 */
	public static void deleteFilesInDirectory(String sRepertoire, String sFichier) {
		Trace.debug("deleteFilesInDirectory:: DEBUT");
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(sRepertoire), sFichier)) {
			dirStream.forEach(path -> {
				File f = path.toFile();
				if (f.exists()) {
					boolean deleted = f.delete();
					Trace.debug("deleteFilesInDirectory:: Suppression de " + path + " : " + deleted);
				}
			});
		} catch (Exception e) {
			Trace.error("deleteFilesInDirectory:: Erreur d'ouverture du repertoire " + sRepertoire + " : "
					+ e.getMessage());
			return;
		}
		Trace.debug("deleteFilesInDirectory:: FIN");
	}

	public static void deleteSpecificFileOrFailIfExist(File downloadedFile) throws FileAlreadyExistsException {
		if (downloadedFile != null && downloadedFile.exists()) {
			if (!downloadedFile.delete()) {
				throw new FileAlreadyExistsException("Cannot delete file : " + downloadedFile.getAbsolutePath());
			}
		}
	}

	/**
	 * Compare le contenu de 2 fichiers (chemin complet) passés en paramètre
	 * 
	 * @param _sFichier1           fichier 1 avec chemin
	 * @param _sFichier2           fichier 2 avec chemin
	 * @param ignoreEndOfLine      faut-il ignorer les retours à la ligne
	 *                             unix/windows
	 * @param ignoreEOLFileCharset utilisé uniquement si ignoreEndOfLine=true : le
	 *                             charset pour les fichiers (si null utilise le
	 *                             charset système)
	 * 
	 * @return false si différents/null/inexistants, true sinon
	 * 
	 * @throws IOException
	 */
	public static boolean compareFilesContent(String sFichier1, String sFichier2, boolean ignoreEndOfLine,
			String ignoreEOLFileCharset) throws IOException {
		Trace.debug("AFFileUtils.compareFilesContent:: Comparaison de");
		Trace.debug(sFichier1);
		Trace.debug("AVEC");
		Trace.debug(sFichier2);

		// Si l'un des deux fichiers n'est pas trouvé
		// Alors un FileNotFoundException sera remonté
		File file1 = AFFileUtils.getFile(sFichier1);
		File file2 = AFFileUtils.getFile(sFichier2);

		boolean contentEquals;
		if (ignoreEndOfLine) {
			contentEquals = FileUtils.contentEqualsIgnoreEOL(file1, file2, ignoreEOLFileCharset);
		} else {
			contentEquals = FileUtils.contentEquals(file1, file2);
		}

		Trace.debug("AFFileUtils.compareFilesContent:: contentEquals = " + contentEquals + " " + "("
				+ "ignoreEndOfLine=" + ignoreEndOfLine + ", ignoreEOLFileCharset=" + ignoreEOLFileCharset + ")");
		return contentEquals;
	}

	/**
	 * Gardé pour rétro compatibilité, sera déprécié dans la V2 du framework
	 * et supprimé en V3<br>
	 * Use {@link #compareFilesContent(String, String, boolean, String)}
	 * 
	 * @param sFichier1
	 * @param sFichier2
	 * @return
	 * @throws IOException
	 */
	public static boolean compareFilesContent(String sFichier1, String sFichier2) throws IOException {
		return compareFilesContent(sFichier1, sFichier2, false, null);
	}

	/**
	 * Lit un fichier et retourne un String avec le contenu
	 * 
	 * @param _sFichier le chemin complet du fichier
	 * 
	 * @return String avec le contenu ou null en cas d'erreur
	 * @throws Exception
	 */
	public static String readFileAsString(String sFichier) throws Exception {
		String fileContent = null;
		try {

			File file = AFFileUtils.getFile(sFichier);
			Path path = Paths.get(file.toURI());

			byte[] fileBytes = Files.readAllBytes(path);

			// Fichus encodages, je suis preneur d'une meilleure solution
			// Les fichiers qu'on lit peuvent venir de différents logiciels
			// (pleiades,hra,4you)
			// .. et différentes applis avec une configuration d'encoding pas forcément
			// similaire
			readFileAsString_Encoding = StandardCharsets.UTF_8;
			fileContent = new String(fileBytes, readFileAsString_Encoding);
			if (fileContent.contains("�")) {
				readFileAsString_Encoding = StandardCharsets.US_ASCII;
				fileContent = new String(fileBytes, readFileAsString_Encoding);
				if (fileContent.contains("�")) {
					readFileAsString_Encoding = StandardCharsets.ISO_8859_1;
					fileContent = new String(fileBytes, readFileAsString_Encoding);
				}
			}
		} catch (Exception e) {
			Trace.error("Erreur d'ouverture du fichier " + sFichier + " : " + e.getMessage());
			throw e;
		}
		return fileContent;
	}

	/**
	 * Copie un fichier vers une autre destination et avec un autre nom
	 * 
	 * @param _sFichierSrc  chemin et nom du fichier source
	 * @param _sFichierDest chemin et nouveau nom du fichier destination
	 * @throws Exception
	 */
	public static void copyFileAnywhere(String sFichierSrc, String sFichierDest) throws Exception {
		Trace.debug("copyFileAnywhere:: Copie de");
		Trace.debug(sFichierSrc);

		// Si le fichier source n'est pas trouvé
		// Alors un FileNotFoundException sera remonté
		File srcFile = AFFileUtils.getFile(sFichierSrc);

		// Pour le fichier destination il faut savoir si le chemin est relatif
		if (!new File(sFichierDest).isAbsolute()) {
			// Si il l'est, il faut le compléter
			sFichierDest = System.getProperty("user.dir") + "\\src\\test\\resources\\" + sFichierDest;
		}

		Trace.debug("VERS");
		Trace.debug(sFichierDest);

		// DEBUT de la copie
		try (InputStream input = new FileInputStream(srcFile);
				OutputStream output = new FileOutputStream(sFichierDest)) {

			IOUtils.copy(input, output);

		} catch (Exception e) {
			Trace.error("copyFileAnywhere:: Erreur durant la copie.");
			Trace.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Méthode permettant de récupérer un fichier<br>
	 * Que le chemin soit absolu<br>
	 * Ou relatif (par rapport à src/test/resources)
	 * 
	 * @param filePath
	 * 
	 * @return un objet File ou throw FileNotFoundException si non trouvé
	 * @throws FileNotFoundException si fichier non trouvé / null
	 */
	public static File getFile(String filePath) throws FileNotFoundException {
		if (filePath != null) {
			// Test 1 : chemin absolu
			File theFile = new File(filePath);
			if (theFile.exists())
				return theFile;

			// Test 2 : chemin relatif depuis src/test/resources
			File fromResources = new File("src/test/resources/" + filePath);
			if (fromResources.exists())
				return fromResources;

			// Test 3 : Chargement depuis le classloader
			URL propertiesFileResource = AFFileUtils.class.getClassLoader().getResource(filePath);
			if (propertiesFileResource != null) {
				File aFile = new File(propertiesFileResource.getFile());
				if (aFile.exists())
					return aFile;
			}

		}

		throw new FileNotFoundException("AFFileUtils::getFile : Le fichier n'existe pas : " + filePath
				+ " (absolu / relatif via test-resources / classpath)");
	}

	/**
	 * Remplacer une chaine dans un fichier
	 * 
	 * @param file      (chemin absolu ou relatif par rapport à
	 *                  src/test/resources/)
	 * @param oldString
	 * @param newString
	 * @throws Throwable
	 */
	public static void replaceStringInFile(String file, String oldString, String newString) throws Throwable

	{

		String fileString = AFFileUtils.readFileAsString(file);
		String newFileString = fileString.replace(oldString, newString);
		FileUtils.writeStringToFile(AFFileUtils.getFile(file), newFileString, AFFileUtils.readFileAsString_Encoding);
	}

	/**
	 * Suppression de N lignes dans un fichier
	 * 
	 * @param file      (chemin absolu ou relatif par rapport à
	 *                  src/test/resources/)
	 * @param numLignes liste des lignes à effacer
	 * @return true si au moins une ligne a été trouvée ; false sinon
	 * @throws Exception
	 */
	public static boolean removeLineFromFile(String file, String linesToDelete) throws Exception {
		List<Integer> numLignes = getIntListFromGluString(linesToDelete);

		boolean lineFound = false;
		String[] fileString = AFFileUtils.readFileAsString(file).split("\n");
		StringBuilder newFileString = new StringBuilder();
		for (int i = 0; i < fileString.length; i++) {
			if (numLignes.contains((i + 1))) {
				Trace.debug("removeLineFromFile::Suppression de la ligne " + (i + 1) + " du fichier " + file);
				lineFound = true;
				continue;
			}
			newFileString.append(fileString[i] + "\n");
		}
		FileUtils.writeStringToFile(AFFileUtils.getFile(file), newFileString.toString(),
				AFFileUtils.readFileAsString_Encoding);
		return lineFound;
	}

	protected static List<Integer> getIntListFromGluString(String linesToDelete) {
		List<Integer> linesList =
				// On récupère un String[] des valeurs séparées par virgule
				Arrays.asList(linesToDelete.split("\\s*,\\s*")).stream()
						// Qu'on trim
						.map(String::trim)
						// Qu'on parse en int
						.map(Integer::parseInt)
						// Pour mettre tout ça dans la List<Integer>
						.collect(Collectors.toList());
		return linesList;
	}

	public static boolean checkFileExistenceAfterDownload(String expectedFilename, File downloadedFile)
			throws Exception {
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName(), expectedFilename,
				(downloadedFile == null ? "nullFile" : downloadedFile.getAbsolutePath())))
			return false;

		boolean found = false; // downloaded file found ?

		// Check existence after download
		if (downloadedFile != null && downloadedFile.exists()) {
			// Le fichier existe directement, c'est rare
			found = true;
		} else {
			// Open watchservice
			try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
				// Add watchservice to downloadDir on creation
				Paths.get(AFFileUtils.DEFAULT_DOWNLOAD_DIR_WIN).register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE);

				boolean valid = true; // loop while valid
				long startTime = System.currentTimeMillis();
				do {
					WatchKey watchKey = watchService.poll(AFFileUtils.DEFAULT_DOWNLOAD_TIMEOUT_SEC, TimeUnit.SECONDS);
					if (watchKey == null) {
						if (downloadedFile != null && downloadedFile.exists()) {
							Trace.debug("downloadFileGlu:: EXISTS");
						} else {
							if (downloadedFile != null && new File(downloadedFile.getAbsolutePath()).exists()) {
								Trace.debug("downloadFileGlu:: EXISTS 2");
							}
						}

						throw new Exception("Unable to instanciate WatchKey");
					}
					long currentTime = (System.currentTimeMillis() - startTime) / 1000;
					if (currentTime > AFFileUtils.DEFAULT_DOWNLOAD_TIMEOUT_SEC) {
						Trace.error(
								"downloadFileGlu:: Download operation timed out.. Expected file was not downloaded");
						valid = false;
					} else {
						for (WatchEvent<?> event : watchKey.pollEvents()) {
							if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
								String fileName = event.context().toString();
								Trace.debug("downloadFileGlu:: New File Created: " + fileName);

								if (expectedFilename.equals(fileName)) {
									Trace.debug("downloadFileGlu:: Downloaded file found : " + fileName);
									Thread.sleep(500); // NOSONAR
									found = true;
									valid = false;
									break;
								}
							}
						}

						// Downloaded file not found
						if (!found) {
							currentTime = (System.currentTimeMillis() - startTime) / 1000;
							if (currentTime > AFFileUtils.DEFAULT_DOWNLOAD_TIMEOUT_SEC) {
								Trace.error("downloadFileGlu:: Failed to download expected file (timeout)");
							}
							valid = watchKey.reset();
						}
					}
				} while (valid);
			} catch (Exception e) {
				Trace.error("downloadFileGlu:: Error occured - " + e.getMessage());
				throw e;
			}
		}
		return found;
	}
}
