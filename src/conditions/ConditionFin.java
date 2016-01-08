package conditions;

/**
 * Balise "fin" de la Condition situ�e parmi les Commandes Event.
 */
public class ConditionFin extends Condition {

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionFin(final int numero) {
		this.numero = numero;
	}
	
	@Override
	public final Boolean estVerifiee() {
		return true;
	}

}
