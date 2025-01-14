package utilitaire.son;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import static utilitaire.ResourceUtility.getResourceUrl;

/**
 * Fichier audio au format WAV.
 */
public class MusiqueWav extends Musique {
	
	protected MusiqueWav(final String nom, final TypeMusique type, final float volume) {
		super(nom, type, volume);
		
		//le fichier est un WAV
		this.format = FormatAudio.WAV;
		try {
			this.clip = AudioSystem.getClip();
			this.stream = AudioSystem.getAudioInputStream(getResourceUrl(DOSSIER_AUDIO+type+"/" + nom));
			((Clip) clip).open((AudioInputStream) this.stream);
			//volume initial
			if (volume < VOLUME_MAXIMAL) {
				this.modifierVolume(volume);
			}
			//Duree
			if (TypeMusique.ME.equals(type)) {
				this.dureeMillisecondes = ((Clip) clip).getMicrosecondLength();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public final void modifierVolume(final float nouveauVolume) {
		final FloatControl gainControl = (FloatControl) ((Clip) this.clip).getControl(FloatControl.Type.MASTER_GAIN);
		final float gain = (1f-nouveauVolume)*Float.MIN_VALUE;
		gainControl.setValue(gain);
		
		//mettre a jour les donnees
		this.volumeActuel = nouveauVolume;
	}
	
	public final void jouerUneSeuleFois(final Float[] volumeBgmMemorise) {
		class LancerWav extends LancerSon {
			private final Clip wavClip;
			
			private LancerWav(Clip clip, TypeMusique type) {
				super(type, volumeBgmMemorise);
				this.wavClip = clip;
			}

			public void run() {
			    wavClip.start(); //jouer une seule fois
			    fermerALaFin();
			}

			@Override
			long obtenirDuree() {
				return dureeMillisecondes;
			}
		}
		final LancerWav r2 = new LancerWav((Clip) this.clip, this.type);
		this.thread = new Thread(r2, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
		if (TypeMusique.ME.equals(type)) {
			try {
				Thread.sleep(DELAI_AVANT_ME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.thread.start();
	}
	
	public final void jouerEnBoucle() {
		class RunWavBgm implements Runnable {
			/**
			 * Jouer le fichier audio.
			 */
			public void run() {
				final Clip wavClip = (Clip) clip;
				wavClip.loop(Clip.LOOP_CONTINUOUSLY); //jouer en boucle
			}
		}
		final RunWavBgm r2 = new RunWavBgm();
		this.thread = new Thread(r2, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
		this.thread.start();
	}
	
	public final void arreterSpecifique() {
		final Clip wavClip = (Clip) clip;
		wavClip.close();
	}

	@Override
	public void mettreEnPause() {
		final Clip wavClip = (Clip) clip;
		wavClip.stop();
	}

	@Override
	public void reprendreApresPause() {
		final Clip wavClip = (Clip) clip;
		wavClip.start();
	}
	
	
}
