package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

import son.LecteurAudio;

/**
 * Jouer un effet sonore.
 */
public class JouerEffetSonore extends CommandeEvent {
	private final String nomFichierSonore;
	
	/**
	 * @param nomFichierSonore nom du fichier de l'effet sonore � jouer
	 */
	public JouerEffetSonore(final String nomFichierSonore) {
		this.nomFichierSonore = nomFichierSonore;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public JouerEffetSonore(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomFichierSonore") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		LecteurAudio.playSe(nomFichierSonore);
		return curseurActuel+1;
	}

}
