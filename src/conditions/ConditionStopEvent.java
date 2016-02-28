package conditions;

import commandes.CommandeEvent;

/**
 * V�rifier si le Lecteur de Map a fig� tous les Events.
 */
public class ConditionStopEvent extends Condition implements CommandeEvent {
	boolean valeurQuIlEstCenseAvoir;
	
	/**
	 * Constructeur explicite
	 * @param valeur attendue
	 */
	public ConditionStopEvent(final boolean valeur) {
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public final boolean estVerifiee() {
		return this.page.event.map.lecteur.stopEvent == valeurQuIlEstCenseAvoir;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}