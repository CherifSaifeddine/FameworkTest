package utils;

import org.apache.log4j.Logger;

import hooks.AFStartup;



/**
 * Classe servant à afficher des traces
 * 
 */
public class Trace {
	private static final Logger LOGGER = Logger.getLogger(Trace.class);

	/**
	 * Indentation des traces pour les glues au sein des macros<br>
	 * Sert à voir facilement quand des glues sont imbriquées dans des macros
	 */
	private static int inceptionLevel = 0;

	public static void debug(String message) {
		LOGGER.debug(message);
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message) {
		LOGGER.warn(message);
	}

	public static void error(String message) {
		LOGGER.error(message);
	}

	public static void error(String message, Throwable e) {
		LOGGER.error(message, e);
	}

	/**
	 * Méthode de trace en début de glue<br>
	 * Si les glues sont désactivées, cette méthode retourne false et rajoute une
	 * trace 'DISABLED'
	 * 
	 * @param methodName
	 * @param params
	 * @return
	 */
	public static boolean methodStart(String methodName, String... params) {
		inceptionLevel++;
		traceMethod(methodName, false, "..", params);
		if (!AFStartup.areStepsEnabled()) {
			Trace.methodDisabled(methodName, params);
			return false;
		}
		return true;
	}

	public static void methodStop(String methodName, String... params) {
		traceMethod(methodName, true, "OK", params);
		inceptionLevel--;
	}

	public static void methodDisabled(String methodName, String... params) {
		traceMethod(methodName, true, "DISABLED", params);
		inceptionLevel--;
	}

	private static void traceMethod(String methodName, boolean debug, String status, String... params) {
		if (inceptionLevel < 0)
			inceptionLevel = 0;

		// Si on a passé un nom de méthode null, ne pas afficher la trace
		if (methodName == null)
			return;

		StringBuilder inceptionStr = new StringBuilder();
		for (int i = 1; i <= inceptionLevel; i++)
			inceptionStr.append(".");

		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String s : params) {
			sb.append(s + " | ");
		}
		sb.append(")");
		if (debug)
			LOGGER.debug(inceptionStr.toString() + " >> " + methodName + " " + sb.toString() + " : [" + status + "]");
		else
			LOGGER.info(inceptionStr.toString() + " >> " + methodName + " " + sb.toString() + " : [" + status + "]");
	}

	public static void resetInceptionLevel() {
		inceptionLevel = 0;
	}
}
