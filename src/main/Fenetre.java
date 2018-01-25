package main;

import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;
import main.capteurs.CapteurClavier;
import main.capteurs.CapteurFenetre;
import main.capteurs.CapteurSouris;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilitaire.graphismes.Graphismes;

/**
 * La Fen�tre affiche l'�cran du jeu, mais a aussi un r�le de listener pour les entr�es clavier.
 */
@SuppressWarnings("serial")
public final class Fenetre extends JFrame {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Fenetre.class);
	public static final int LARGEUR_ECRAN = 640;
	public static final int HAUTEUR_ECRAN = 480;
	private static int largeurPleinEcran;
	private static int hauteurPleinEcran;
	private static final String titre = "Le meilleur jeu du monde";

	private final boolean pleinEcran;
	private int margeGauche, margeHaut;
	public BufferStrategy bufferStrategy;
	private Graphics bufferStrategyGraphics;
	private GraphicsDevice device;
	
	/**
	 * Constructeur explicite
	 */
	Fenetre(final boolean pleinEcran) {
		super(titre);
		this.pleinEcran = pleinEcran;

		this.addKeyListener(new CapteurClavier(this)); //r�cup�rer les entr�es Clavier
		this.addWindowFocusListener(new CapteurFenetre(this)); //pauser le jeu si Fenetre inactive
		this.addMouseListener(new CapteurSouris(this)); //plein �cran si double-clic
		this.device = this.getGraphicsConfiguration().getDevice();
		
		// D�marrer JavaFX pour pouvoir ensuite lire des fichiers MP3
		@SuppressWarnings("unused")
		final JFXPanel fxPanel = new JFXPanel();
		
		//-------------------------------------------------------------------------------
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Insets marges = obtenirLesMarges();
		this.margeGauche = marges.left;
		this.margeHaut = marges.top;
		this.setSize(marges.left+LARGEUR_ECRAN+marges.right, marges.top+HAUTEUR_ECRAN+marges.bottom);
		try {
			final ArrayList<Image> icones = new ArrayList<Image>();
			final BufferedImage iconePetite = Graphismes.ouvrirImage("Icons", "baleine icone.png");
			final BufferedImage iconeGrande = Graphismes.ouvrirImage("Icons", "baleine icone grand.png");
			icones.add(iconePetite);
			icones.add(iconeGrande);
			this.setIconImages(icones);
		} catch (IOException e) {
			//probl�me avec les icones
			LOG.error("Probl�me avec les ic�nes", e);
		}
		this.setResizable(false);
		this.setVisible(true);
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
	 * La Fen�tre confie l'affichage d'un Menu/Map � un Lecteur.
	 * Si jamais le Lecteur actuel est �teint et qu'un futur Lecteur est d�sign�, on effectue le remplacement.
	 * Si aucun futur Lecteur n'est d�sign�, la Fen�tre se ferme.
	 */
	public void demarrerAffichage() {
		// Utiliser une BufferStrategy double
		this.createBufferStrategy(2);
		this.bufferStrategy = this.getBufferStrategy();
		this.bufferStrategyGraphics = this.bufferStrategy.getDrawGraphics();
		
		while (!Main.quitterLeJeu) {
			//on lance le Lecteur
			Main.lecteur.demarrer(); //boucle tant que le Lecteur est allum�
			
			//si on est ici, c'est que le Lecteur a �t� �teint par une Commande Event
			//y en a-t-il un autre apr�s ?
			if (Main.futurLecteur != null) {
				if (!Main.quitterLeJeu) {
					//on passe au Lecteur suivant
					Main.lecteur = Main.futurLecteur;
					Main.futurLecteur = null;
				}
			} else {
				//pas de Lecteur � suivre
				//on �teint le jeu
				Main.quitterLeJeu = true;
			}
		}
		//le jeu a �t� �teint ou bien il n'y a plus de Lecteur � suivre
		
		// On ferme la Fen�tre
		this.bufferStrategyGraphics.dispose();
		this.bufferStrategy.dispose();
		this.dispose();
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
				//final Graphics graphics = this.bufferStrategy.getDrawGraphics();
				final BufferedImage imageAgrandie;
				if (this.device.getFullScreenWindow() != null) {
					imageAgrandie = Graphismes.redimensionner(image, getLargeurPleinEcran(), getHauteurPleinEcran());
				} else {
					imageAgrandie = image;
				}
				// Dessiner l'�cran de cette frame-ci
				bufferStrategyGraphics.drawImage(imageAgrandie, this.margeGauche, this.margeHaut, null);
				// Lib�rer le contexte graphique
				//graphics.dispose();
				// R�p�ter le rendu si jamais le contenu du tampon est restaur�
			//} while (this.bufferStrategy.contentsRestored());
			// Afficher le tampon
			this.bufferStrategy.show();
			// R�p�ter le rendu si le tampon a �t� perdu
		//} while (this.bufferStrategy.contentsLost());
	}

	/**
	 * Entrer ou quitter le mode plein �cran.
	 */
	public void pleinEcran() {
		//TODO fermer la Fen�tre et en utiliser une nouvelle
		if (this.device.isFullScreenSupported()) {
			// Est-on d�j� en mode plein �cran ?
			if (this.pleinEcran) {
				// On quitte le mode plein �cran
				this.device.setFullScreenWindow(null);
				this.setUndecorated(false);
			} else {
				// On entre en mode plein �cran
				this.device.setFullScreenWindow(this);
				this.setUndecorated(true);
			}
		}
	}
	
	/**
	 * Calculer la largeur et la hauteur de l'�cran en mode plein �cran.
	 * @return largeur (en pixels)
	 */
	private int getLargeurPleinEcran() {
		if (largeurPleinEcran <= 0) {
			final Rectangle bounds = getGraphicsConfiguration().getBounds();
			largeurPleinEcran = (int) (bounds.getMaxX() - bounds.getMinX());
			hauteurPleinEcran = (int) (bounds.getMaxY() - bounds.getMinY());
		}
		return largeurPleinEcran;
	}
	
	/**
	 * Obtenir la hauteur de l'�cran en mode plein �cran.
	 * @return hauteur (en pixels)
	 */
	private int getHauteurPleinEcran() {
		return hauteurPleinEcran;
	}

}
