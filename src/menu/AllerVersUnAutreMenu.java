package menu;

/**
 * Passer � un autre Menu
 */
public class AllerVersUnAutreMenu extends ComportementElementDeMenu {
	public Menu nouveauMenu;
	
	/**
	 * Constructeur explicite
	 * @param nouveauMenu Menu qui remplacera l'actuel
	 */
	public AllerVersUnAutreMenu(final Menu nouveauMenu) {
		this.nouveauMenu = nouveauMenu;
	}
	
	@Override
	public final void executer() {
		//FIXME TODO changerMenu() devrait �tre appel�e par le nouveau Lecteur
		this.element.menu.lecteur.changerMenu(nouveauMenu);
	}

}
