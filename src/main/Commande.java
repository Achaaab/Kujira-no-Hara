package main;

import java.util.ArrayList;

/**
 * Une Commande modifie l'�tat du jeu.
 * Elle peut �tre lanc�e par une Page d'Event, ou par un El�ment de Menu.
 */
public interface Commande {
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut �tre inchang� (attendre n frames...) ;
	 * le curseur peut �tre incr�ment� (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit execut�e
	 * @param commandes liste des Commandes de la Page de comportement en train d'�tre lue
	 * @return nouvelle position du curseur apr�s l'execution totale ou partielle de la Commande
	 */
	int executer(int curseurActuel, ArrayList<? extends Commande> commandes);
}
