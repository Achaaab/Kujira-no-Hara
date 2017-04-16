package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;
import son.Musique;

/**
 * Jouer un fond sonore en boucle.
 */
public class JouerFondSonore extends Commande implements CommandeEvent, CommandeMenu {
	private final String nomFichierSonore;
	private final float volume;
	
	/**
	 * Constructeur explicite
	 * @param nomFichierSonore nom du fichier de la musique � jouer
	 * @param volume sonore (entre 0.0f et 1.0f)
	 */
	public JouerFondSonore(final String nomFichierSonore, final float volume) {
		this.nomFichierSonore = nomFichierSonore;
		this.volume = volume;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public JouerFondSonore(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? (float) parametres.get("volume") : Musique.VOLUME_MAXIMAL
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		LecteurAudio.playBgs(nomFichierSonore, volume);
		
		return curseurActuel+1;
	}

}
