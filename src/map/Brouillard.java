package map;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Le Brouillard est une image ajout�e en transparence par dessus la Map et ses Events.
 * Son int�r�t est d'enrichir l'ambiance colorim�trique du d�cor.
 */
public final class Brouillard {
	public final String nomImage;
	public BufferedImage image;
	public int largeur;
	public int hauteur;
	public int opacite;
	
	/** Il y a trois fa�ons de superposer une image � un support : 
	 * normal (moyennage), 
	 * addition des valeurs RGB des pixels, 
	 * soustraction des valeurs RGB des pixels 
	 */
	public enum ModeDeSuperposition {
		NORMAL("normal"), ADDITION("addition"), SOUSTRACTION("soustraction");
		public String nom;
		/**
		 * Constructeur explicite
		 * @param nom du mode de superposition
		 */
		ModeDeSuperposition(final String nom) {
			this.nom = nom;
		}
	};
	public final ModeDeSuperposition mode;
	
	//TODO arr�ter le d�filement en cas de StopEvent ?
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
	private Brouillard(final String nomImage, final int opacite, final ModeDeSuperposition mode, final int defilementX, final int defilementY, final long zoom) {
		this.zoom = zoom;
		this.nomImage = nomImage;
		try {
			this.image = redimensionnerImage(ImageIO.read(new File(".\\ressources\\Graphics\\Fogs\\"+this.nomImage)), zoom);
			this.largeur = this.image.getWidth();
			this.hauteur = this.image.getHeight();
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
		
	    final int ancienneLargeur  = image.getWidth();
	    final int ancienneHauteur = image.getHeight();
	    final AffineTransform scaleTransform = AffineTransform.getScaleInstance(ratio, ratio);
	    final AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
	    final int nouvelleLargeur = Math.round(ratio*ancienneLargeur);
	    final int nouvelleHauteur = Math.round(ratio*ancienneHauteur);
	    return bilinearScaleOp.filter(image, new BufferedImage(nouvelleLargeur, nouvelleHauteur, image.getType()));
	}
	
	/**
	 * Extrait le Brouillard de l'objet JSON de la Map.
	 * @param jsonMap objet JSON repr�sentant la Map
	 * @return Brouillard de la Map
	 */
	public static Brouillard creerBrouillardAPartirDeJson(final JSONObject jsonMap) {
		try {
			final JSONObject brouillardJson = jsonMap.getJSONObject("brouillard");
			final String nomImage = brouillardJson.getString("nom");
			final int opacite = brouillardJson.getInt("opacite");
			int defilementX = 0;
			try {
				defilementX = brouillardJson.getInt("defilementX");
			} catch (JSONException e) {
				//pas de d�filement x
			}
			int defilementY = 0;
			try {
				defilementY = brouillardJson.getInt("defilementY");
			} catch (JSONException e) {
				//pas de d�filement y
			}
			long zoom = 1;
			try {
				zoom = brouillardJson.getInt("zoom");
			} catch (JSONException e) {
				//pas de zoom
			}
			return new Brouillard(nomImage, opacite, ModeDeSuperposition.NORMAL, defilementX, defilementY, zoom);
		} catch (JSONException e) {
			//pas de brouillard
			System.err.println("Pas de Brouillard pour cette Map.");
			return null;
		}
	}
	
	/**
	 * Calcule la position o� dessiner l'image du Brouillard.
	 * @param numeroVignette l'�cran est recouvert plusieurs fois avec l'image du Brouillard si elle est petite
	 * @param tailleBrouillard taille de l'image du Brouillard
	 * @param decalageTemporel l'image du Brouillard se d�place � l'�cran
	 * @param positionCamera position de la cam�ra par rapport au coin haut-gauche de la Map
	 * @return position de la vignette par rapport au coin haut-gauche de l'�cran
	 */
	public static int calculerAffichage(final int numeroVignette, final int tailleBrouillard, final int decalageTemporel, final int positionCamera) {
		return numeroVignette*tailleBrouillard + decalageTemporel - positionCamera;
	}
}
