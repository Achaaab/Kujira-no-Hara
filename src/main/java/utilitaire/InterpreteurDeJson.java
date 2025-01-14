package utilitaire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * Classe utilitaire pour transformer les fichiers JSON en objets JSON.
 */
public abstract class InterpreteurDeJson {
	private static final Logger LOG = LogManager.getLogger(InterpreteurDeJson.class);
	public static final String CARACTERES_INTERDITS = "[^a-zA-Z\\d\\s\\(\\):\\.-]"; // ^ signifie "tout sauf"
	
	/**
	 * Charger un objet JSON quelconque
	 * @param nomFichier nom du fichier JSON a charger
	 * @param adresse du fichier JSON a charger
	 * @param parPrefixe trouver le fichier uniquement a partir du debut de son nom
	 * @return objet JSON
	 * @throws JSONException Erreur de syntaxe dans le fichier JSON
	 * @throws IOException erreur d'ouverture de fichier
	 */
	public static JSONObject ouvrirJson(final String nomFichier, final String adresse, final boolean parPrefixe) throws JSONException, IOException {
		final String nomTotal;
		if (parPrefixe) {
			// fichiers commencant par
			final File dossier = new File(adresse);
			final File[] fichiersTrouves = dossier.listFiles(new FilenameFilter() {
			    public boolean accept(final File dir, final String name) {
			        return name.startsWith(nomFichier);
			    }
			});
			nomTotal = adresse+fichiersTrouves[0].getName();
		} else {
			// nom exact
			nomTotal = adresse+nomFichier+".json";
		}

		var classLoader = InterpreteurDeJson.class.getClassLoader();

		try (
				var inputStream = classLoader.getResourceAsStream(nomTotal);
				var reader = new InputStreamReader(requireNonNull(inputStream), UTF_8);
				var bufferedReader = new BufferedReader(reader)) {

			var json = bufferedReader.lines().collect(joining("\n"));
			return new JSONObject(json);

		} catch (JSONException e) {
			LOG.error("Erreur de syntaxe dans le fichier JSON "+nomTotal, e);
			throw e;
		} catch (UnsupportedEncodingException e1) {
			LOG.error("Erreur d'encodage du fichier JSON "+nomTotal, e1);
			throw e1;
		} catch (NullPointerException | IOException e2) {
			// Le fichier JSON n'existe pas
			throw new IOException("Le fichier "+nomTotal+" JSON n'existe pas.", e2);
		}
	}
	
	/**
	 * Charger un objet JSON quelconque (par son nom exact)
	 * @param nomFichier nom du fichier JSON a charger
	 * @param adresse du fichier JSON a charger
	 * @return objet JSON
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONObject ouvrirJson(final String nomFichier, final String adresse) throws Exception {
		return ouvrirJson(nomFichier, adresse, false);
	}
	
	/**
	 * Charger le parametrage d'une nouvelle Partie au format JSON.
	 * @return objet JSON contenant le parametrage d'une nouvelle Partie
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONObject ouvrirJsonNouvellePartie() throws Exception {
		return ouvrirJson("nouvellePartie", "Data/");
	}
	
	/**
	 * Charger la liste des Quetes du jeu au format JSON.
	 * @return objet JSON contenant la liste des Quetes du jeu
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONArray ouvrirJsonQuetes() throws Exception {
		final JSONObject jsonQuetes = ouvrirJson("quetes", "Data/");
		return jsonQuetes.getJSONArray("quetes");
	}
	
	/**
	 * Charger la liste des Objets du jeu au format JSON.
	 * @return objet JSON contenant la liste des Objets du jeu
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONArray ouvrirJsonObjets() throws Exception {
		final JSONObject jsonObjets = ouvrirJson("objets", "Data/");
		return jsonObjets.getJSONArray("objets");
	}
	
	/**
	 * Charger la liste des Armes du jeu au format JSON.
	 * @return objet JSON contenant la liste des Armes du jeu
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONArray ouvrirJsonArmes() throws Exception {
		final JSONObject jsonArmes = ouvrirJson("armes", "Data/");
		return jsonArmes.getJSONArray("armes");
	}
	
	/**
	 * Charger la liste des Gadgets du jeu au format JSON.
	 * @return objet JSON contenant la liste des Gadgets du jeu
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONArray ouvrirJsonGadgets() throws Exception {
		final JSONObject jsonGadgets = ouvrirJson("gadgets", "Data/");
		return jsonGadgets.getJSONArray("gadgets");
	}
	
	/**
	 * Charger la liste des Animations modeles du jeu au format JSON.
	 * @return objet JSON contenant la liste des Animations du jeu
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONArray ouvrirJsonAnimations() throws Exception {
		final JSONObject jsonArmes = ouvrirJson("animations", "Data/");
		return jsonArmes.getJSONArray("animations");
	}
	
	/**
	 * Charger une Map au format JSON.
	 * @param numero de la Map a charger
	 * @return objet JSON contenant la description de la Map
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONObject ouvrirJsonMap(final int numero) throws Exception {
		final String numeroA3Chiffres = String.format("%03d", numero);
		return ouvrirJson(numeroA3Chiffres, "Data/Maps/", false);
	}
	
	/**
	 * Charger un Tileset au format JSON.
	 * @param nom du Tileset a charger
	 * @return objet JSON contenant la description du Tileset
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONObject ouvrirJsonTileset(final String nom) throws Exception {
		return ouvrirJson(nom, "Data/Tilesets/");
	}

	/**
	 * Charger un Event generique au format JSON.
	 * Les Events generiques sont situes dans le dossier "ressources/Data/GenericEvents"
	 * @param nomEvent nom de l'Event generique
	 * @param artisanal est-ce que l'Event generique a ete ecrit a la main ?
	 * @return objet JSON contenant la description de l'Event
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONObject ouvrirJsonEventGenerique(final String nomEvent, final boolean artisanal) 
			throws Exception {
		final String sousDossier = artisanal ? "" : "Exportation/";
		final String nomEventSansLesZeros = nomEvent.replace("[00", "[").replace("[0", "[");
		return ouvrirJson(nomEventSansLesZeros, "Data/GenericEvents/"+sousDossier);
	}

	/**
	 * Charger les Zones d'attaque du jeu decrites dans un fichier JSON.
	 * @return zones d'attaque du jeu
	 * @throws Exception erreur dans la lecture du fichier JSON
	 */
	public static JSONArray ouvrirJsonZonesDAttaque() throws Exception {
		final JSONObject jsonZones = ouvrirJson("zonesDAttaque", "Data/");
		return jsonZones.getJSONArray("zonesDAttaque");
	}
	
}
