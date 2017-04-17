package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import son.LecteurAudio;
import son.Musique;

/**
 * Jouer un musique en boucle.
 */
public class JouerMusique extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(JouerMusique.class);
	
	private final String nomFichierSonore;
	private final float volume;
	private final int nombreDeFrames;
	private int frame;
	
	/**
	 * Constructeur explicite
	 * @param nomFichierSonore nom du fichier de la musique � jouer
	 * @param volume sonore (entre 0.0f et 1.0f)
	 * @param nombreDeFrames dur�e de l'entr�e en fondu
	 */
	public JouerMusique(final String nomFichierSonore, final float volume, final int nombreDeFrames) {
		this.nomFichierSonore = nomFichierSonore;
		this.volume = volume;
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public JouerMusique(final HashMap<String, Object> parametres) {
		this( 
				(String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? (float) parametres.get("volume") : Musique.VOLUME_MAXIMAL,
				parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (frame == 0) {
			// D�marrage de la musique
			LecteurAudio.playBgm(nomFichierSonore, 0);
			this.frame++;
			
			LOG.info("D�marrage de la musique.");
			return curseurActuel;
			
		} else if (frame < nombreDeFrames) {
			// Augmentation progressive du volume
			final float volumeProgressif = volume * (float) frame /(float) nombreDeFrames;
			LecteurAudio.bgmEnCours.modifierVolume(volumeProgressif);
			this.frame++;
			
			return curseurActuel;
			
		} else {
			// Le volume final est atteint
			LecteurAudio.bgmEnCours.modifierVolume(volume);
			this.frame = 0;
			
			LOG.info("La musique est d�marr�e.");
			return curseurActuel+1;
		}
	}

}
