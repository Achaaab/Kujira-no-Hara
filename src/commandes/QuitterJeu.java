package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Main;

/**
 * Eteindre le jeu.
 */
public class QuitterJeu extends Commande implements CommandeMenu {
	
	/**
	 * Constructeur vide
	 */
	public QuitterJeu() {
		
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public QuitterJeu(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Main.quitterLeJeu = true;
		
		return curseurActuel+1;
	}

}
