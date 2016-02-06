package commandesEvent;

import java.util.ArrayList;

import map.PageEvent;

/**
 * Chaque Page de comportement d'un Event contient des Commandes.
 * Celles-ci sont execut�es dans l'ordre lorsque la page est d�clench�e.
 * Lorsque la Commande est consid�r�e comme termin�e, le curseur se d�place � la Commande suivante.
 */
public abstract class CommandeEvent {
	public PageEvent page; //une Commande Event conna�t la Page � laquelle elle appartient
	
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut �tre inchang� (attendre n frames...) ;
	 * le curseur peut �tre incr�ment� (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit execut�e
	 * @param commandes liste des Commandes de la Page de comportement en train d'�tre lue
	 * @return nouvelle position du curseur apr�s l'execution totale ou partielle de la Commande
	 */
	public abstract int executer(int curseurActuel, ArrayList<CommandeEvent> commandes);

}
