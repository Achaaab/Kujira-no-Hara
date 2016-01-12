package conditions;

import map.Event;

/**
 * Condition pour v�rifier la valeur d'un interrupteur local d'un Event
 */
public class ConditionInterrupteurLocal extends Condition {
	int numeroInterrupteur;
	boolean valeurQuIlEstCenseAvoir;
	Event eventConcerne;
	
	/**
	 * Constructeur explicite
	 * @param lettre 0 A ; 1 B ; 2 C ; 3 D
	 * @param event concern�
	 * @param valeur bool�enne attendue
	 */
	public ConditionInterrupteurLocal(final int lettre, final Event event, final boolean valeur) {
		this.numeroInterrupteur = lettre;
		this.eventConcerne = event;
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public final boolean estVerifiee() {
		if (numeroInterrupteur>=0 && numeroInterrupteur<Event.NOMBRE_INTERRUPTEURS_LOCAUX) {
			return eventConcerne.interrupteursLocaux[numeroInterrupteur]==valeurQuIlEstCenseAvoir;
		}
		System.err.println("L'interrupteur local num�ro "+numeroInterrupteur+" n'existe pas !");
		return false;
	}

}