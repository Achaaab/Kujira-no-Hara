package map;

import java.awt.image.BufferedImage;

import main.Fenetre;
import utilitaire.Graphismes;
import utilitaire.Graphismes.ModeDeFusion;

/**
 * Image � afficher � l'�cran et ses param�tres.
 */
public class Picture {
	public BufferedImage image;
	public Integer numero;
	/** coordonn�e x d'affichage par rapport au coin de l'�cran */
	public int x;
	/** coordonn�e y d'affichage par rapport au coin de l'�cran */
	public int y;
	private int opacite;
	private ModeDeFusion modeDeFusion;
	/** angle de rotation de l'image */
	private int angle;
	
	/**
	 * Constructeur explicite
	 * @param image nom du fichier image
	 * @param numero de l'image pour le LecteurMap
	 * @param x coordonn�e x d'affichage � l'�cran (en pixels)
	 * @param y coordonn�e y d'affichage � l'�cran (en pixels)
	 * @param opacite de l'image (sur 255)
	 * @param modeDeFusion de la superposition d'images
	 * @param angle de rotation de l'image (en degr�s)
	 */
	public Picture(final BufferedImage image, final int numero, final int x, final int y, final int opacite, final ModeDeFusion modeDeFusion, final int angle) {
		this.image = image;
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.opacite = opacite;
		this.modeDeFusion = modeDeFusion;
		this.angle = angle;
	}
	
	/**
	 * Dessiner les images.
	 * @param ecran sur lequel on affiche les images
	 * @return �cran avec les images dessin�es
	 */
	public static BufferedImage dessinerLesImages(BufferedImage ecran) {
		for (Picture picture : Fenetre.getPartieActuelle().images.values()) {
			ecran = Graphismes.superposerImages(ecran, picture.image, picture.x, picture.y, picture.opacite, picture.angle);
		}
		return ecran;
	}
	
}
