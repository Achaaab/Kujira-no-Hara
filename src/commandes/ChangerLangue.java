package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Main;
import menu.ImageMenu;
import menu.LecteurMenu;
import menu.Listable;
import menu.Liste;
import menu.Menu;
import menu.Texte;

/**
 * Changer la langue du jeu.
 */
public class ChangerLangue extends Commande implements CommandeMenu {
	private final int nouvelleLangue;
	
	/**
	 * Constructeur explicite
	 * @param nouvelleLangue num�ro de la nouvelle langue � adopter
	 */
	private ChangerLangue(final int nouvelleLangue) {
		this.nouvelleLangue = nouvelleLangue;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ChangerLangue(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("langue") );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Main.langue = this.nouvelleLangue;
		final Menu menu = ((LecteurMenu) Main.lecteur).menu;
		for (Texte texte : menu.textes) {
			texte.actualiserImage();
		}
		for (Liste<Listable> liste : menu.listes) {
			liste.elements = liste.genererLesImagesDesElements(liste.largeurMaximaleElement, liste.hauteurMaximaleElement); //la taille maximale est sup�rieure � la taille minimale
			for (ImageMenu element : liste.elements) {
				element.menu = menu;
			}
			liste.determinerLesElementsAAfficher();
		}
		return curseurActuel+1;
	}

}
