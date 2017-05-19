package utilitaire.son;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Permet d'uniformiser le contr�le de la Musique malgr� les diff�rents formats audio.
 */
public abstract class Musique {
	//constantes
	protected static final Logger LOG = LogManager.getLogger(Musique.class);
	public static final float VOLUME_MAXIMAL = 1.0f;
	private static final long DUREE_MAXIMALE_SE = 5000; //en millisecondes
	
	/**
	 * Le clip peut �tre un Clip javax ou bien un OggClip.
	 */
	protected Object clip;
	protected InputStream stream;
	public String nom;
	public FormatAudio format;
	public long dureeMillisecondes;
	public TypeMusique type;
	protected Thread thread;
	public float volumeActuel;
	
	/**
	 * Diff�rents formats de fichiers audio possibles
	 */
	public enum FormatAudio {
		WAV("WAV"), OGG("OGG"), MP3("MP3");
		
		public final String nom;
		
		/**
		 * @param nom du format audio
		 */
		FormatAudio(final String nom) {
			this.nom = nom;
		}
	}
	
	/**
	 * Diff�rents types de fichiers audio possibles
	 */
	public enum TypeMusique {
		BGM("BGM"), BGS("BGS"), ME("ME"), SE("SE");

		public final String nom;
		
		/**
		 * @param nom du type de musique
		 */
		TypeMusique(final String nom) {
			this.nom = nom;
		}
	}
	
	/**
	 * Thread parall�le qui joue la Musique.
	 */
	abstract class LancerSon implements Runnable {
		private final TypeMusique type;
		private final long duree;
		private final Float volumeBgmMemorise;
		
		protected LancerSon(TypeMusique type, long duree, Float volumeBgmMemorise) {
			super();
			this.type = type;
			this.duree = duree;
			this.volumeBgmMemorise = volumeBgmMemorise;
		}
		
		/**
		 * Refermer le clip � la fin de son execution.
		 * Si c'est un ME, red�marrer le BGM mis en silence.
		 */
		protected void fermerALaFin() {
			if (TypeMusique.SE.equals(this.type)) {
		    	// interrompre un SE trop long
		    	try {
		    		Thread.sleep(DUREE_MAXIMALE_SE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    	
		    } else if (TypeMusique.ME.equals(this.type)) {
		    	// attendre la fin du ME
		    	try {
		    		Thread.sleep(this.duree);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    	
		    	// red�marrer le BGM apr�s la fin du ME
		    	if (LecteurAudio.bgmEnCours != null) {
		    		LecteurAudio.bgmEnCours.modifierVolume(this.volumeBgmMemorise);
		    	}
		    }
		    arreter();
		}
	}
	
	/**
	 * Constructeur explicite
	 * @param nom du fichier audio
	 * @param type BGM, BGS, ME, SE
	 * @param volume entre 0.0 et 1.0
	 */
	protected Musique(final String nom, final TypeMusique type, final float volume) {
		this.nom = nom;
		this.type = type;
		this.volumeActuel = volume;
	}
	
	/**
	 * Modifier le volume de la Musique.
	 * @param nouveauVolume � appliquer
	 */
	public abstract void modifierVolume(final float nouveauVolume);
	
	/**
	 * Jouer un fichier sonore qui s'arr�tera tout seul arriv� � la fin.
	 */
	public abstract void jouerUneSeuleFois(final Float volumeBgmMemorise);
	
	/**
	 * Jouer une fichier sonore qui tourne en boucle sans s'arr�ter.
	 */
	public abstract void jouerEnBoucle();

	/**
	 * Arr�ter cette Musique.
	 * Il y a potentiellement deux threads � fermer : le Clip et l'InputStream.
	 * Ne pas utiliser autrement que via LecteurAudio.arreterBgm().
	 */
	public final void arreter() {
		//on ferme le clip
		arreterSpecifique();
		//on ferme l'InputStream
		if (this.stream!=null) {
			try {
				this.stream.close();
			} catch (IOException e) {
				LOG.warn("Impossible de fermer le stream audio "+this.nom, e);
			}
		}
	}
	
	/**
	 * Arr�ter la Musique en fonction du format audio.
	 */
	public abstract void arreterSpecifique();
	
	/**
	 * Mettre la Musique en pause.
	 */
	public abstract void mettreEnPause();
	
	/**
	 * Continuer la lecture de la Musique apr�s la pause.
	 */
	public abstract void reprendreApresPause();
	
}
