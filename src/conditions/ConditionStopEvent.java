package conditions;

/**
 * V�rifier si le Lecteur de Map a fig� tous les Events 
 */
public class ConditionStopEvent extends Condition {
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
		return page.event.map.lecteur.stopEvent == valeurQuIlEstCenseAvoir;
	}

}