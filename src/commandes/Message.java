package commandes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import main.Commande;
import map.LecteurMap;
import map.PageEvent;
import menu.Texte;
import utilitaire.GestionClavier;

/**
 * Afficher un Message dans une bo�te de dialogue
 */
public class Message implements CommandeEvent {
	PageEvent page;
	
	//constantes
	private static final int MARGE_DU_TEXTE = 24;
	
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
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		final LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.normaliserApparenceDuHerosAvantMessage();
		//si le message � afficher est diff�rent du message affich�, on change !
		if ( lecteur.messageActuel==null || !lecteur.messageActuel.equals(texte) ) {
			lecteur.messageActuel = this;
			try {
				BufferedImage imageMessage = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\parchotexte.png"));
				final Texte t = new Texte(texte);
				imageMessage = lecteur.superposerImages(imageMessage, t.texteToImage(), MARGE_DU_TEXTE, MARGE_DU_TEXTE);
				this.image = imageMessage;
				//lecteur.stopEvent = true; //TODO � enlever, gestion via la condition parler
			} catch (IOException e) {
				System.out.println("impossible d'ouvrir l'image");
				e.printStackTrace();
			}
			
		}
		//si la touche action est relach�e, la prochaine fois qu'elle sera press� sera une nouvelle input
		if ( !lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION) ) {
			leRelachementDeToucheAEuLieu = true;
		}
		//et cette nouvelle input servira � fermer le message
		if (leRelachementDeToucheAEuLieu && lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION)) {
			//on ferme le message
			lecteur.messageActuel = null;
			//lecteur.stopEvent = false; //TODO � enlever, gestion via la condition parler
			leRelachementDeToucheAEuLieu = false;
			return curseurActuel+1;
		} else {
			//on laisse le message ouvert
			return curseurActuel;
		}
	}
	
	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
