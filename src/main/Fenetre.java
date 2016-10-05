package main;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;
import jeu.Partie;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import map.LecteurMap;
import map.Map;
import menu.LecteurMenu;
import menu.Menu;
import utilitaire.GestionClavier;
import utilitaire.InterpreteurDeJson;

/**
 * La Fen�tre affiche l'�cran du jeu, mais a aussi un r�le de listener pour les entr�es clavier.
 */
@SuppressWarnings("serial")
public final class Fenetre extends JFrame implements KeyListener {
	//constantes
	public static final int TAILLE_D_UN_CARREAU = 32;
	public static final int LARGEUR_ECRAN = 640;
	public static final int HAUTEUR_ECRAN = 480;
	
	private static Fenetre maFenetre = null;
	public static String titre = "Le meilleur jeu du monde";

	public JLabel labelEcran = null;
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
		this.labelEcran = new JLabel();
		final Menu menuTitre = InterpreteurDeJson.creerMenuDepuisJson("Titre", null); //new MenuTitre();
		this.lecteur = new LecteurMenu(this, menuTitre, null);

		this.addKeyListener(this);
		
		//d�marrer JavaFX pour pouvoir ensuite lire des fichiers MP3
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
		final int margeHorizontale = marges.left+marges.right;
		final int margeVerticale = marges.top+marges.bottom;
		fenetre.setSize(LARGEUR_ECRAN+margeHorizontale, HAUTEUR_ECRAN+margeVerticale);
		try {
			final ArrayList<Image> icones = new ArrayList<Image>();
			final BufferedImage iconePetite = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\baleine icone.png"));
			final BufferedImage iconeGrande = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\baleine icone grand.png"));
			icones.add(iconePetite);
			icones.add(iconeGrande);
			fenetre.setIconImages(icones);
		} catch (IOException e) {
			//probl�me avec les icones
			e.printStackTrace();
		}
		fenetre.setResizable(false);
		fenetre.setVisible(true);
	}
	
	/**
	 * La Fen�tre confie l'affichage d'un Menu/Map � un Lecteur.
	 * Si jamais le Lecteur actuel est �teint et qu'un futur Lecteur est d�sign�, on effectue le remplacement.
	 * Si aucun futur Lecteur n'est d�sign�, la Fen�tre se ferme.
	 */
	public static void demarrerAffichage() {
		while (!maFenetre.quitterLeJeu) {
			//on lance le Lecteur
			maFenetre.lecteur.demarrer(); //boucle tant que le Lecteur est allum�
			
			//si on est ici, c'est que le Lecteur a �t� �teint par une Commande Event
			//y en a-t-il un autre apr�s ?
			if (maFenetre.futurLecteur!=null) {
				if (!maFenetre.quitterLeJeu) {
					//on passe au Lecteur suivant
					maFenetre.lecteur = maFenetre.futurLecteur;
					maFenetre.futurLecteur = null;
				}
			} else {
				//pas de Lecteur � suivre
				//on �teint le jeu
				maFenetre.quitterLeJeu = true;
			}
		}
		//le jeu a �t� �teint ou bien il n'y a plus de Lecteur � suivre
	}
	
	/**
	 * Changer l'image affich�e dans la Fen�tre de jeu.
	 * @param image nouvelle image � afficher dans la fen�tre
	 */
	public void actualiserAffichage(final Image image) {
		this.invalidate();
		this.remove(this.labelEcran);
		this.labelEcran = new JLabel(new ImageIcon(image));
		this.add(this.labelEcran);
		this.revalidate();
	}
	
	/**
	 * Point d'entr�e du programme
	 * @param args pas besoin d'arguments
	 */
	public static void main(final String[] args) {
		ouvrirFenetre();
		demarrerAffichage();
	}

	
	@Override
	public void keyPressed(final KeyEvent event) {
		final Integer keycode = event.getKeyCode();
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
			} catch (FileNotFoundException e) {
				System.err.println("Impossible de charger la partie.");
				e.printStackTrace();
			}
		}
		this.futurLecteur = new LecteurMap(this);
		try {
			((LecteurMap) futurLecteur).map = new Map(this.partie.numeroMap, (LecteurMap) this.futurLecteur, this.partie.xHeros, this.partie.yHeros, this.partie.directionHeros);
		} catch (FileNotFoundException e) {
			System.err.println("Impossible de charger la map numero "+partie.numeroMap);
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
		final Path file = Paths.get("C:/Users/RoiOfTheSuisse/Documents/kujira-perf2.csv");
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
