package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;

/**
 * Arr�ter le fond sonore.
 */
public class ArreterFondSonore extends Commande implements CommandeEvent, CommandeMenu {
	/** Dur�e totale de l'arr�t en fondu */
	private final int nombreDeFrames;
	/** Compteur de frames de l'arr�t en fondu */
	private int frame;
	private float volumeInitial;
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames dur�e totale de l'arr�t en fondu
	 */
	private ArreterFondSonore(final int nombreDeFrames) {
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ArreterFondSonore(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// On m�morise le volume initial
		if (frame == 0) {
			volumeInitial = LecteurAudio.bgsEnCours.volumeActuel;
		}
		
		if (frame < nombreDeFrames) {
			// Arr�t en fondu
			final float nouveauVolume = volumeInitial * (float) (nombreDeFrames-frame)/(float) nombreDeFrames;
			LecteurAudio.bgsEnCours.modifierVolume(nouveauVolume);
			frame++;
			
			return curseurActuel;
			
		} else {
			// L'arr�t en fondu est termin�
			LecteurAudio.bgsEnCours.arreter();
			frame = 0;
			
			return curseurActuel+1;
		}
	}

}
