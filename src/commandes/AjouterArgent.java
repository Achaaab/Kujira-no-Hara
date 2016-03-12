package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Ajouter de l'argent au joueur.
 */
public class AjouterArgent extends Commande implements CommandeEvent, CommandeMenu {
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param quantite � ajouter
	 */
	public AjouterArgent(final int quantite) {
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterArgent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("quantite"));
	}
	
	@Override
	public final void executer() {
		Fenetre.getPartieActuelle().argent += quantite;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}