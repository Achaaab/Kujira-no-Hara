package menu;

/**
 * Un �l�ment de menu peut avoir diverses cons�quences une fois s�lectionn� : ouverture d'un autre menu etc.
 */
public abstract class ComportementElementDeMenu {
	public Selectionnable element;
	public abstract void executer();
}
