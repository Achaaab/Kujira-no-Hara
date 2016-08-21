package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Fenetre;
import utilitaire.Maths.Inegalite;

/**
 * V�rifier la valeur d'une variable
 */
public class ConditionVariable extends Condition implements CommandeEvent, CommandeMenu {
	private int numeroVariable;
	private Inegalite inegalite;
	private int valeurQuIlEstCenseAvoir;
	
	/**
	 * Utiliser les constantes situ�es dans la classe pour d�finir le type de comparaison.
	 * @param numero de la Condition
	 * @param numeroVariable num�ro de la variable
	 * @param symboleInegalite = egal ; >= superieur large ; <= inferieur large ; > superieur strict ; < inferieur strict ; != diff�rent
	 * @param valeur comparative
	 */
	public ConditionVariable(final int numero, final int numeroVariable, final String symboleInegalite, final int valeur) {
		this.numero = numero;
		this.numeroVariable = numeroVariable;
		this.inegalite = Inegalite.getInegalite(symboleInegalite);
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public final boolean estVerifiee() {
		final int[] variables = Fenetre.getPartieActuelle().variables;
		return inegalite.comparer(variables[numeroVariable], valeurQuIlEstCenseAvoir);
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}