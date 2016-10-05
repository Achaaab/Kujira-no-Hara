package commandes;

import java.util.ArrayList;
import main.Commande;

/**
 * Une Boucle r�p�te ind�finiment les Commandes qu'elle contient.
 */
public class Boucle extends Commande implements CommandeEvent, CommandeMenu {
	public int numero; //le num�ro de Boucle est le m�me que le num�ro de fin de Boucle qui correspond

	/**
	 * Constructeur explicite
	 * @param numero identifiant de la Boucle
	 */
	public Boucle(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Une Boucle est une Commande Event, elle peut �tre execut�e pour faire des sauts de curseur.
	 * Son execution est instantan�e.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
