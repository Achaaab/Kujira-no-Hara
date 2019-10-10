package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Ajouter une lettre � la fin du mot de passe.
 */
public class AjouterUneLettreAuMot extends Commande implements CommandeMenu {
	private final String lettre;
	private final int numeroMot;

	/**
	 * Constructeur explicite
	 * 
	 * @param lettre    � ajouter � la fin du mot
	 * @param numeroMot num�ro du mot auquel ajouter une lettre
	 */
	public AjouterUneLettreAuMot(final String lettre, final int numeroMot) {
		this.lettre = lettre;
		this.numeroMot = numeroMot;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterUneLettreAuMot(final HashMap<String, Object> parametres) {
		this((String) parametres.get("lettre"), (int) parametres.get("numeroMot"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final String mot = getPartieActuelle().mots[this.numeroMot];
		if (mot == null) {
			// mot vide
			getPartieActuelle().mots[this.numeroMot] = this.lettre;
			this.element.menu.reactualiserTousLesTextes();
		} else if (mot.length() < getPartieActuelle().tailleMaximaleDuMot) {
			// mot d�j� rempli
			getPartieActuelle().mots[this.numeroMot] += this.lettre;
			this.element.menu.reactualiserTousLesTextes();
		}
		return curseurActuel + 1;
	}

}
