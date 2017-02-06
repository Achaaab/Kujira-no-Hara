package commandes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.LecteurMap;
import menu.Texte;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Afficher un Message dans une bo�te de dialogue
 */
public class Message extends Commande implements CommandeEvent {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Message.class);
	protected static final int MARGE_DU_TEXTE = 24;
	protected static final String NOM_IMAGE_BOITE_MESSAGE = "parchotexte.png";
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_PLEINE = chargerImageDeFondDeLaBoiteMessage();
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_VIDE = Graphismes.creerUneImageVideDeMemeTaille(IMAGE_BOITE_MESSAGE_PLEINE);
	protected static BufferedImage imageBoiteMessage = IMAGE_BOITE_MESSAGE_PLEINE;
	
	public String[] texte;
	public BufferedImage image;
	private boolean touchePresseePourQuitterLeMessage = false;
	private boolean premiereFrameDAffichageDuMessage = true;
	
	/**
	 * Constructeur explicite
	 * @param texte affich� dans la bo�te de dialogue
	 */
	public Message(final String[] texte) {
		this.texte = texte;
		this.touchePresseePourQuitterLeMessage = false;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Message(final HashMap<String, Object> parametres) {
		this( InterpreteurDeJson.construireTexteMultilingue(parametres.get("texte")) );
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.normaliserApparenceDesInterlocuteursAvantMessage(this.page.event);
		//si le Message � afficher est diff�rent du Message affich�, on change !
		if (ilFautReactualiserLImageDuMessage(lecteur)) {
			lecteur.messageActuel = this;
			this.image = produireImageDuMessage();
		}
		
		//fermer le message
		if (this.touchePresseePourQuitterLeMessage) {
			//on ferme le message
			lecteur.messageActuel = null;
			this.touchePresseePourQuitterLeMessage = false;
			this.premiereFrameDAffichageDuMessage = true;
			return redirectionSelonLeChoix(curseurActuel, commandes);
		} else {
			//on laisse le message ouvert
			this.premiereFrameDAffichageDuMessage = false;
			return curseurActuel;
		}
	}

	/**
	 * Faut-il r�actualiser l'image du Message ?
	 * @param lecteur de map
	 * @return true s'il faut construire une nouvelle image de Message, false sinon
	 */
	protected boolean ilFautReactualiserLImageDuMessage(LecteurMap lecteur) {
		return lecteur.messageActuel==null //le dialogue vient de commencer
				|| !lecteur.messageActuel.texte.equals(this.texte) //le texte a chang� mais pas encore l'image
				|| this.premiereFrameDAffichageDuMessage; //TODO utile ?
	}

	/**
	 * Charge l'image de fond de la bo�te de dialogue.
	 * @return image de fond de la bo�te de dialogue
	 */
	private static BufferedImage chargerImageDeFondDeLaBoiteMessage() {
		try {
			return Graphismes.ouvrirImage("Pictures", NOM_IMAGE_BOITE_MESSAGE);
		} catch (IOException e) {
			LOG.error("impossible d'ouvrir l'image "+NOM_IMAGE_BOITE_MESSAGE, e);
			return null;
		}
	}
	
	/**
	 * Fabrique l'image du Message � partir de l'image de la bo�te de dialogue et du texte.
	 * M�thode d�riv�e par la classe Choix.
	 * @return image du Message
	 */
	protected BufferedImage produireImageDuMessage() {
		// Partir de la bo�te de dialogue
		BufferedImage imageMessage = Graphismes.clonerUneImage(Message.imageBoiteMessage);
		
		// Ajout du texte
		final Texte t = new Texte( this.texte[Fenetre.getPartieActuelle().langue] );
		imageMessage = Graphismes.superposerImages(imageMessage, t.image, MARGE_DU_TEXTE, MARGE_DU_TEXTE);
		return imageMessage;
	}
	
	/**
	 * Ce n'est pas un Choix, juste un Message Normal, donc la Commande suivante est juste apr�s.
	 * M�thode d�riv�e par la classe Choix.
	 * @param curseurActuel
	 * @return curseur incr�ment�
	 */
	protected int redirectionSelonLeChoix(final int curseurActuel, final ArrayList<Commande> commandes) {
		return curseurActuel+1;
	}
	
	protected final int calculerHauteurTexte() {
		final int nombreDeLignesDuTexte;
		if (this.texte == null || this.texte.equals("")) {
			nombreDeLignesDuTexte = 0; 
		} else {
			nombreDeLignesDuTexte = 1 + StringUtils.countMatches(this.texte[Fenetre.getPartieActuelle().langue], "\n");
		}
		final int hauteurLigne = Texte.TAILLE_MOYENNE + Texte.INTERLIGNE;
		final int hauteurTexte = nombreDeLignesDuTexte * hauteurLigne;
		return hauteurTexte;
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	public void haut() {
		// rien
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	public void bas() {
		// rien
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	public void gauche() {
		// rien
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	public void droite() {
		// rien
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	public void action() {
		this.touchePresseePourQuitterLeMessage = true;
	}
}
