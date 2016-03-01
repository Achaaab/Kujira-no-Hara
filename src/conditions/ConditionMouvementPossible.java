package conditions;

import java.util.HashMap;

import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.Deplacement;

/**
 * V�rifie si ce Mouvement est possible.
 * L'Event consid�r� est mentionn� dans l'objet Mouvement.
 */
public class ConditionMouvementPossible extends Condition implements CommandeEvent {
	private Deplacement deplacement;

	/**
	 * Constructeur explicite
	 * @param deplacement qui ne contient qu'un Mouvement, ainsi que l'id de l'Event � d�placer
	 * @param numeroCondition num�ro de la condition
	 */
	public ConditionMouvementPossible(final Deplacement deplacement, final int numeroCondition) {
		this.deplacement = deplacement;
		this.numero = numeroCondition;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionMouvementPossible(final HashMap<String, Object> parametres) {
		this( new Deplacement((JSONObject) parametres.get("deplacement"), null),
				(int) parametres.get("numero"));
	}

	@Override
	public final boolean estVerifiee() {
		this.deplacement.page = this.page;
		return this.deplacement.mouvements.get(0).mouvementPossible();
	}

	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
}
