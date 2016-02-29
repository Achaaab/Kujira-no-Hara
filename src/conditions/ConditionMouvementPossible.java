package conditions;

import java.util.HashMap;

import org.json.JSONObject;

import commandes.CommandeEvent;
import mouvements.Mouvement;
import utilitaire.InterpreteurDeJson;

/**
 * V�rifie si ce Mouvement est possible.
 * L'Event consid�r� est mentionn� dans l'objet Mouvement.
 */
public class ConditionMouvementPossible extends Condition implements CommandeEvent {
	private Mouvement mouvement;

	/**
	 * Constructeur explicite
	 * @param mouvement dont il faut v�rifier la faisabilit�
	 * @param numeroCondition num�ro de la condition
	 */
	public ConditionMouvementPossible(final Mouvement mouvement, final int numeroCondition) {
		this.mouvement = mouvement;
		this.numero = numeroCondition;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionMouvementPossible(final HashMap<String, Object> parametres) {
		this(InterpreteurDeJson.recupererUnMouvement((JSONObject) parametres.get("mouvement")),
				(int) parametres.get("numero"));
	}

	@Override
	public final boolean estVerifiee() {
		return this.mouvement.mouvementPossible();
	}

	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
}
