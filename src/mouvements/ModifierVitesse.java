package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Modifier la vitesse actuelle d'un Event.
 */
public class ModifierVitesse extends Mouvement {
	private int nouvelleVitesse;
	
	/**
	 * Constructeur explicite
	 * @param nouvelleVitesse � donner � l'Event
	 */
	public ModifierVitesse(final int nouvelleVitesse) {
		this.nouvelleVitesse = nouvelleVitesse;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierVitesse(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("vitesse") );
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
		event.vitesseActuelle = this.nouvelleVitesse;
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
		return "nouvelle vitesse : " + this.nouvelleVitesse;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
