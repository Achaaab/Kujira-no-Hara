package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Objet;
import main.Fenetre;

/**
 * Le joueur poss�de-t-il l'Objet ?
 */
public class ConditionObjetPossede extends Condition implements CommandeEvent, CommandeMenu {
	private int numeroObjet;
	
	/**
	 * Constructeur partiel
	 * R�serv� aux Conditions de Pages et Menus
	 * @param objet identifiant de l'Objet : num�ro ou nom
	 */
	public ConditionObjetPossede(final Object objet) {
		try {
			this.numeroObjet = (Integer) objet;
		} catch (Exception e) {
			this.numeroObjet = Objet.objetsDuJeuHash.get((String) objet).numero;
		}
	}
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param objet identifiant de l'Objet : num�ro ou nom
	 */
	public ConditionObjetPossede(final int numero, final Object objet) {
		this.numero = numero;
		try {
			this.numeroObjet = (Integer) objet;
		} catch (Exception e) {
			this.numeroObjet = Objet.objetsDuJeuHash.get((String) objet).numero;
		}
	}
		
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionObjetPossede(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			parametres.get("objet")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return Fenetre.getPartieActuelle().objetsPossedes[this.numeroObjet] >= 1;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
