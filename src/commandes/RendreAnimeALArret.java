package commandes;

import java.util.ArrayList;

import main.Commande;
import map.PageEvent;

/**
 * Activer ou d�sactiver l'animation � l'arr�t d'un Event.
 * La modification se fait sur la propri�t� actuelle de l'Event, et non sur la Page.
 */
public class RendreAnimeALArret implements CommandeEvent {
	private PageEvent page;
	
	private boolean valeur;
	
	/**
	 * Constructeur explicite
	 * @param valeur � affecter
	 */
	public RendreAnimeALArret(final boolean valeur) {
		this.valeur = valeur;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		this.getPage().event.animeALArretActuel = this.valeur;
		return curseurActuel+1;
	}

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}
	
}
