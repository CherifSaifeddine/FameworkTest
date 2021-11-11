package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * Classe utilitaire liée à windows
 *
 */
public class AFWinUtils {
	private AFWinUtils() {
		throw new AssertionError("Il ne doit pas être possible d'instancier cette classe.");
	}

	/**
	 * Kill un webdriver en prenant son nom en paramètre
	 * 
	 * @param execName chromedriver.exe par exemple
	 * 
	 * @return false si on a essayé de kill qqch qui n'est pas un driver ; true
	 *         sinon
	 */
	public static boolean killWebDriverProcess(String execName) {
		// Evitons à l'user de kill tout et n'importe quoi
		if (execName == null || !execName.toLowerCase().contains("driver")
				|| !execName.matches("([a-zA-Z0-9])+(.exe)$")) {
			Trace.error("L'appel à cette méthode est uniquement destiné à tuer des webdrivers pour l'instant.");
			return false;
		}

		String command = "taskkill /F /IM " + execName;
		AFWinUtils.executeWinCommand(command);
		return true;
	}

	/**
	 * Execution d'une commande windows<br>
	 * Cette méthode doit rester privée et statique !
	 * 
	 * @param cmd
	 */
	private static void executeWinCommand(String cmd) {
		Trace.info("Exécution de la commande windows : " + cmd);
		try {
			// Run Windows command
			Process process = Runtime.getRuntime().exec(cmd);

			// Get input streams
			try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				readCmdResult("standard", stdInput);
			}
			try (BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				readCmdResult("erreur", stdError);
			}
		} catch (Exception e) {
			Trace.error("Erreur durant l'exécution de la commande windows : ", e);
		}
	}

	/**
	 * Lecture de la sortie d'une commande
	 * 
	 * @param stdName nom du stream
	 * @param std     le stream
	 * @throws IOException
	 */
	private static void readCmdResult(String stdName, BufferedReader std) throws IOException {
		Trace.info(">> Sortie " + stdName + " : ");

		// Read command standard output
		String s;
		boolean output = false;
		while ((s = std.readLine()) != null) {
			output = true;
			Trace.info(">" + s);
		}

		if (!output) {
			Trace.info("> Aucune sortie " + stdName + ".");
		}
	}
}
