package conditions;

import main.Fenetre;

/**
 * V�rifier la valeur d'un interrupteur
 */
public class ConditionInterrupteur extends Condition {
	int numeroInterrupteur;
	boolean valeurQuIlEstCenseAvoir;
	
	/**
	 * Constructeur explicite
	 * @param numeroInterrupteur num�ro de l'interrupteur � inspecter
	 * @param valeur attendue pour cet interrupteur
	 * @param numeroCondition identifiant de la condition
	 */
	public ConditionInterrupteur(final int numeroInterrupteur, final boolean valeur, final int numeroCondition) {
		this.numeroInterrupteur = numeroInterrupteur;
		this.valeurQuIlEstCenseAvoir = valeur;
		this.numero = numeroCondition;
	}
	
	@Override
	public final boolean estVerifiee() {
		return Fenetre.getPartieActuelle().interrupteurs[numeroInterrupteur] == valeurQuIlEstCenseAvoir;
	}

}