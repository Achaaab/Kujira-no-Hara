package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Ajouter de l'argent au joueur.
 */
public class AjouterArgent extends Commande implements CommandeEvent, CommandeMenu {
	private final int quantite;

	/**
	 * Constructeur explicite
	 * 
	 * @param quantite � ajouter
	 */
	public AjouterArgent(final int quantite) {
		this.quantite = quantite;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterArgent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("quantite"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().argent += quantite;

		return curseurActuel + 1;
	}

}