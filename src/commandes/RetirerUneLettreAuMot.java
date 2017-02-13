package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Retirer la derni�re lettre du mot de passe.
 */
public class RetirerUneLettreAuMot extends Commande implements CommandeMenu {
	final int numeroMot;
	
	/**
	 * Constructeur explicite
	 * @param numeroMot num�ro du mot auquel il faut retirer une lettre
	 */
	public RetirerUneLettreAuMot(final int numeroMot) {
		this.numeroMot = numeroMot;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public RetirerUneLettreAuMot(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroMot") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final String mot = Fenetre.getPartieActuelle().mots[this.numeroMot];
		if (mot != null && mot.length() > 0) {
			Fenetre.getPartieActuelle().mots[this.numeroMot] = mot.substring(0, mot.length()-1);
			this.element.menu.reactualiserTousLesTextes();
		}
		return curseurActuel+1;
	}

}
