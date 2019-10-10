package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Arr�ter le Chronometre.
 */
public class ArreterChronometre extends Commande implements CommandeEvent {

	/**
	 * Constructeur vide
	 */
	public ArreterChronometre() {
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ArreterChronometre(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().chronometre = null;
		return curseurActuel + 1;
	}

}
