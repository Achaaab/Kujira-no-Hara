package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import main.Lecteur;
import map.LecteurMap;
import menu.LecteurMenu;
import menu.Menu;
import utilitaire.InterpreteurDeJson;

/**
 * Ouvrir un Menu.
 */
public class OuvrirMenu extends Commande implements CommandeEvent, CommandeMenu {
	final String nomMenu;
	
	/**
	 * Constructeur explicite
	 * @param nomMenu du Menu
	 */
	public OuvrirMenu(final String nomMenu) {
		this.nomMenu = nomMenu;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public OuvrirMenu(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomMenu") );
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Fenetre fenetre = Fenetre.getFenetre();
		final Lecteur lecteur = fenetre.lecteur;
		final LecteurMenu nouveauLecteur;
		final Menu nouveauMenu = InterpreteurDeJson.creerMenuDepuisJson(this.nomMenu, null); //pas de Menu parent car appel� depuis la Map
		if (lecteur instanceof LecteurMenu) {
			// Le Menu est ouvert depuis un autre Menu
			final LecteurMenu lecteurActuel = (LecteurMenu) lecteur;
			nouveauMenu.menuParent = lecteurActuel.menu;
			nouveauLecteur = new LecteurMenu(fenetre, nouveauMenu, lecteurActuel.lecteurMapMemorise);
		} else {
			// Le Menu est ouvert depuis une Map
			final LecteurMap lecteurActuel = (LecteurMap) lecteur;
			nouveauLecteur = new LecteurMenu(fenetre, nouveauMenu, lecteurActuel);
		}
		nouveauLecteur.changerMenu();
		return curseurActuel+1;
	}

}
