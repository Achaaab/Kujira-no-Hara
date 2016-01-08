package menu;

/**
 * Un El�ment de Menu peut avoir diverses cons�quences une fois s�lectionn� : ouverture d'un autre menu etc.
 */
public abstract class ComportementElementDeMenu {
	public Selectionnable element;
	
	/**
	 * Executer le Comportement de l'Element de Menu
	 */
	public abstract void executer();
}
