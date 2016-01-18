package conditions;

import main.Fenetre;
import main.Partie;

/**
 * V�rifier si le H�ros a �quip� cette Arme
 */
public class ConditionArmeEquipee extends Condition {
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
		if (partieActuelle.idArmesPossedees.size()>0) {
			return partieActuelle.getArmeEquipee().id == this.idArme;
		}
		return false; //aucune arme poss�d�e
	}
	
	public final boolean estLieeAuHeros() {
		return false;
	}

}