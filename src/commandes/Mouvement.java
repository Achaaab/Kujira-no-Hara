package commandes;

import map.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit impl�menter cette interface.
 */
public interface Mouvement {
	
	/**
	 * Proc�der aux modifications de donn�es permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * @param deplacement dont fait partie ce mouvement
	 */
	void executerLeMouvement(final Deplacement deplacement);
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @return true si le Mouvement est possible
	 */
	boolean mouvementPossible();
	
	/**
	 * Tout Mouvement d�place un Event.
	 * Cet Event doit �tre accessible par les classes qui impl�mentent Mouvement.
	 * @return l'Event qui va �tre d�plac�
	 */
	Event getEventADeplacer();
	
	/**
	 * Le Mouvement est-il termin� sur la Map ?
	 * Utile � savoir pour passer � la CommandeEvent suivante.
	 * @return true si le Mouvement est termin�
	 */
	boolean isTermine();
	
	/**
	 * Pr�venir le Mouvement qu'il a �t� termin� sur la Map.
	 */
	void setTermine();

}
