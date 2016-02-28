package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import main.Fenetre;

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