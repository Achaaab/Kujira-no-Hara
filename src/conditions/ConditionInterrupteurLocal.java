package conditions;

import map.Event;

public class ConditionInterrupteurLocal extends Condition{
	int numeroInterrupteur;
	boolean valeurQuIlEstCenseAvoir;
	Event eventConcerne;
	
	/**
	 * @param lettre 0 A ; 1 B ; 2 C ; 3 D
	 * @param event concern�
	 * @param valeur bool�enne attendue
	 */
	public ConditionInterrupteurLocal(int lettre, Event event, boolean valeur){
		this.numeroInterrupteur = lettre;
		this.eventConcerne = event;
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public Boolean estVerifiee() {
		switch(numeroInterrupteur){
			case 0 : return eventConcerne.interrupteurLocalA==valeurQuIlEstCenseAvoir;
			case 1 : return eventConcerne.interrupteurLocalB==valeurQuIlEstCenseAvoir;
			case 2 : return eventConcerne.interrupteurLocalC==valeurQuIlEstCenseAvoir;
			default : return eventConcerne.interrupteurLocalD==valeurQuIlEstCenseAvoir;
		}
	}

}