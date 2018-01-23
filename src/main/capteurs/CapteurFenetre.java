package main.capteurs;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Fenetre;
import utilitaire.son.LecteurAudio;

/**
 * Actions � effectuer lorsque la Fen�tre du jeu est modifi�e.
 */
public class CapteurFenetre implements WindowFocusListener {
	private static final Logger LOG = LogManager.getLogger(CapteurFenetre.class);
	
	private Fenetre fenetre;
	
	/**
	 * Constructeur explicite
	 * @param fenetre du jeu
	 */
	public CapteurFenetre(final Fenetre fenetre) {
		this.fenetre = fenetre;
	}
	
	@Override
	public final void windowGainedFocus(final WindowEvent arg0) {
		LOG.info("Fen�tre r�activ�e");
		LecteurAudio.redemarrerToutesLesMusiques();
	}

	@Override
	public final void windowLostFocus(final WindowEvent arg0) {
		LOG.info("Fen�tre d�sactiv�e");
		LecteurAudio.mettreEnPauseToutesLesMusiques();
	}

}
