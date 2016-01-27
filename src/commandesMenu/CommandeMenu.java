package commandesMenu;

import menu.Selectionnable;

/**
 * Un El�ment de Menu peut avoir diverses cons�quences une fois s�lectionn� : ouverture d'un autre menu etc.
 */
public abstract class CommandeMenu {
	public Selectionnable element;
	
	/**
	 * Executer le Comportement de l'Element de Menu
	 */
	public abstract void executer();
}
