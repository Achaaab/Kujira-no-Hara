package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import son.LecteurAudio;
import map.LecteurMap;
import menu.LecteurMenu;

/**
 * Le lecteur peut �tre un lecteur de menu ou un lecteur de map.
 * Le r�le du lecteur est d'afficher dans la fen�tre la succession des �crans au cours du temps.
 */
public abstract class Lecteur {
	public BufferedImage ecranAtuel;
	public Fenetre fenetre = null;
	/**
	 * Dur�e minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compr�hensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal � faire tourner le bousin.
	 */
	private static final long DUREE_FRAME = 30;
	public static final int TYPE_DES_IMAGES = BufferedImage.TYPE_INT_ARGB;
	/**
	 * Est-ce que le lecteur est allum� ?
	 * Si le lecteur est allum�, l'affichage est actualis� sans cesse.
	 * Si le lecteur est �teint, l'affichage arr�te sa boucle, et la fen�tre attend un nouveau lecteur.
	 */
	public Boolean allume = true;
	public int frameActuelle = 0;

	/**
	 * Le r�le d'un Lecteur est de calculer l'�cran � afficher dans la Fen�tre.
	 * @return �cran � afficher maintenant
	 */
	public abstract  BufferedImage calculerAffichage();
	
	/**
	 * Pr�venir le Lecteur qu'une touche a �t� press�e, pour qu'il en d�duise une action � faire.
	 * @param keycode num�ro de la touche press�e
	 */
	public abstract void keyPressed(int keycode);
	
	/**
	 * Pr�venir le Lecteur qu'une touche a �t� relach�e, pour qu'il en d�duise une action � faire.
	 * @param keycode num�ro de la touche relach�e
	 */
	public abstract void keyReleased(Integer keycode);
	
	/**
	 * Produire un rectangle noir pour l'afficher comme �cran
	 * @return un rectangle noir
	 */
	public final BufferedImage ecranNoir() {
		int largeur = Fenetre.LARGEUR_ECRAN;
		int hauteur = Fenetre.HAUTEUR_ECRAN;
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(Color.black);
		graphics.fillRect(0, 0, largeur, hauteur);
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
		Color couleur = new Color(0, 0, 0, 0);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(couleur);
		graphics.fillRect(0, 0, largeur, hauteur);
		return image;
	}
	
	/**
	 * Enregistrer une image dans l'ordinateur
	 * @param image � enregistrer
	 */
	public static void sauvegarderImage(final BufferedImage image) {
		try {
			File outputfile = new File("C:/Users/RoiOfTheSuisse/Pictures/saved.png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superpos�e sur l'�cran
	 * @param x position x o� on superpose l'image2
	 * @param y position y o� on superpose l'image2
	 * @return �cran sur lequel on a superpos� l'image2
	 */
	public final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y) {
		BufferedImage image1 = ecran;
		int largeur = image1.getWidth();
		int hauteur = image1.getHeight();
		BufferedImage image3 = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		Graphics2D g2d = image3.createGraphics();
		g2d.drawImage(image1, null, 0, 0);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g2d.drawImage(image2, null, x, y);
		g2d.dispose();
		return image3;
	}
	
	/***
	 * R�cup�rer le nom du BGM qu'il faut jouer pour accompagner le Manu ou la Map
	 * @return nom du BGM � jouer
	 */
	public final String getNomBgm() {
		if (this instanceof LecteurMap) {
			return ((LecteurMap) this).map.nomBGM;
		} else if (this instanceof LecteurMenu) {
			return ((LecteurMenu) this).menu.nomBGM;
		}
		return null;
	}
	
	/**
	 * D�marrer le Lecteur.
	 * Le Lecteur est allum�, la musique est lue, un �cran est affich� � chaque frame.
	 */
	public final void demarrer() {
		this.allume = true;
		String typeLecteur = this.getClass().getName().equals(LecteurMap.class.getName()) ? "LecteurMap" : "LecteurMenu";
		System.out.println("Un nouveau "+typeLecteur+" vient d'�tre d�marr�.");
		LecteurAudio.playBgm(getNomBgm(), 1.0f);
		while (this.allume) {
			Date d1 = new Date();
			this.ecranAtuel = calculerAffichage();
			Date d2 = new Date();
			long dureeEffectiveDeLaFrame = d2.getTime()-d1.getTime();
			if (dureeEffectiveDeLaFrame < Lecteur.DUREE_FRAME) {
				//si l'affichage a pris moins de temps que la dur�e attendue, on attend que la frame se termine
				try {
					Thread.sleep(Lecteur.DUREE_FRAME-dureeEffectiveDeLaFrame);
				} catch (InterruptedException e) {
					System.out.println("La boucle de lecture du jeu dans Lecteur.demarrer() fait de la merde.");
					e.printStackTrace();
				}
			}
			this.fenetre.actualiserAffichage(this.ecranAtuel);
			this.frameActuelle++;
			//System.out.println("dureeEffectiveDeLaFrame : " + dureeEffectiveDeLaFrame);
		}
		System.out.println("Le "+typeLecteur+" actuel vient d'�tre arr�t� � la frame "+this.frameActuelle);
	}
	
}
