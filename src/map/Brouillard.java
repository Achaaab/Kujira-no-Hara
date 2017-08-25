package map;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.Sauvegarder.Sauvegardable;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le Brouillard est une image ajout�e en transparence par dessus la Map et ses Events.
 * Son int�r�t est d'enrichir l'ambiance colorim�trique du d�cor.
 */
public final class Brouillard implements Sauvegardable {
	private static final Logger LOG = LogManager.getLogger(Brouillard.class);
	
	public final String nomImage;
	public BufferedImage image;
	public int largeur;
	public int hauteur;
	public int opacite;

	public final ModeDeFusion mode;
	
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
	public Brouillard(final String nomImage, final int opacite, final ModeDeFusion mode, final int defilementX, 
			final int defilementY, final long zoom) {
		this.zoom = zoom;
		this.nomImage = nomImage;
		try {
			this.image = redimensionnerImage(Graphismes.ouvrirImage("Fogs", this.nomImage), zoom);
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
			final String nomModeDeFusion = brouillardJson.has("modeDeFusion") ? brouillardJson.getString("modeDeFusion") : null;

			return new Brouillard(nomImage, opacite, ModeDeFusion.parNom(nomModeDeFusion), defilementX, defilementY, zoom);
		} catch (JSONException e) {
			//pas de brouillard
			LOG.info("Pas de Brouillard pour cette Map.");
			return null;
		}
	}
	
	/**
	 * Dessiner le Brouillard au dessus de la Map et ses Events.
	 * @param ecran sur lequel on dessine
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @param frame d'animation du Brouillard
	 * @return �cran sur lequel on a dessin� le Brouillard
	 */
	public BufferedImage dessinerLeBrouillard(BufferedImage ecran, final int xCamera, final int yCamera, final int frame) {
		if (this.image == null || this.opacite <= 0) {
			//pas de Brouillard
			return ecran;
		}
		
		final int largeurEcran = ecran.getWidth();
		final int hauteurEcran = ecran.getWidth();
		final int decalageX = this.defilementX * (frame % this.largeur);
		final int decalageY = this.defilementY * (frame % this.hauteur); 
		int imin = (xCamera - decalageX) / this.largeur;
		int imax = (xCamera + largeurEcran - decalageX) / this.largeur;
		int jmin = (yCamera - decalageY) / this.hauteur;
		int jmax = (yCamera + hauteurEcran - decalageY) / this.hauteur;
		if (Brouillard.calculerAffichage(imin, this.largeur, decalageX, xCamera) >= 0) {
			imin--;
		}
		if (Brouillard.calculerAffichage(imax, this.largeur, decalageX, xCamera) <= largeurEcran) {
			imax++;
		}
		if (Brouillard.calculerAffichage(jmin, this.hauteur, decalageY, yCamera) >= 0) {
			jmin--;
		}
		if (Brouillard.calculerAffichage(jmax, this.largeur, decalageY, yCamera) <= hauteurEcran) {
			jmax++;
		}
		for (int i = imin; i<imax; i++) {
			for (int j = jmin; j<jmax; j++) {
				ecran = Graphismes.superposerImages(
					ecran, 
					this.image, 
					Brouillard.calculerAffichage(i, this.largeur, decalageX, xCamera), 
					Brouillard.calculerAffichage(j, this.hauteur, decalageY, yCamera),
					false,
					Graphismes.PAS_D_HOMOTHETIE, //le zoom a d�j� �t� pris en compte
					Graphismes.PAS_D_HOMOTHETIE, //le zoom a d�j� �t� pris en compte
					this.opacite, 
					this.mode,
					Graphismes.PAS_DE_ROTATION
				);	
			}
		}
		return ecran;
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

	@Override
	public JSONObject sauvegarderEnJson() {
		final JSONObject jsonBrouillard = new JSONObject();
		jsonBrouillard.put("nomImage", nomImage);
		jsonBrouillard.put("opacite", opacite);
		jsonBrouillard.put("mode", mode.nom);
		jsonBrouillard.put("defilementX", defilementX);
		jsonBrouillard.put("defilementY", defilementY);
		jsonBrouillard.put("zoom", zoom);
		return jsonBrouillard;
	}

}
