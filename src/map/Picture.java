package map;

import java.awt.image.BufferedImage;

import commandes.DeplacerImage;
import main.Fenetre;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Image � afficher � l'�cran et ses param�tres.
 */
public class Picture {
	public BufferedImage image;
	public Integer numero; //Integer car utilis� comme cl� d'une HashMap
	/** coordonn�e x d'affichage par rapport au coin de l'�cran */
	public int x;
	/** coordonn�e y d'affichage par rapport au coin de l'�cran */
	public int y;
	/** la nouvelle origine est-elle le centre de l'image ? */
	public boolean centre;
	/** zoom horizontal (en pourcents)*/
	public int zoomX;
	/** zoom vertical (en pourcents)*/
	public int zoomY;
	public int opacite;
	public ModeDeFusion modeDeFusion;
	/** angle de rotation de l'image */
	public int angle;
	/** L'image bouge-t-elle d'elle-m�me ? */
	public DeplacerImage deplacementActuel;
	
	/**
	 * Constructeur explicite
	 * @param image nom du fichier image
	 * @param numero de l'image pour le LecteurMap
	 * @param x coordonn�e x d'affichage � l'�cran (en pixels)
	 * @param y coordonn�e y d'affichage � l'�cran (en pixels)
	 * @param centre l'origine de l'image est-elle son centre ?
	 * @param zoomX zoom horizontal (en pourcents)
	 * @param zoomY zoom vertical (en pourcents)
	 * @param opacite de l'image (sur 255)
	 * @param modeDeFusion de la superposition d'images
	 * @param angle de rotation de l'image (en degr�s)
	 */
	public Picture(final BufferedImage image, final int numero, final int x, final int y, final boolean centre, 
			final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion, final int angle) {
		this.image = image;
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.centre = centre;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
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
			// D�placer les images (dont le d�placement a �t� d�l�gu� au LecteurMap par une Commande)
			if (picture.deplacementActuel != null) {
				picture.deplacementActuel.executerCommeUnDeplacementPropre(picture);
			}
			
			// Afficher les images
			ecran = Graphismes.superposerImages(ecran, picture.image, picture.x, picture.y, picture.centre, picture.zoomX, picture.zoomY, picture.opacite, picture.modeDeFusion, picture.angle);
		}
		return ecran;
	}
	
}
