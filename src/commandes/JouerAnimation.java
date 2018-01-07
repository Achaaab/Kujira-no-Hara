package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;

/**
 * Une Animation peut cibler un Event, ou bien �tre positionn�e en des coordonn�es sp�cifiques.
 */
public class JouerAnimation extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(JouerAnimation.class);
	
	public final int idAnimation;
	public Integer idEvent;
	public int xEcran, yEcran;
	
	public int frameActuelle;
	
	/**
	 * Constructeur explicite
	 * @param idAnimation identifiant de l'Animation � jouer
	 * @param idEvent identifiant de l'Event sur lequel on affiche l'Animation ; "null" vaut pour "cet Event"
	 * @param xEcran position x de la Map o� on affiche l'animation (facultatif si Event)
	 * @param yEcran position y de la Map o� on affiche l'animation (facultatif si Event)
	 */
	private JouerAnimation(final int idAnimation, final Integer idEvent, final int xEcran, final int yEcran) {
		this.idAnimation = idAnimation;
		this.idEvent = idEvent;
		this.xEcran = xEcran;
		this.yEcran = yEcran;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public JouerAnimation(final HashMap<String, Object> parametres) {
		this( 	(int) parametres.get("idAnimation"),
				parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null, //soit on utilise l'idEvent
				parametres.containsKey("xEcran") ? (int) parametres.get("xEcran") : -1, //soit on utilise des coordonn�es
				parametres.containsKey("yEcran") ? (int) parametres.get("yEcran") : -1
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.frameActuelle = 0;
		if (this.xEcran == -1 && this.yEcran == -1) {
			// L'animation concerne un Event
			
			// "null" signifie donc "cet Event"
			if (this.idEvent == null) {
				this.idEvent = this.page.event.id;
			}
			
			// On v�rifie qu'il n'y a pas d�j� une animation sur cet Event
			int nombreDAnimations = Fenetre.getPartieActuelle().animations.size();
			JouerAnimation animation;
			for (int i = 0; i<nombreDAnimations; i++) {
				animation = Fenetre.getPartieActuelle().animations.get(i);
				// Est-ce qu'il y a d�j� une animation sur l'Event concern� ?
				if (this.idEvent == animation.idEvent) {
					// On retire les animations actuellement associ�es � cet Event
					Fenetre.getPartieActuelle().animations.remove(i);
					i--;
					nombreDAnimations--;
				}
			}
		} else {
			// L'animation est d�finie par ses coordonn�es
			this.idEvent = null;
		}
		
		// On ajoute la nouvelle animation
		Fenetre.getPartieActuelle().animations.add(this);
		LOG.debug("Animation "+this.idAnimation+" ajout�e � la file");
		return curseurActuel+1;
	}

}
