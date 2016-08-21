package conditions;

import java.util.ArrayList;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Commande;

/**
 * Une Condition peut servir � d�finir le moment de d�clenchement d'une Page, ou faire partie du code Event.
 */
public abstract class Condition extends Commande {
	public int numero = -1; //le num�ro de condition est le m�me que le num�ro de fin de condition qui correspond

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
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		//une Condition doit avoir un num�ro pour �tre ex�cut�e comme Commande Event
		if (this.numero == -1) {
			System.err.println("La condition "+this.getClass().getName()+" n'a pas de num�ro !");
		}
		
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
					if (this instanceof CommandeEvent) {
						System.out.println("L'�v�nement n�"+this.page.event.numero+" n'a pas trouv� sa fin de condition "+this.numero+" :");
					}
					if (this instanceof CommandeMenu) {
						System.out.println("L'�l�ment de menu n�"+this.element.id+" n'a pas trouv� sa fin de condition "+this.numero+" :");
					}
					e.printStackTrace();
				} catch (Exception e) {
					//pas une condition
				}
			}
			return nouveauCurseur+1;
		}
	}
	
	/** 
	 * Est-ce que la Condition demande un mouvement particulier du H�ros ?
	 * Contact, Arriv�eAuContact, Parler... 
	 * @return false si la Condition est � consid�rer pour l'apparence d'un Event, false sinon
	 */
	public abstract boolean estLieeAuHeros();
	
	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 * Cette m�thode, exig�e par CommandeMenu, est la m�me pour toutes les Conditions.
	 */
	public void executer() {
		//rien
	}
}
