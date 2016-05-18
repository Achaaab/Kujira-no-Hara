package map;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Le Brouillard est une image ajout�e en transparence par dessus la Map et ses Events.
 * Son int�r�t est d'enrichir l'ambiance colorim�trique du d�cor.
 */
public class Brouillard {
	public final String nomImage;
	public BufferedImage image;
	public int opacite;
	
	/** Il y a trois fa�ons de superposer une image � un support : 
	 * normal (moyennage), 
	 * addition des valeurs RGB des pixels, 
	 * soustraction des valeurs RGB des pixels 
	 */
	public enum ModeDeSuperposition {
		NORMAL, ADDITION, SOUSTRACTION
	};
	
	public final ModeDeSuperposition mode;
	public final int defilementX;
	public final int defilementY;
	public final long zoom;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom de l'image situ�e dans le dossier "Graphics/Fogs/"
	 * @param opacite transparence de l'image
	 * @param mode mode de superposition de l'image avec la Map
	 * @param defilementX vitesse de d�placement du Brouillard suivant l'axe x
	 * @param defilementY vitesse de d�placement du Brouillard suivant l'axe y
	 * @param zoom taux d'aggrandissement de l'image (en pourcents)
	 * @throws IOException l'image n'a pas pu �tre charg�e
	 */
	public Brouillard(final String nomImage, final int opacite, final ModeDeSuperposition mode, final int defilementX, final int defilementY, final long zoom) {
		this.zoom = zoom;
		this.nomImage = nomImage;
		try {
			this.image = redimensionnerImage(ImageIO.read(new File(".\\ressources\\Graphics\\Fogs\\"+this.nomImage)), zoom);
		} catch (IOException e) {
			e.printStackTrace();
			this.image = null;
		}
		this.opacite = opacite;
		this.mode = mode;
		this.defilementX = defilementX;
		this.defilementY = defilementY;
	}
	
	/**
	 * Redimensionne une image selon un ratio.
	 * @param image � redimensionner
	 * @param ratio d'aggrandissement
	 * @return image redimensionn�e
	 */
	private static BufferedImage redimensionnerImage(final BufferedImage image, final long ratio) {
		if (ratio == 1) {
			//pas de redimensionnement � faire
			return image;
		}
		
	    int ancienneLargeur  = image.getWidth();
	    int ancienneHauteur = image.getHeight();
	    AffineTransform scaleTransform = AffineTransform.getScaleInstance(ratio, ratio);
	    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
	    int nouvelleLargeur = Math.round(ratio*ancienneLargeur);
	    int nouvelleHauteur = Math.round(ratio*ancienneHauteur);
	    return bilinearScaleOp.filter(image, new BufferedImage(nouvelleLargeur, nouvelleHauteur, image.getType()));
	}
}
