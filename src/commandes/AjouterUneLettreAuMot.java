package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Ajouter une lettre � la fin du mot de passe.
 */
public class AjouterUneLettreAuMot extends Commande implements CommandeMenu {
	private final String lettre;
	
	/**
	 * Constructeur explicite
	 * @param lettre � ajouter � la fin du mot
	 */
	public AjouterUneLettreAuMot(final String lettre) {
		this.lettre = lettre;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterUneLettreAuMot(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("lettre"));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (Fenetre.getPartieActuelle().mot.length() < Fenetre.getPartieActuelle().tailleMaximaleDuMot) {
			Fenetre.getPartieActuelle().mot += this.lettre;
			this.element.menu.reactualiserTousLesTextes();
		}
		return curseurActuel+1;
	}

}
