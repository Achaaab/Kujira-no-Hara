package commandes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import main.Commande;
import map.LecteurMap;
import menu.Texte;
import utilitaire.GestionClavier;
import utilitaire.Graphismes;

/**
 * Afficher un Message dans une bo�te de dialogue
 */
public class Message extends Commande implements CommandeEvent {
	//constantes
	protected static final int MARGE_DU_TEXTE = 24;
	protected static final String NOM_IMAGE_BOITE_MESSAGE = ".\\ressources\\Graphics\\Pictures\\parchotexte.png";
	protected static final BufferedImage IMAGE_BOITE_MESSAGE = chargerImageDeFondDeLaBoiteMessage();
	
	public String texte;
	public BufferedImage image;
	public boolean leRelachementDeToucheAEuLieu = false;
	
	/**
	 * Constructeur explicite
	 * @param texte affich� dans la bo�te de dialogue
	 */
	public Message(final String texte) {
		this.texte = texte;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Message(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("texte") );
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.normaliserApparenceDesInterlocuteursAvantMessage(this.page.event);
		//si le Message � afficher est diff�rent du Message affich�, on change !
		if ( lecteur.messageActuel==null 
				|| !lecteur.messageActuel.texte.equals(this.texte) 
				|| siChoixLeCurseurATIlBouge()
		) {
			lecteur.messageActuel = this;
			this.image = produireImageDuMessage();			
		}
		//si la touche action est relach�e, la prochaine fois qu'elle sera press�e sera une nouvelle input
		if ( !lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION) ) {
			leRelachementDeToucheAEuLieu = true;
		}
		//et cette nouvelle input servira � fermer le message
		if (leRelachementDeToucheAEuLieu && lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION)) {
			//on ferme le message
			lecteur.messageActuel = null;
			//lecteur.stopEvent = false; //TODO � enlever, gestion via la condition parler
			leRelachementDeToucheAEuLieu = false;
			return redirectionSelonLeChoix(curseurActuel, commandes);
		} else {
			//on laisse le message ouvert
			return curseurActuel;
		}
	}

	/**
	 * Charge l'image de fond de la bo�te de dialogue.
	 * @return image de fond de la bo�te de dialogue
	 */
	private static BufferedImage chargerImageDeFondDeLaBoiteMessage() {
		try {
			final BufferedImage imageMessage = ImageIO.read(new File(NOM_IMAGE_BOITE_MESSAGE));
			return imageMessage;
		} catch (IOException e) {
			System.out.println("impossible d'ouvrir l'image");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Fabrique l'image du Message � partir de l'image de la bo�te de dialogue et du texte.
	 * M�thode d�riv�e par la classe Choix.
	 * @return image du Message
	 */
	protected BufferedImage produireImageDuMessage() {
		// Partir de la bo�te de dialogue vide
		BufferedImage imageMessage = Graphismes.clonerUneImage(IMAGE_BOITE_MESSAGE);
		
		// Ajout du texte
		final Texte t = new Texte(texte);
		imageMessage = Graphismes.superposerImages(imageMessage, t.image, MARGE_DU_TEXTE, MARGE_DU_TEXTE);
		return imageMessage;
	}
	
	/**
	 * Ce n'est pas un Choix, juste un Message Normal, donc le curseur n'a pas chang�.
	 * On n'a jamais besoin de remplacer l'image du Message.
	 * M�thode d�riv�e par la classe Choix.
	 * @return false
	 */
	protected boolean siChoixLeCurseurATIlBouge() {
		return false;
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
}
