package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Fenetre;

/**
 * Le joueur poss�de-t-il assez d'argent ?
 */
public class ConditionArgent extends Condition implements CommandeEvent, CommandeMenu {

	/** In�galit�s possibles pour comparer l'argent */
	private static enum Inegalite {
		PLUS_OU_AUTANT(">="), PLUS_STRICTEMENT(">"), MOINS_OU_AUTANT("<="), MOINS_STRICTEMENT("<");
		
		public String symbole;
		
		private Inegalite(String symbole) {
			this.symbole = symbole;
		}
		
		public static Inegalite getInegalite(String symbole) {
			for (Inegalite inegalite : Inegalite.values()) {
				if (inegalite.symbole.equals(symbole)) {
					return inegalite;
				}
			}
			System.err.println("Cette inegalit� n'a pas �t� trouv�e : "+symbole);
			return null;
		}
	}
	
	private int quantite;
	private Inegalite inegalite;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param quantite d'argent � poss�der
	 * @param symbole de l'In�galit� � utiliser pour comparer l'argent
	 */
	public ConditionArgent(final int numero, final Integer quantite, final String symbole) {
		this.numero = numero;
		this.quantite = quantite;
		this.inegalite = Inegalite.getInegalite(symbole);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionArgent(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(int) parametres.get("quantite"),
			(String) parametres.get("inegalite")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		switch(this.inegalite) {
			case PLUS_OU_AUTANT:
				return Fenetre.getPartieActuelle().argent >= this.quantite;
			case PLUS_STRICTEMENT:
				return Fenetre.getPartieActuelle().argent > this.quantite;
			case MOINS_STRICTEMENT:
				return Fenetre.getPartieActuelle().argent < this.quantite;
			case MOINS_OU_AUTANT:
				return Fenetre.getPartieActuelle().argent <= this.quantite;
			default:
				return Fenetre.getPartieActuelle().argent >= this.quantite;
		}
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
