package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import commandes.AfficherImage;
import main.Fenetre;
import utilitaire.Graphismes;
import utilitaire.Graphismes.ModeDeFusion;

/**
 * Image � afficher � l'�cran et ses param�tres.
 */
public class Picture {
	private String nomImage;
	public BufferedImage image;
	public Integer numero;
	private boolean centre;
	private boolean variables;
	/** coordonn�e x d'affichage par rapport au coin de l'�cran */
	public int x;
	/** coordonn�e y d'affichage par rapport au coin de l'�cran */
	public int y;
	private int zoomX;
	private int zoomY;
	private int opacite;
	private ModeDeFusion modeDeFusion;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom du fichier image
	 * @param numero de l'image pour le LecteurMap
	 * @param centre l'origine de l'image est elle le centre de l'image ou bien son coin haut-gauche ?
	 * @param variables les coordonn�es sont stock�es dans des variables 
	 * @param x coordonn�e x d'affichage � l'�cran (en pixels)
	 * @param y coordonn�e y d'affichage � l'�cran (en pixels)
	 * @param zoomX �tirement horizontal (en pourcents)
	 * @param zoomY �tirement vertical (en pourcents)
	 * @param opacite de l'image (sur 255)
	 * @param modeDeFusion de la superposition d'images
	 * @throws IOException impossible d'ouvrir l'image
	 */
	public Picture(final String nomImage, final int numero, final boolean centre, final boolean variables, final int x, final int y, final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion) throws IOException {
		this.nomImage = nomImage;
		this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\"+this.nomImage));
		this.numero = numero;
		this.centre = centre;
		this.variables = variables;
		this.x = x;
		this.y = y;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.opacite = opacite;
		this.modeDeFusion = modeDeFusion;
	}
	
	/**
	 * Dessiner les images.
	 * @param ecran sur lequel on affiche les images
	 * @return �cran avec les images dessin�es
	 */
	public static BufferedImage dessinerLesImages(BufferedImage ecran) {
		for (Picture picture : Fenetre.getPartieActuelle().images.values()) {
			
			//coordonn�es
			int xAffichage;
			int yAffichage;
			if (picture.variables) {
				//valeurs stock�es dans des variables
				xAffichage = Fenetre.getPartieActuelle().variables[picture.x];
				yAffichage = Fenetre.getPartieActuelle().variables[picture.y];
			} else {
				//valeurs brutes
				xAffichage = picture.x;
				yAffichage = picture.y;
			}
			
			//zoom
			BufferedImage imageAAfficher = picture.image;
			if (picture.zoomX != AfficherImage.PAS_D_HOMOTHETIE || picture.zoomY != AfficherImage.PAS_D_HOMOTHETIE) {
				final int largeur = imageAAfficher.getWidth() * picture.zoomX / 100;
				final int hauteur = imageAAfficher.getHeight() * picture.zoomY / 100;
				imageAAfficher = Graphismes.redimensionner(imageAAfficher, largeur, hauteur);
			}
			
			//origine
			if (picture.centre) {
				//l'origine de l'image est son centre
				xAffichage -= imageAAfficher.getWidth()/2;
				yAffichage -= imageAAfficher.getHeight()/2;
			}
			
			//mode de fusion
			//TODO
			
			ecran = Graphismes.superposerImages(ecran, imageAAfficher, xAffichage, yAffichage, picture.opacite);
		}
		return ecran;
	}
	
}
