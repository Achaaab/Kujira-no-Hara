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
	private final int numeroMot;
	
	/**
	 * Constructeur explicite
	 * @param lettre � ajouter � la fin du mot
	 */
	public AjouterUneLettreAuMot(final String lettre, final int numeroMot) {
		this.lettre = lettre;
		this.numeroMot = numeroMot;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterUneLettreAuMot(final HashMap<String, Object> parametres) {
		this( 
				(String) parametres.get("lettre"),
				(int) parametres.get("numeroMot")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		String mot = Fenetre.getPartieActuelle().mots[this.numeroMot];
		if (mot == null) {
			// mot vide
			Fenetre.getPartieActuelle().mots[this.numeroMot] = this.lettre;
		} else if (mot.length() < Fenetre.getPartieActuelle().tailleMaximaleDuMot) {
			// mot d�j� rempli
			Fenetre.getPartieActuelle().mots[this.numeroMot] += this.lettre;
			this.element.menu.reactualiserTousLesTextes();
		}
		return curseurActuel+1;
	}

}
