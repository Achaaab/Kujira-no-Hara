package utilitaire;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONObject;

/**
 * Classe utilitaire pour transformer les fichiers JSON en objets JSON.
 */
public abstract class InterpreteurDeJson {
	/**
	 * Charger un objet JSON quelconque
	 * @param nomFichier nom du fichier JSON � charger
	 * @param adresse du fichier JSON � charger
	 * @return objet JSON
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	private static JSONObject ouvrirJson(final String nomFichier, final String adresse) throws FileNotFoundException {
		String nomFichierJson = adresse+nomFichier+".json";
		Scanner scanner = new Scanner(new File(nomFichierJson));
		String contenuFichierJson = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return new JSONObject(contenuFichierJson);
	}
	
	/**
	 * Charger une Map au format JSON.
	 * @param numero de la Map � charger
	 * @return objet JSON contenant la description de la Map
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonMap(final int numero) throws FileNotFoundException {
		return ouvrirJson(""+numero, ".\\ressources\\Data\\Maps\\");
	}

	/**
	 * Charger un Event g�n�rique au format JSON.
	 * Les Events g�n�riques sont situ�s dans le dossier "ressources/Data/GenericEvents"
	 * @param nomEvent nom de l'Event g�n�rique
	 * @return objet JSON contenant la description de l'Event
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonEventGenerique(final String nomEvent) throws FileNotFoundException {
		return ouvrirJson(nomEvent, ".\\ressources\\Data\\GenericEvents\\");
	}
	
}
