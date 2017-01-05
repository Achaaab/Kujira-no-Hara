package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import main.Fenetre;

/**
 * V�rifie si le H�ros poss�de ce Gadget.
 */
public class ConditionGadgetPossede extends Condition implements CommandeEvent, CommandeMenu {
	public int idGadget;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idGadget identifiant du Gadget � v�rifier
	 */
	public ConditionGadgetPossede(final int numero, final int idGadget) {
		this.numero = numero;
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionGadgetPossede(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idGadget") 
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (partieActuelle.nombreDeGadgetsPossedes > 0) {
			return partieActuelle.gadgetsPossedes[this.idGadget];
		}
		return false; //aucun Gadget poss�d�
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}