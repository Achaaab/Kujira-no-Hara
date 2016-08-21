package commandes;

import java.util.ArrayList;

import main.Commande;

/**
 * Activer ou d�sactiver l'animation � l'arr�t d'un Event.
 * La modification se fait sur la propri�t� actuelle de l'Event, et non sur la Page.
 */
public class RendreAnimeALArret extends Commande implements CommandeEvent {
	private boolean valeur;
	
	/**
	 * Constructeur explicite
	 * @param valeur � affecter
	 */
	public RendreAnimeALArret(final boolean valeur) {
		this.valeur = valeur;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.page.event.animeALArretActuel = this.valeur;
		return curseurActuel+1;
	}
	
}
