package utilitaire.son;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Classe utilitaire charg�e de lire les fichiers audio du jeu.
 */
public abstract class LecteurAudio {
	private static final Logger LOG = LogManager.getLogger(LecteurAudio.class);
	static final int NOMBRE_DE_PISTES = 10;
	
	public static Musique[] bgmEnCours = new Musique[NOMBRE_DE_PISTES];
	public static Musique[] bgsEnCours = new Musique[NOMBRE_DE_PISTES];
	public static ArrayList<Musique> seEnCours = new ArrayList<Musique>();
	
	/**
	 * Jouer un effet sonore.
	 * Volume sonore maximal par defaut.
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
		final Musique musique;
		if (nom.endsWith(".ogg")) {
			musique = new MusiqueOgg(nom, Musique.TypeMusique.SE, volume);
		} else if (nom.endsWith(".wav")) {
			musique = new MusiqueWav(nom, Musique.TypeMusique.SE, volume);
		} else {
			LOG.error("Format audio inconnu : " + nom);
			return;
		}
		LOG.debug("SE d�marr� : " + nom);
		musique.jouerUneSeuleFois(null);

		// On met a jour les donnees
		seEnCours.add(musique);
	}
	
	/**
	 * Jouer une fanfare musicale.
	 * @param nom du fichier audio
	 * @param volume sonore
	 */
	public static synchronized void playMe(final String nom, final float volume) {
		final Musique musique;
		if (nom.endsWith(".ogg")) {
			musique = new MusiqueOgg(nom, Musique.TypeMusique.ME, volume);
		} else if (nom.endsWith(".wav")) {
			musique = new MusiqueWav(nom, Musique.TypeMusique.ME, volume);
		} else {
			LOG.error("Format audio inconnu : "+nom);
			return;
		}
		final Float[] volumeBgmMemorise = new Float[NOMBRE_DE_PISTES];
		for (int i = 0; i<NOMBRE_DE_PISTES; i++) {
			final Musique bgm = LecteurAudio.bgmEnCours[i];
			if (bgm != null) {
				volumeBgmMemorise[i] = bgm.volumeActuel;
				bgm.modifierVolume(0);
			}
		}
		LOG.debug("ME d�marr� : "+nom);
		musique.jouerUneSeuleFois(volumeBgmMemorise);
	}
	
