package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.son.LecteurAudio;

/**
 * Arr�ter la musique.
 */
public class ArreterMusique extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ArreterMusique.class);
	
	/** Dur�e totale de l'arr�t en fondu */
	private final int nombreDeFrames;
	/** Compteur de frames de l'arr�t en fondu */
	private int frame;
	private float volumeInitial;
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames dur�e totale de l'arr�t en fondu
	 */
	private ArreterMusique(final int nombreDeFrames) {
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ArreterMusique(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// On ne fait rien si la musique est d�j� arr�t�e
		if (LecteurAudio.bgmEnCours == null) {
			LOG.warn("La musique est d�j� arr�t�e.");
			return curseurActuel+1;
		}
		
		// On m�morise le volume initial
		if (frame == 0) {
			volumeInitial = LecteurAudio.bgmEnCours.volumeActuel;
		}
		
		if (frame < nombreDeFrames) {
			// Arr�t en fondu
			final float nouveauVolume = volumeInitial * (float) (nombreDeFrames-frame)/(float) nombreDeFrames;
			LecteurAudio.bgmEnCours.modifierVolume(nouveauVolume);
			frame++;
			
			return curseurActuel;
			
		} else {
			// L'arr�t en fondu est termin�
			LecteurAudio.stopBgm();
			frame = 0;
			
			LOG.info("Arr�t total de la musique.");
			return curseurActuel+1;
		}
	}

}
