package commandes;

import java.util.ArrayList;
import main.Commande;

/**
 * Une Boucle r�p�te ind�finiment les Commandes qu'elle contient.
 */
public class BoucleFin extends Commande implements CommandeEvent, CommandeMenu {
	public int numero; //le num�ro de Boucle est le m�me que le num�ro de fin de Boucle qui correspond

	/**
	 * Une Boucle est une Commande Event, elle peut �tre execut�e pour faire des sauts de curseur.
	 * Son execution est instantan�e.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof Boucle) {
				final Boucle debutDeBoucle = (Boucle) commande;
				if (debutDeBoucle.numero == this.numero) {
					//le d�but de Boucle a �t� trouv�
					return i+1;
				}
			}
		}
		//le d�but de Boucle n'a pas �t� trouv�
		System.err.println("Le d�but de boucle num�ro "+numero+" n'a pas �t� trouv� !");
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
