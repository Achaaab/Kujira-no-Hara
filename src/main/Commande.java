package main;

import java.util.ArrayList;

import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Une Commande modifie l'�tat du jeu.
 * Elle peut �tre lanc�e par une Page d'Event, ou par un El�ment de Menu.
 */
public abstract class Commande {
	/** [CommandeEvent] Eventuelle Page d'Event qui a appel� cette Commande */
	public PageEvent page;
	/** [CommandeMenu] Element de Menu qui a appel� cette Commande de Menu */
	public ElementDeMenu element;
	
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut �tre inchang� (attendre n frames...) ;
	 * le curseur peut �tre incr�ment� (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit execut�e
	 * @param commandes liste des Commandes de la Page de comportement en train d'�tre lue
	 * @return nouvelle position du curseur apr�s l'execution totale ou partielle de la Commande
	 */
	public abstract int executer(int curseurActuel, ArrayList<Commande> commandes);
}
