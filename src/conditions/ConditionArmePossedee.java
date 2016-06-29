package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import main.Fenetre;

/**
 * V�rifie si le H�ros poss�de cette Arme.
 */
public class ConditionArmePossedee extends Condition implements CommandeEvent, CommandeMenu {
	public int idArme;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idArme identifiant de l'Arme � v�rifier
	 */
	public ConditionArmePossedee(final int numero, final int idArme) {
		this.numero = numero;
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionArmePossedee(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idArme") 
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (partieActuelle.nombreDArmesPossedees > 0) {
			return partieActuelle.armesPossedees[this.idArme];
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