package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;

/**
 * Arr�ter le fond sonore.
 */
public class ArreterFondSonore extends Commande implements CommandeEvent, CommandeMenu {
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ArreterFondSonore(final HashMap<String, Object> parametres) {}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		LecteurAudio.bgsEnCours.arreter();
		
		return curseurActuel+1;
	}

}
