package commandes;

import main.Commande;
import map.PageEvent;

/**
 * Chaque Page de comportement d'un Event contient des Commandes.
 * Celles-ci sont execut�es dans l'ordre lorsque la page est d�clench�e.
 * Lorsque la Commande est consid�r�e comme termin�e, le curseur se d�place � la Commande suivante.
 */
public interface CommandeEvent extends Commande {
	/**
	 * Une Commande Event conna�t la Page � laquelle elle appartient.
	 * @return Page de cette Commande Event
	 */
	PageEvent getPage();
	
	/**
	 * Apprendre � la commande qui est sa Page.
	 * @param page � m�moriser
	 */
	void setPage(PageEvent page);

}
