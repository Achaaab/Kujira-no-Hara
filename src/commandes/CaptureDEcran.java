package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Prendre une capture d'�cran du jeu.
 */
public class CaptureDEcran extends Commande implements CommandeEvent, CommandeMenu {
	
	/**
	 * Constructeur explicite
	 */
	public CaptureDEcran() {
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public CaptureDEcran(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final void executer() {
		Fenetre.getFenetre().lecteur.faireUneCaptureDEcran();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		executer();
		return curseurActuel+1;
	}

}
