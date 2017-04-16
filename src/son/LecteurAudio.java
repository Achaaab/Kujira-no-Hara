package son;

/**
 * Classe utilitaire charg�e de lire les fichiers audio du jeu.
 */
public abstract class LecteurAudio {
	
	public static Musique bgmEnCours = null;
	public static Musique bgsEnCours = null;
	
	/**
	 * Jouer un effet sonore.
	 * Volume sonore maximal par d�faut.
	 * @param nom du fichier audio
	 */
	public static synchronized void playSe(final String nom) {
		playSe(nom, Musique.VOLUME_MAXIMAL);
	}
	
	/**
	 * Jouer un effet sonore.
	 * @param nom du fichier audio
	 * @param volume sonore
	 */
	public static synchronized void playSe(final String nom, final float volume) {
		final Musique musique = new Musique(nom, Musique.TypeMusique.SE, volume);
		musique.jouerUneSeuleFois();
	}
	
	/**
	 * Jouer une fanfare musicale.
	 * @param nom du fichier audio
	 * @param volume sonore
	 */
	//TODO un ME doit mettre en silence le BGM durant son execution
	public static synchronized void playMe(final String nom, final float volume) {
		final Musique musique = new Musique(nom, Musique.TypeMusique.ME, volume);
		musique.jouerUneSeuleFois();
	}
	
	/**
	 * Jouer une musique.
	 * Volume maximal par d�faut.
	 * @param nom du fichier audio
	 */
	public static synchronized void playBgm(final String nom) {
		playBgm(nom, Musique.VOLUME_MAXIMAL);
	}
	
	/**
	 * Jouer une musique
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 */
	public static synchronized void playBgm(final String nom, final float volume) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.equals("")) {
			return;
		}
		
		// Si on est d�j� en train de jouer le bon morceau, on ne fait rien
		if (LecteurAudio.bgmEnCours == null || !nom.equals(LecteurAudio.bgmEnCours.nom)) {
			// On �teint la musique actuelle
			stopBgm();

			// On lance la nouvelle
			final Musique musique = new Musique(nom, Musique.TypeMusique.BGM, volume);
			musique.jouerEnBoucle();
				
			// On met � jour les donn�es
			LecteurAudio.bgmEnCours = musique;
		
		// Modification du volume uniquement
		} else if (bgmEnCours.volumeActuel != volume) {
			bgmEnCours.modifierVolume(volume);
		}
	}
	
	/**
	 * Arr�ter la musique actuellement jou�e.
	 */
	public static synchronized void stopBgm() {
		if (LecteurAudio.bgmEnCours != null) {
			LecteurAudio.bgmEnCours.arreter();
		}
	}
	
	/**
	 * Jouer un fond sonore.
	 * Volume maximal par d�faut.
	 * @param nom du fichier audio
	 */
	public static synchronized void playBgs(final String nom) {
		playBgs(nom, Musique.VOLUME_MAXIMAL);
	}
	
	/**
	 * Jouer un fond sonore.
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 */
	public static synchronized void playBgs(final String nom, final float volume) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.equals("")) {
			return;
		}
			
		// Si on est d�j� en train de jouer le bon fond sonore, on ne fait rien
		if (LecteurAudio.bgsEnCours == null || !nom.equals(LecteurAudio.bgsEnCours.nom)) {
			// On �teint le fond sonore actuel
			stopBgs();

			// On lance le nouveau
			final Musique musique = new Musique(nom, Musique.TypeMusique.BGS, volume);
			musique.jouerEnBoucle();
				
			// On met � jour les donn�es
			LecteurAudio.bgsEnCours = musique;
			
		// Modification du volume uniquement
		} else if (bgsEnCours.volumeActuel != volume) {
			bgsEnCours.modifierVolume(volume);
		}
	}
	
	/**
	 * Arr�ter le fond sonore actuellement jou�.
	 */
	public static synchronized void stopBgs() {
		if (LecteurAudio.bgsEnCours != null) {
			LecteurAudio.bgsEnCours.arreter();
		}
	}
	
}
