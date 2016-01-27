package commandesEvent;

import map.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit impl�menter cette interface.
 */
public interface Mouvement {
	
	/**
	 * Proc�der aux modifications de donn�es permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * @param event qui se d�place
	 * @param deplacement dont fait partie ce mouvement
	 */
	void executerLeMouvement(final Event event, final Deplacement deplacement);
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @param event � d�placer
	 * @return true si le Mouvement est possible
	 */
	boolean mouvementPossible(Event event);
	
}
