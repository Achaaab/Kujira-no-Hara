package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import map.LecteurMap;
import map.Map;
import map.Transition;
import menu.LecteurMenu;
import menu.Menu;
import net.bull.javamelody.Parameter;
import utilitaire.EmbeddedServer;

public class Main {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Main.class);
	private static final int PORT_JAVAMELODY = 8080;
	public static final int NOMBRE_DE_THREADS = 5;
	public static final int TAILLE_D_UN_CARREAU = 32;
	
	public static Fenetre fenetre = null;
	public static Lecteur lecteur = null;
	public static Lecteur futurLecteur = null;
	public static boolean quitterLeJeu = false;
	public static int langue;
	private static Partie partie = null;

	
	
	public static ArrayList<String> mesuresDePerformance = new ArrayList<String>();
	
	/**
	 * Point d'entr�e du programme
	 * @param args pas besoin d'arguments
	 */
	public static void main(final String[] args) {
		// La premi�re Fen�tre n'est pas en plein �cran
		fenetre = new Fenetre(false);
		
		// Le premier Lecteur est celui du Menu titre
		final Menu menuTitre = Menu.creerMenuDepuisJson("Titre", null);
		//la s�lection initiale est "chargerPartie" s'il y a d�j� une sauvegarde, sinon "nouvellePartie"
		int selectionInitiale = 0;
		try {
			final int nombreDeFichiersDeSauvegarde = (int) Files.list(Paths.get(".\\saves")).count();
			if (nombreDeFichiersDeSauvegarde > 0) {
				selectionInitiale = 1;
			}
		} catch (IOException e) {
			LOG.error("Impossible de compter les fichiers de sauvegarde !", e);
		}
		lecteur = new LecteurMenu(menuTitre, null, selectionInitiale);
		
		// Mesurer les performances
		lancerSupervisionJavaMelody();
		
		// Acc�l�ration du calcul graphique
		System.setProperty("sun.java2d.opengl", "True");
		
		//
		fenetre.demarrerAffichage();
	}
	
	/**
	 * La Fen�tre a une partie s�lectionn�e, on l'ouvre.
	 */
	public static void ouvrirLaPartie() {
		if (partie == null) {
			try {
				partie = Partie.creerNouvellePartie();
			} catch (Exception e) {
				LOG.error("Impossible de charger la partie.");
				e.printStackTrace();
			}
		}
		futurLecteur = new LecteurMap(Transition.AUCUNE);
		try {
			((LecteurMap) futurLecteur).map = new Map(
					partie.numeroMap, 
					(LecteurMap) futurLecteur, 
					null,
					partie.brouillardACharger,
					partie.xHeros, 
					partie.yHeros, 
					partie.directionHeros,
					0, // pas de d�calage car ce n'est pas un changement de Map
					0  // pas de d�calage car ce n'est pas un changement de Map
			);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero "+partie.numeroMap);
			e.printStackTrace();
		}
		lecteur.allume = false; //TODO est-ce utile ?
	}
	
	/**
	 * Obtenir la Partie actuelle
	 * @return la Partie en cours
	 */
	public static Partie getPartieActuelle() {
		return partie;
	}
	
	/**
	 * M�moriser la Partie actuelle
	 * @param partieActuelle � faire m�moriser par la Fenetre
	 */
	public static void setPartieActuelle(final Partie partieActuelle) {
		partie = partieActuelle;
	}

	/**
	 * Superviser les performances avec JavaMelody.
	 */
	private static void lancerSupervisionJavaMelody() {
		final HashMap<Parameter, String> parametresJavaMelody = new HashMap<>();
		// Authentification
		parametresJavaMelody.put(Parameter.AUTHORIZED_USERS, "admin:password");
		// Dossier d'enregistrement
		parametresJavaMelody.put(Parameter.STORAGE_DIRECTORY, "C://Users/Public/tmp/javamelody");
		// Fr�quence d'�chantillonnage
		parametresJavaMelody.put(Parameter.SAMPLING_SECONDS, "1.0");
		// Emplacement des rapports d'analyse
		parametresJavaMelody.put(Parameter.MONITORING_PATH, "/");
		try {
			// D�marrer le server d'affichage de l'analyse JavaMelody
			LOG.info("D�marrage de JavaMelody...");
			EmbeddedServer.start(PORT_JAVAMELODY, parametresJavaMelody);
			LOG.info("JavaMelody est d�marr�.");
		} catch (Exception e) {
			LOG.error("Impossible de lancer l'analyse de performance avec JavaMelody.", e);
		}
	}
	
	/**
	 * Exporter les mesures de performances en tant que fichier CSV.
	 */
	private void exporterCsv() {
		final Path file = Paths.get("C:/Users/Public/kujira-perf2.csv");
		try {
			Files.write(file, this.mesuresDePerformance, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
