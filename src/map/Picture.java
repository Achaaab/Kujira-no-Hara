package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Fenetre;
import utilitaire.Graphismes;

/**
 * Image � afficher � l'�cran et ses param�tres.
 */
public class Picture {
	private String nomImage;
	public BufferedImage image;
	public Integer numero;
	private boolean centre;
	/** coordonn�e x d'affichage par rapport au coin de l'�cran */
	public int x;
	/** coordonn�e y d'affichage par rapport au coin de l'�cran */
	public int y;
	private int zoomX;
	private int zoomY;
	private int opacite;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom du fichier image
	 * @param numero de l'image pour le LecteurMap
	 * @param centre l'origine de l'image est elle le centre de l'image ou bien son coin haut-gauche ?
	 * @param x coordonn�e x d'affichage � l'�cran (en pixels)
	 * @param y coordonn�e y d'affichage � l'�cran (en pixels)
	 * @param zoomX �tirement horizontal (en pourcents)
	 * @param zoomY �tirement vertical (en pourcents)
	 * @param opacite de l'image (sur 255)
	 * @throws IOException impossible d'ouvrir l'image
	 */
	public Picture(final String nomImage, final int numero, final boolean centre, final int x, final int y, final int zoomX, final int zoomY, final int opacite) throws IOException {
		this.nomImage = nomImage;
		this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\"+this.nomImage));
		this.numero = numero;
		this.centre = centre;
		this.x = x;
		this.y = y;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.opacite = opacite;
	}
	
	/**
	 * Dessiner les images.
	 * @param ecran sur lequel on affiche les images
	 * @return �cran avec les images dessin�es
	 */
	public static BufferedImage dessinerLesImages(BufferedImage ecran) {
		for (Picture picture : Fenetre.getPartieActuelle().images.values()) {
			//TODO utiliser les autres param�tres de picture
			
			int xAffichage = picture.x;
			int yAffichage = picture.y;
			if (picture.centre) {
				xAffichage -= picture.image.getWidth()/2;
				yAffichage -= picture.image.getHeight()/2;
			}
			ecran = Graphismes.superposerImages(ecran, picture.image, xAffichage, yAffichage, picture.opacite);
		}
		return ecran;
	}
	
}
