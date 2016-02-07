package commandes;

import main.Commande;
import menu.ElementDeMenu;

/**
 * Un El�ment de Menu peut avoir diverses cons�quences une fois s�lectionn� : ouverture d'un autre menu etc.
 */
public interface CommandeMenu extends Commande {
	
	/**
	 * Une Commande de Menu conna�t son El�ment de Menu.
	 * @return l'El�ment de Menu de cette Commande
	 */
	ElementDeMenu getElement();
	
	/**
	 * Apprendre � la commande qui est son El�ment de Menu.
	 * @param element de Menu � m�moriser
	 */
	void setElement(final ElementDeMenu element);
	
	/**
	 * Executer la Commande de Menu.
	 */
	void executer();

}
