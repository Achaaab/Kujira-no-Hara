package conditions;

import java.util.HashMap;

import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import commandes.Deplacement;
import jeu.Partie;
import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * V�rifie si le H�ros a �quip� cette Arme.
 */
public class ConditionArmeEquipee extends Condition implements CommandeEvent, CommandeMenu {
	public int idArme;
	
	/**
	 * Constructeur explicite
	 * @param idArme identifiant de l'Arme � v�rifier
	 */
	public ConditionArmeEquipee(final int idArme) {
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionArmeEquipee(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idArme") );
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (partieActuelle.nombreDArmesPossedees > 0) {
			return partieActuelle.getArmeEquipee().id == this.idArme;
		}
		return false; //aucune arme poss�d�e
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}