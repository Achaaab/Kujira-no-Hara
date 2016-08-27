package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Fait regarder l'event dans une direction donn�e.
 */
public class RegarderDansUneDirection extends Mouvement {
	
	protected int direction;

	/**
	 * Constructeur explicite
	 * @param direction dans laquelle l'Event doit regarder
	 */
	public RegarderDansUneDirection(final int direction) {
		this.direction = direction;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public RegarderDansUneDirection(final HashMap<String, Object> parametres) {
		this((int) parametres.get("direction"));
	}
	

	@Override
	protected void reinitialiserSpecifique() {
		// rien
	}

	@Override
	public final boolean mouvementPossible() {
		// Regarder dans une direction est toujours possible.
		return true;
	}

	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * @param event subissant le Mouvement
	 */
	@Override
	protected final void calculDuMouvement(final Event event) {
		//rien car le travail est fait par la m�thode getDirectionImposee()
	}

	@Override
	protected void terminerLeMouvementSpecifique(final Event event) {
		// Mouvement non sp�cifique	
	}

	@Override
	protected void ignorerLeMouvementSpecifique(final Event event) {
		// Mouvement non sp�cifique
	}
	
	/**
	 * Cette m�thode est d�riv�e dans la classe RegarderUnEvent.
	 */
	@Override
	public int getDirectionImposee() {
		return this.direction;
	}

	@Override
	public final String toString() {
		return "Regarder dans la direction " + this.direction;
	}

}
