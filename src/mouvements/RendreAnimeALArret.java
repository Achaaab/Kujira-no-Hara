package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Rendre un Event anim� � l'arr�t.
 */
public class RendreAnimeALArret extends Mouvement {
	private boolean nouveauAnimeALArret;
	
	/**
	 * Constructeur explicite
	 * @param nouveauAnimeALArret � donner � l'Event
	 */
	public RendreAnimeALArret(final boolean nouveauAnimeALArret) {
		this.nouveauAnimeALArret = nouveauAnimeALArret;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public RendreAnimeALArret(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("animeALArret") ? (boolean) parametres.get("animeALArret") : true );
	}
	
	@Override
	protected void reinitialiserSpecifique() {
		//rien
	}

	@Override
	public final boolean mouvementPossible() {
		//toujours possible
		return true;
	}

	@Override
	protected final void calculDuMouvement(final Event event) {
		event.animeALArretActuel = this.nouveauAnimeALArret;
	}

	@Override
	protected void terminerLeMouvementSpecifique(final Event event) {
		//rien
	}

	@Override
	protected void ignorerLeMouvementSpecifique(final Event event) {
		//rien
	}

	@Override
	public final String toString() {
		return "anim� � l'arr�t : "+this.nouveauAnimeALArret;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
