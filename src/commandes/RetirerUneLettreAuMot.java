package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Ajouter une lettre � la fin du mot de passe.
 */
public class RetirerUneLettreAuMot extends Commande implements CommandeMenu {
	
	/**
	 * Constructeur explicite
	 */
	public RetirerUneLettreAuMot() {
		
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public RetirerUneLettreAuMot(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final String mot = Fenetre.getPartieActuelle().mot;
		if (mot.length()>0) {
			Fenetre.getPartieActuelle().mot += mot.substring(0, mot.length()-1);
		}
		return curseurActuel+1;
	}

}
