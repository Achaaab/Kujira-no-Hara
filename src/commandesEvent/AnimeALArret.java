package commandesEvent;

import java.util.ArrayList;

/**
 * Activer ou d�sactiver l'animation � l'arr�t d'un Event.
 * La modification se fait sur la propri�t� actuelle de l'Event, et non sur la Page.
 */
public class AnimeALArret extends CommandeEvent {
	private boolean valeur;
	
	/**
	 * Constructeur explicite
	 * @param valeur � affecter
	 */
	public AnimeALArret(final boolean valeur) {
		this.valeur = valeur;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		this.page.event.animeALArretActuel = this.valeur;
		return curseurActuel+1;
	}

}
