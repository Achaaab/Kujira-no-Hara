package commandes;

import java.util.ArrayList;
import main.Commande;

/**
 * Sortir de la Boucle actuelle.
 */
public class BoucleSortir extends Commande implements CommandeEvent, CommandeMenu {
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
			if (commande instanceof BoucleFin) {
				final Boucle finDeBoucle = (Boucle) commande;
				if (finDeBoucle.numero == this.numero) {
					//la fin de Boucle a �t� trouv�e
					return i+1;
				}
			}
		}
		//la fin de Boucle n'a pas �t� trouv�e
		System.err.println("La fin de boucle num�ro "+numero+" n'a pas �t� trouv�e !");
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
