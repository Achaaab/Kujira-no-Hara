package conditions;

import java.util.HashMap;

import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.Mouvement;
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
		this(InterpreteurDeJson.recupererUnMouvement((JSONObject) parametres.get("mouvement"), null),
				(int) parametres.get("numero"));
	}

	@Override
	public final boolean estVerifiee() {
		this.mouvement.page = this.page; //on informe le Mouvement de sa Page
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
