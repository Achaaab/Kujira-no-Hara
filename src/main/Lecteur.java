package main;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilitaire.GestionClavier.ToucheRole;
import utilitaire.graphismes.Graphismes;
import map.PageCommune;

/**
 * Le lecteur peut �tre un lecteur de menu ou un lecteur de map.
 * Le r�le du lecteur est d'afficher dans la fen�tre la succession des �crans au cours du temps.
 */
public abstract class Lecteur {	
	protected static final Logger LOG = LogManager.getLogger(Lecteur.class);
	
	private BufferedImage ecranAtuel;
	
	/**
	 * Dur�e minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compr�hensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal � faire tourner le bousin.
	 */
	public static final int DUREE_FRAME = 30;
	
	/**
	 * Est-ce que le Lecteur est allum� ?
	 * Si le Lecteur est allum�, l'affichage de l'�cran est actualis� en continu.
	 * Si le Lecteur est �teint, l'affichage arr�te sa boucle, et la Fen�tre doit d�marrer un nouveau Lecteur.
	 */
	public boolean allume = true;
	
	/** 
	 * Num�ro de la frame en cours (en temps r�el).
	 * Une frame est une image affich�e � l'�cran pendant une courte dur�e.
	 * A chaque nouveau Lecteur, on repart de 0.
	 */
	public int frameActuelle = 0;
	
	/**
	 * Le r�le d'un Lecteur est de calculer l'�cran � afficher dans la Fen�tre.
	 * @param frame de l'�cran � calculer
	 * @return �cran � afficher maintenant
	 */
	public abstract  BufferedImage calculerAffichage(int frame);
	
	/**
	 * Pr�venir le Lecteur qu'une touche a �t� press�e, pour qu'il en d�duise une action � faire.
	 * La touche envoy�e ne dois pas �tre nulle !
	 * @param touchePressee touche press�e
	 */
	public abstract void keyPressed(ToucheRole touchePressee);
	
	/**
	 * Pr�venir le Lecteur qu'une touche a �t� relach�e, pour qu'il en d�duise une action � faire.
	 * La touche envoy�e ne dois pas �tre nulle !
	 * @param toucheRelachee touche relach�e
	 */
	public abstract void keyReleased(ToucheRole toucheRelachee);
	
	/**
	 * Faire une capture de l'�cran actuel.
	 */
	public final void faireUneCaptureDEcran() {
		final String nomImage = "capture ecran kujira "+new Date().getTime();
		LOG.info("Capture d'�cran : "+nomImage);
		Graphismes.sauvegarderImage(this.ecranAtuel, nomImage);
	}
	
	/***
	 * R�cup�rer le nom du BGM qu'il faut jouer pour accompagner le Menu ou la Map
	 */
	protected abstract void lireMusique();
	
	/**
	 * D�marrer le Lecteur.
	 * Le Lecteur est allum�, la musique est lue, un �cran est affich� � chaque frame.
	 * Si jamais "allume" est mis � false, le Lecteur s'arr�te et la Fenetre devra lancer le prochain Lecteur.
	 */
	public final void demarrer() {
		this.allume = true;
		LOG.info("-------------------------------------------------------------");
		LOG.info("Un nouveau "+this.typeDeLecteur()+" vient d'�tre d�marr�.");
		//d�marrer la Musique de la Map/du Menu
		lireMusique();
		
		long t1, t2;
		long dureeEffectiveDeLaFrame;
		while (this.allume) {
			t1 = System.currentTimeMillis();
			this.ecranAtuel = calculerAffichage(this.frameActuelle);
			t2 = System.currentTimeMillis();
			dureeEffectiveDeLaFrame = t2-t1;
			if (dureeEffectiveDeLaFrame < Lecteur.DUREE_FRAME) {
				//si l'affichage a pris moins de temps que la dur�e attendue, on attend que la frame se termine
				try {
					Thread.sleep(Lecteur.DUREE_FRAME-dureeEffectiveDeLaFrame);
				} catch (InterruptedException e) {
					LOG.error("La boucle de lecture du jeu dans Lecteur.demarrer() n'a pas r�ussi son sleep().", e);
				}
			} else {
				//l'affichage a pris trop de temps
				LOG.warn("Irrespect de la vitesse d'affichage : "+dureeEffectiveDeLaFrame+"/"+Lecteur.DUREE_FRAME);
			}
			Main.fenetre.actualiserAffichage(this.ecranAtuel);
			this.frameActuelle++;
		}
		//si on est ici, c'est qu'une Commande Event a �teint le Lecteur
		//la Fen�tre va devoir le remplacer par le futur Lecteur (si elle en a un de rechange)
		LOG.info("Le "+typeDeLecteur()+" actuel vient d'�tre arr�t� � la frame "+this.frameActuelle);
	}

	/**
	 * Quel est le type de ce Lecteur ?
	 * @return LecteurMap ou LecteurMenu
	 */
	protected abstract String typeDeLecteur();
	
}