	/**
	 * Jouer une musique
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 * @param piste sur laquelle jouer la musique
	 */
	public static synchronized void playBgm(final String nom, final float volume, final int piste) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.isEmpty()) {
			LOG.debug("Nom de musique vide : on ne fait rien.");
			return;
		}
		
		if (LecteurAudio.bgmEnCours[piste] != null && nom.equals(LecteurAudio.bgmEnCours[piste].nom)) {
			// meme morceau que le précédent
			if (bgmEnCours[piste].volumeActuel != volume) {
				// Modification du volume uniquement
				bgmEnCours[piste].modifierVolume(volume);
			}
		} else {
			// Morceau différent du précédent
			
			// On éteint la musique actuelle
			stopBgm(piste);
			
			// On lance la nouvelle
			final Musique musique;
			if (nom.endsWith(".ogg")) {
				musique = new MusiqueOgg(nom, Musique.TypeMusique.BGM, volume);
			} else if (nom.endsWith(".wav")) {
				musique = new MusiqueWav(nom, Musique.TypeMusique.BGM, volume);
			} else {
				//c'est sûrement encore un connard qui a oublié l'extension du fichier audio dans le JSON
				final File dossierAudio = new File(Musique.DOSSIER_AUDIO + Musique.TypeMusique.BGM.nom + "/");
				final File[] fichiersTrouves = dossierAudio.listFiles(new FilenameFilter() {
				    public boolean accept(final File dir, final String nomTotal) {
				        return nomTotal.startsWith(nom);
				    }
				});
				if (fichiersTrouves.length > 0) {
					final String vraiNom = fichiersTrouves[0].getName();
					if (vraiNom.endsWith(".ogg")) {
						musique = new MusiqueOgg(vraiNom, Musique.TypeMusique.BGM, volume);
					} else if (vraiNom.endsWith(".wav")) {
						musique = new MusiqueWav(vraiNom, Musique.TypeMusique.BGM, volume);
					} else {
						// format non géré
						LOG.error("Format audio inconnu : " + nom);
						return;
					}
					LOG.warn("Le vrai nom du fichier audio \"" + nom +"\" devrait etre \""+ vraiNom +"\"");
				} else {
					// pas de fichier trouvé
					LOG.error("Fichier audio introuvable : " + nom);
					return;
				}
			}
			LOG.debug("BGM d�marr� : "+nom);
			musique.jouerEnBoucle();
				
			// On met a jour les donnees
			LecteurAudio.bgmEnCours[piste] = musique;
		}
	}
	
	/**
	 * arreter la musique actuellement jou�e.
	 * @param piste a arreter
	 */
	public static synchronized void stopBgm(final int piste) {
		if (LecteurAudio.bgmEnCours[piste] != null) {
			LecteurAudio.bgmEnCours[piste].arreter();
			LOG.debug("arret du BGM");
			LecteurAudio.bgmEnCours[piste] = null;
		} else {
			LOG.debug("Le BGM est deja arrete.");
		}
	}

	/**
	 * Jouer un fond sonore.
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 * @param piste sur laquelle jouer
	 */
	public static synchronized void playBgs(final String nom, final float volume, final int piste) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.equals("")) {
			return;
		}
			
		// Si on est deja en train de jouer le bon fond sonore, on ne fait rien
		if (LecteurAudio.bgsEnCours[piste] == null || !nom.equals(LecteurAudio.bgsEnCours[piste].nom)) {
			// On eteint le fond sonore actuel
			stopBgs(piste);

			// On lance le nouveau
			final Musique musique;
			if (nom.endsWith(".ogg")) {
				musique = new MusiqueOgg(nom, Musique.TypeMusique.BGS, volume);
			} else if (nom.endsWith(".wav")) {
				musique = new MusiqueWav(nom, Musique.TypeMusique.BGS, volume);
			} else {
				LOG.error("Format audio inconnu : "+nom);
				return;
			}
			LOG.debug("BGS d�marr� : "+nom);
			musique.jouerEnBoucle();
				
			// On met a jour les donnees
			LecteurAudio.bgsEnCours[piste] = musique;
			
		// Modification du volume uniquement
		} else if (bgsEnCours[piste].volumeActuel != volume) {
			bgsEnCours[piste].modifierVolume(volume);
		}
	}
	
	/**
	 * arreter le fond sonore actuellement jou�.
	 * @param piste a arreter
	 */
	public static synchronized void stopBgs(final int piste) {
		if (LecteurAudio.bgsEnCours[piste] != null) {
			LecteurAudio.bgsEnCours[piste].arreter();
			LOG.debug("arret du BGS");
			LecteurAudio.bgsEnCours[piste] = null;
		} else {
			LOG.debug("Le BGS est deja arrete.");
		}
	}
	
	/**
	 * ReDemarrer toutes les Musiques du jeu apres la pause.
	 */
	public static void redemarrerToutesLesMusiques() {
		for (Musique bgm : bgmEnCours) {
			if (bgm != null) {
				bgm.reprendreApresPause();
			}
		}
		for (Musique bgs : bgsEnCours) {
			if (bgs != null) {
				bgs.reprendreApresPause();
			}
		}
	}
	
	/**
	 * Mettre toutes les Musiques du jeu en pause.
	 */
	public static void  mettreEnPauseToutesLesMusiques() {
		for (Musique bgm : bgmEnCours) {
			if (bgm != null) {
				bgm.mettreEnPause();
			}
		}
		for (Musique bgs : bgsEnCours) {
			if (bgs != null) {
				bgs.mettreEnPause();
			}
		}
	}
	
}
