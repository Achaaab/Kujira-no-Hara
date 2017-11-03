package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.embed.swing.JFXPanel;
import jeu.Partie;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.LecteurMap;
import map.Map;
import map.Transition;
import menu.LecteurMenu;
import menu.Menu;
import net.bull.javamelody.Parameter;
import utilitaire.EmbeddedServer;
import utilitaire.GestionClavier;
import utilitaire.graphismes.Graphismes;

/**
 * La Fen�tre affiche l'�cran du jeu, mais a aussi un r�le de listener pour les entr�es clavier.
 */
@SuppressWarnings("serial")
public final class Fenetre extends JFrame implements KeyListener {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Fenetre.class);
	public static final int TAILLE_D_UN_CARREAU = 32;
	public static final int LARGEUR_ECRAN = 640;
	public static final int HAUTEUR_ECRAN = 480;
	private static final int PORT_JAVAMELODY = 8080;
	public static final int NOMBRE_DE_THREADS = 5;
	
	private static Fenetre maFenetre = null;
	public static String titre = "Le meilleur jeu du monde";
	public static int langue;

	private int margeGauche, margeHaut;
	public BufferStrategy bufferStrategy;
	public Lecteur lecteur = null;
	private Partie partie = null;
	public Lecteur futurLecteur = null;
	public boolean quitterLeJeu = false;
	
	public ArrayList<String> mesuresDePerformance = new ArrayList<String>();
	
	/**
	 * Constructeur explicite
	 */
	private Fenetre() {
		super(titre);
		final Menu menuTitre = Menu.creerMenuDepuisJson("Titre", null);
		
		//la s�lection initiale est ChargerPartie s'il y a d�j� une sauvegarde, sinon nouvellePartie
		int selectionInitiale = 0;
		try {
			final int nombreDeFichiersDeSauvegarde = (int) Files.list(Paths.get(".\\saves")).count();
			if (nombreDeFichiersDeSauvegarde > 0) {
				selectionInitiale = 1;
			}
		} catch (IOException e) {
			LOG.error("Impossible de compter les fichiers de sauvegarde !", e);
		}
		this.lecteur = new LecteurMenu(this, menuTitre, null, selectionInitiale);

		this.addKeyListener(this);
		
		// D�marrer JavaFX pour pouvoir ensuite lire des fichiers MP3
		@SuppressWarnings("unused")
		final JFXPanel fxPanel = new JFXPanel();
	}
	
	/**
	 * Obtenir � tout moment l'unique Fen�tre active (singleton).
	 * @return Fen�tre active
	 */
	public static Fenetre getFenetre() {
		if (maFenetre == null) {
			maFenetre = new Fenetre();
		}
		return maFenetre;
	}
	
	/**
	 * Calculer la taille des marges de la Fen�tre (variables selon l'Operating System).
	 * En v�rit� la Fen�tre est un peu plus grande que l'�cran : elle a des marges tout autour.
	 * @return marges � prendre en compte
	 */
	public static Insets obtenirLesMarges() {
		final JFrame fenetreBidon = new JFrame();
		fenetreBidon.setVisible(true);
		final Insets marges = fenetreBidon.getInsets();
		fenetreBidon.dispose();
		return marges;
	}
	
	/**
	 * Ouvrir la Fen�tre, afficher son titre et son ic�ne.
	 * Son contenu est encore vide pour l'instant, car l'affichage n'a pas d�marr�.
	 */
	public static void ouvrirFenetre() {
		final Fenetre fenetre = getFenetre();
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(final java.awt.event.WindowEvent windowEvent) {
		        fenetre.fermer(); //ex�cuter la m�thode fermer() lorsqu'on ferme la Fen�tre
		    }
		});
		final Insets marges = obtenirLesMarges();
		fenetre.margeGauche = marges.left;
		fenetre.margeHaut = marges.top;
		fenetre.setSize(marges.left+LARGEUR_ECRAN+marges.right, marges.top+HAUTEUR_ECRAN+marges.bottom);
		try {
			final ArrayList<Image> icones = new ArrayList<Image>();
			final BufferedImage iconePetite = Graphismes.ouvrirImage("Icons", "baleine icone.png");
			final BufferedImage iconeGrande = Graphismes.ouvrirImage("Icons", "baleine icone grand.png");
			icones.add(iconePetite);
			icones.add(iconeGrande);
			fenetre.setIconImages(icones);
		} catch (IOException e) {
			//probl�me avec les icones
			LOG.error("Probl�me avec les ic�nes", e);
		}
		fenetre.setResizable(false);
		fenetre.setVisible(true);
	}
	
	/**
	 * La Fen�tre confie l'affichage d'un Menu/Map � un Lecteur.
	 * Si jamais le Lecteur actuel est �teint et qu'un futur Lecteur est d�sign�, on effectue le remplacement.
	 * Si aucun futur Lecteur n'est d�sign�, la Fen�tre se ferme.
	 */
	public void demarrerAffichage() {
		// Acc�l�ration du calcul graphique
		System.setProperty("sun.java2d.opengl", "True");
		
		// Utiliser une BufferStrategy double
		this.createBufferStrategy(2);
		this.bufferStrategy = this.getBufferStrategy();
		
		while (!this.quitterLeJeu) {
			//on lance le Lecteur
			this.lecteur.demarrer(); //boucle tant que le Lecteur est allum�
			
			//si on est ici, c'est que le Lecteur a �t� �teint par une Commande Event
			//y en a-t-il un autre apr�s ?
			if (this.futurLecteur!=null) {
				if (!this.quitterLeJeu) {
					//on passe au Lecteur suivant
					this.lecteur = this.futurLecteur;
					this.futurLecteur = null;
				}
			} else {
				//pas de Lecteur � suivre
				//on �teint le jeu
				this.quitterLeJeu = true;
			}
		}
		//le jeu a �t� �teint ou bien il n'y a plus de Lecteur � suivre
	}
	
	/**
	 * Changer l'image affich�e dans la Fen�tre de jeu.
	 * @param image nouvelle image � afficher dans la fen�tre
	 */
	public void actualiserAffichage(final BufferedImage image) {
		//do {
			// S'assurer que le contenu du tampon graphique reste coh�rent
			//do {
				// Il faut un nouveau contexte graphique � chaque tour de boucle pour valider la strat�gie
				final Graphics graphics = this.bufferStrategy.getDrawGraphics();
				// Dessiner l'�cran de cette frame-ci
				graphics.drawImage(image, this.margeGauche, this.margeHaut, null);
				// Lib�rer le contexte graphique
				graphics.dispose();
				// R�p�ter le rendu si jamais le contenu du tampon est restaur�
			//} while (this.bufferStrategy.contentsRestored());
			// Afficher le tampon
			this.bufferStrategy.show();
			// R�p�ter le rendu si le tampon a �t� perdu
		//} while (this.bufferStrategy.contentsLost());
	}
	
	/**
	 * Point d'entr�e du programme
	 * @param args pas besoin d'arguments
	 */
	public static void main(final String[] args) {
		ouvrirFenetre();
		//lancerSupervisionJavaMelody();
		maFenetre.demarrerAffichage();
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

	@Override
	public void keyPressed(final KeyEvent event) {
		final Integer keycode = event.getKeyCode();
		//this.mesuresDePerformance.add("press;"+keycode+";"+System.currentTimeMillis());
		final GestionClavier.ToucheRole touchePressee = GestionClavier.ToucheRole.getToucheRole(keycode);
		if (touchePressee!=null && !touchePressee.touche.enfoncee) {
			touchePressee.touche.enfoncee = true;
			touchePressee.touche.frameDAppui = (Integer) this.lecteur.frameActuelle; // m�morisation de la frame d'appui
			
			this.lecteur.keyPressed(touchePressee);
		}
	}

	@Override
	public void keyReleased(final KeyEvent event) {
		final Integer keycode = event.getKeyCode();
		//this.mesuresDePerformance.add("release;"+keycode+";"+System.currentTimeMillis());
		final GestionClavier.ToucheRole toucheRelachee = GestionClavier.ToucheRole.getToucheRole(keycode);
		
		if (toucheRelachee!=null && toucheRelachee.touche.enfoncee) {
			toucheRelachee.touche.enfoncee = false;
			toucheRelachee.touche.frameDAppui = null;
			
			this.lecteur.keyReleased(toucheRelachee);
		}
	}

	@Override
	public void keyTyped(final KeyEvent event) {
		//rien
	}
	
	/**
	 * La Fen�tre a une partie s�lectionn�e, on l'ouvre.
	 */
	public void ouvrirLaPartie() {
		if (this.partie == null) {
			try {
				this.partie = Partie.creerNouvellePartie();
			} catch (Exception e) {
				LOG.error("Impossible de charger la partie.");
				e.printStackTrace();
			}
		}
		this.futurLecteur = new LecteurMap(this, Transition.AUCUNE);
		try {
			((LecteurMap) futurLecteur).map = new Map(
					this.partie.numeroMap, 
					(LecteurMap) this.futurLecteur, 
					null,
					this.partie.brouillardACharger,
					this.partie.xHeros, 
					this.partie.yHeros, 
					this.partie.directionHeros
			);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero "+partie.numeroMap);
			e.printStackTrace();
		}
		this.lecteur.allume = false; //TODO est-ce utile ?
	}
	
	/**
	 * Fermer la Fen�tre et quitter le jeu
	 */
	public void fermer() {
		exporterCsv();
		System.exit(0);
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
	
	/**
	 * Obtenir la Partie actuelle
	 * @return la Partie en cours
	 */
	public static Partie getPartieActuelle() {
		return maFenetre.partie;
	}
	
	/**
	 * M�moriser la Partie actuelle
	 * @param partieActuelle � faire m�moriser par la Fenetre
	 */
	public void setPartieActuelle(final Partie partieActuelle) {
		this.partie = partieActuelle;
	}

}
