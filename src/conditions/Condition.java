package conditions;

import java.util.ArrayList;
import java.util.HashMap;

import comportementEvent.CommandeEvent;

/**
 * Une Condition peut servir � d�finir le moment de d�clenchement d'une Page, ou faire partie du code Event.
 */
public abstract class Condition extends CommandeEvent {
	public int numero; //le num�ro de condition est le m�me que le num�ro de fin de condition qui correspond
	
	/**
	 * La Condition est elle v�rifi�e l� maintenant ?
	 * @return true si v�rifi�e, false si non v�rifi�e
	 */
	public abstract boolean estVerifiee();
	
	/**
	 * Une Condition est une Commande Event, elle peut �tre execut�e pour faire des sauts de curseur.
	 * Son execution est instantan�e.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		if ( estVerifiee() ) {
			return curseurActuel+1;
		} else {
			int nouveauCurseur = curseurActuel;
			boolean onATrouveLaFinDeSi = false;
			while (!onATrouveLaFinDeSi) {
				nouveauCurseur++;
				try {
					//la fin de si a le m�me numero que la condition
					if ( ((Condition) commandes.get(nouveauCurseur)).numero == numero ) {
						onATrouveLaFinDeSi = true;
					}
				} catch (IndexOutOfBoundsException e) {
					System.out.println("L'�v�nement n�"+this.page.event.numero+" n'a pas trouv� sa fin de condition "+this.numero+" :");
					e.printStackTrace();
				} catch (Exception e) {
					//pas une condition
				}
			}
			return nouveauCurseur+1;
		}
	}
}
