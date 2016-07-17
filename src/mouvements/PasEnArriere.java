package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;

/**
 * D�placer un Event d'un pas dans la direction oppos�e � la direction de l'Event.
 */
public class PasEnArriere extends Avancer {
	
	/**
	 * Constructeur explicite
	 */
	public PasEnArriere() {
		//le -1 est bidon, il sera remplac� par la direction de l'Event lors de la v�rification
		super(-1, Fenetre.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public PasEnArriere(final HashMap<String, Object> parametres) {
		this();
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		//on assigne la vraie direction de l'Event
		final Event event = this.deplacement.getEventADeplacer();
		this.direction = event.direction;
		
		//puis on lance la v�rification traditionnelle
		return super.mouvementPossible();
	}

	@Override
	public int getDirectionImposee() {
		prendreDirectionOpposee();
		return this.direction;
	}

}
