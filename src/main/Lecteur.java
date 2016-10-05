package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import son.LecteurAudio;
import utilitaire.GestionClavier.ToucheRole;
import map.LecteurMap;
import menu.LecteurMenu;

/**
 * Le lecteur peut �tre un lecteur de menu ou un lecteur de map.
 * Le r�le du lecteur est d'afficher dans la fen�tre la succession des �crans au cours du temps.
 */
public abstract class Lecteur {	
	private BufferedImage ecranAtuel;
	public Fenetre fenetre = null;
	
	/**
	 * Dur�e minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compr�hensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal � faire tourner le bousin.
	 */
	private static final long DUREE_FRAME = 30;
	
	public static final int TYPE_DES_IMAGES = BufferedImage.TYPE_INT_ARGB;
	
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
	 * Afficher ou masquer la bo�te des Messages.
	 */
	public static boolean afficherBoiteMessage = true;
	
	/**
	 * Le r�le d'un Lecteur est de calculer l'�cran � afficher dans la Fen�tre.
	 * @param frame de l'�cran � calculer
	 * @return �cran � afficher maintenant
	 */
	public abstract  BufferedImage calculerAffichage(final int frame);
	
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
	 * Produire un rectangle noir pour l'afficher comme �cran
	 * @return un rectangle noir
	 */
	public final BufferedImage ecranNoir() {
		BufferedImage image = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Lecteur.TYPE_DES_IMAGES);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(Color.black);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		return image;
	}
	
	/**
	 * Produire un rectangle vide
	 * @param largeur du rectangle
	 * @param hauteur du rectangle
	 * @return un rectangle sans couleur
	 */
	public final BufferedImage imageVide(final int largeur, final int hauteur) {
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		final Color couleur = new Color(0, 0, 0, 0);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(couleur);
		graphics.fillRect(0, 0, largeur, hauteur);
		return image;
	}
	
	/**
	 * Produire un rectangle vide pour l'afficher comme �cran
	 * @return un rectangle vide
	 */
	public final BufferedImage ecranVide() {
		return imageVide(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
	}
	
	/***
	 * R�cup�rer le nom du BGM qu'il faut jouer pour accompagner le Manu ou la Map
	 * @return nom du BGM � jouer
	 */
	public final String getNomBgm() {
		if (this instanceof LecteurMap) {
			return ((LecteurMap) this).map.nomBGM;
		} else if (this instanceof LecteurMenu) {
			if (((LecteurMenu) this).menu==null) {
				System.err.println("Le menu est null pour le lecteur");
			}
			return ((LecteurMenu) this).menu.nomBGM;
		}
		return null;
	}
	
	/**
	 * D�marrer le Lecteur.
	 * Le Lecteur est allum�, la musique est lue, un �cran est affich� � chaque frame.
	 * Si jamais "allume" est mis � false, le Lecteur s'arr�te et la Fenetre devra lancer le prochain Lecteur.
	 */
	public final void demarrer() {
		this.allume = true;
		final String typeLecteur = this.getClass().getName().equals(LecteurMap.class.getName()) ? "LecteurMap" : "LecteurMenu";
		System.out.println("-------------------------------------------------------------");
		System.out.println("Un nouveau "+typeLecteur+" vient d'�tre d�marr�.");
		LecteurAudio.playBgm(getNomBgm(), 0.5f);
		//TODO LecteurAudio.playBgs(getNomBgs(), 1.0f);
		
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
					System.err.println("La boucle de lecture du jeu dans Lecteur.demarrer() n'a pas r�ussi son sleep().");
					e.printStackTrace();
				}
			}
			this.fenetre.actualiserAffichage(this.ecranAtuel);
			this.frameActuelle++;
		}
		//si on est ici, c'est qu'une Commande Event a �teint le Lecteur
		//la Fen�tre va devoir le remplacer par le futur Lecteur (si elle en a un de rechange)
		System.out.println("Le "+typeLecteur+" actuel vient d'�tre arr�t� � la frame "+this.frameActuelle);
	}
	
}
