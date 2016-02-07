package conditions;

import main.Fenetre;
import map.Event;
import map.Heros;
import map.Hitbox;

/**
 * V�rifier si le H�ros est arm�, et si l'Event se trouve dans la zone d'action de son Arme
 */
public class ConditionDansZoneDAttaque extends Condition {
	
	/**
	 * Constructeur vide
	 */
	public ConditionDansZoneDAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final boolean estCeQueLeHerosAUneArme = (Fenetre.getPartieActuelle().nombreDArmesPossedees > 0);
		if (estCeQueLeHerosAUneArme) {
			final Heros heros = this.page.event.map.heros;
			final Event event = this.page.event;
			final boolean reponse = Hitbox.estDansZoneDAttaque(event, heros);
			return reponse;
		} else {
			return false;
		}
	}
	
	/**
	 * C'est une Condition qui implique une proximit� avec le H�ros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}

}