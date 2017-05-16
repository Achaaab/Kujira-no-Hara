package utilitaire.graphismes;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Fenetre;
import utilitaire.Maths;

/**
 * CLasse utilitaire pour les op�rations graphiques r�currentes.
 */
public abstract class Graphismes {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Graphismes.class);
	/** Mod�le de couleur unique pour toutes les images du jeu */
	public static final ColorModel COLORMODEL = new BufferedImage(1, 1, Graphismes.TYPE_DES_IMAGES).getColorModel();
	/** Type unique pour toutes les images du jeu */
	public static final int TYPE_DES_IMAGES = BufferedImage.TYPE_INT_ARGB;
	
	/** Valeur (en pourcents) repr�sentant l'absence d'homoth�tie */
	public static final int PAS_D_HOMOTHETIE = 100;
	/** Valeur (en degr�s) repr�sentant l'absence de rotation */
	public static final int PAS_DE_ROTATION = 0;
	/** Valeur (sur 255) repr�sentant l'absence de transparence */
	public static final int OPACITE_MAXIMALE = 255;
	/** L'origine de l'image est son coin haut-gauche et non son centre */
	private static final boolean ORIGINE_HAUT_GAUCHE = false;
	public static Graphics2D graphismes;

	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superpos�e sur l'�cran
	 * @param x position x o� on superpose l'image2
	 * @param y position y o� on superpose l'image2
	 * @return �cran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y) {
		return superposerImages(ecran, image2, x, y, OPACITE_MAXIMALE);
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superpos�e sur l'�cran
	 * @param x position x o� on superpose l'image2
	 * @param y position y o� on superpose l'image2
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @return �cran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y, final int opacite) {
		return superposerImages(ecran, image2, x, y, opacite, ModeDeFusion.NORMAL);
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superpos�e sur l'�cran
	 * @param x position x o� on superpose l'image2
	 * @param y position y o� on superpose l'image2
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @return �cran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y, final int opacite, final ModeDeFusion modeDeFusion) {
		return superposerImages(ecran, image2, x, y, ORIGINE_HAUT_GAUCHE, PAS_D_HOMOTHETIE, PAS_D_HOMOTHETIE, opacite, modeDeFusion, PAS_DE_ROTATION);
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superpos�e sur l'�cran
	 * @param x position x o� on superpose l'image2
	 * @param y position y o� on superpose l'image2
	 * @param centre l'origine de l'image est-elle son centre ?
	 * @param zoomX zoom horizontal (en pourcents)
	 * @param zoomY zoom vertical (en pourcents)
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @param angle de rotation de l'image
	 * @return �cran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, BufferedImage image2, int x, int y, 
			final boolean centre, final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion, 
			final int angle) {
		final Graphics2D g2d = (Graphics2D) ecran.createGraphics();
		
		if (opacite <= 0) {
			return ecran;
		}
		
		//transparence et mode de fusion
		if (opacite < OPACITE_MAXIMALE || !ModeDeFusion.NORMAL.equals(modeDeFusion)) {
			final float alpha = (float) opacite/OPACITE_MAXIMALE;
	        final Composite comp = MonComposite.creerComposite(modeDeFusion, alpha);
			g2d.setComposite(comp);
		}
		
		//zoom
		if (zoomX != Graphismes.PAS_D_HOMOTHETIE || zoomY != Graphismes.PAS_D_HOMOTHETIE) {
			final int largeur = image2.getWidth() * zoomX / 100;
			final int hauteur = image2.getHeight() * zoomY / 100;
			image2 = Graphismes.redimensionner(image2, largeur, hauteur);
		}
		//origine
		if (centre) {
			//l'origine de l'image est son centre
			x -= image2.getWidth()/2;
			y -= image2.getHeight()/2;
		}
		
		if (angle == PAS_DE_ROTATION) {
			g2d.drawImage(image2, null, x, y);
		} else {
			//rotation de l'image
			final double angleRadians = Math.toRadians(angle);
			
			// La translation avant la rotation n'est pas forc�ment la m�me apr�s la rotation, car la taille de l'image a chang�
			final double preTranslationX = image2.getWidth() / 2;
			final double preTranslationY = image2.getHeight() / 2;
			
			// On pivote les coins de l'image pour conna�tre la nouvelle largeur/hauteur
			final int[][] coins = new int[][]{{x, y}, 
				{x+image2.getWidth(), y}, 
				{x, y+image2.getHeight()}, 
				{x+image2.getWidth(), y+image2.getHeight()}};
				final int[][] coinsPivotes = new int[4][2];
			for (int i = 0; i<4; i++) {
				coinsPivotes[i][0] = (int) (coins[i][0]*Math.cos(angleRadians)-coins[i][1]*Math.sin(angleRadians));
				coinsPivotes[i][1] = (int) (coins[i][0]*Math.sin(angleRadians)-coins[i][1]*Math.cos(angleRadians));
			}
			final int nouvelleLargeur = Maths.max(coinsPivotes[0][0], coinsPivotes[1][0], coinsPivotes[2][0], coinsPivotes[3][0])
					- Maths.min(coinsPivotes[0][0], coinsPivotes[1][0], coinsPivotes[2][0], coinsPivotes[3][0]);
			final int nouvelleHauteur = Maths.max(coinsPivotes[0][1], coinsPivotes[1][1], coinsPivotes[2][1], coinsPivotes[3][1])
					- Maths.min(coinsPivotes[0][1], coinsPivotes[1][1], coinsPivotes[2][1], coinsPivotes[3][1]);
			final int postTranslationX = nouvelleLargeur/2;
			final int postTranslationY = nouvelleHauteur/2;
			
			// Rotation de l'image
			final AffineTransform tx = new AffineTransform();
			tx.translate(postTranslationX, postTranslationY); // �tape 3 : on la red�cale sur son coin
			tx.rotate(angleRadians); // �tape 2 : on la tourne
			tx.translate(-preTranslationX, -preTranslationY); // �tape 1 : on d�cale l'image sur son centre
			final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			final BufferedImage imagePivotee = op.filter(image2, null);
			final int nouveauX = x + (int) preTranslationX - postTranslationX;
			final int nouveauY = y + (int) preTranslationY - postTranslationY;
			g2d.drawImage(imagePivotee, nouveauX, nouveauY, null);
		}
		g2d.dispose();
		
		return ecran;
	}
	
	/**
	 * Produire un rectangle de couleur pour l'afficher comme �cran
	 * @return un rectangle de couleur
	 */
	public static BufferedImage ecranColore(Color couleur) {
		BufferedImage image = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(couleur);
		g2d.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		g2d.dispose();
		return image;
	}
	
	/**
	 * Produire un rectangle vide
	 * @param largeur du rectangle
	 * @param hauteur du rectangle
	 * @return un rectangle sans couleur
	 */
	public static BufferedImage imageVide(final int largeur, final int hauteur) {
		BufferedImage image = new BufferedImage(largeur, hauteur, Graphismes.TYPE_DES_IMAGES);
		final Color couleur = new Color(0, 0, 0, 0);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(couleur);
		g2d.fillRect(0, 0, largeur, hauteur);
		g2d.dispose();
		return image;
	}
	
	/**
	 * Produire un rectangle vide pour l'afficher comme �cran
	 * @return un rectangle vide
	 */
	public static BufferedImage ecranVide() {
		return imageVide(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
	}
	
	/**
	 * Cloner une image mod�le.
	 * @param image � cloner
	 * @return clone de l'image
	 */
	public static BufferedImage creerUneImageVideDeMemeTaille(final BufferedImage image) {
		final BufferedImage cloneVide = new BufferedImage(
				image.getWidth(),
				image.getHeight(),
				Graphismes.TYPE_DES_IMAGES
		);
		return cloneVide;
	}
	
	/**
	 * Cloner une image mod�le.
	 * @param image � cloner
	 * @return clone de l'image
	 */
	public static BufferedImage clonerUneImage(final BufferedImage image) {
		BufferedImage clone = creerUneImageVideDeMemeTaille(image);
		
		// Ajout de l'image de bo�te de dialogue
		clone = Graphismes.superposerImages(clone, image, 0, 0);
		return clone;
	}
	
	/**
	 * Enregistrer une image dans l'ordinateur
	 * @param image � enregistrer
	 * @param nom de l'image enregistr�e
	 */
	public static void sauvegarderImage(final BufferedImage image, final String nom) {
		final File directory = new File(String.valueOf("C:/Users/Public/kujira"));
		if (!directory.exists()) {
			directory.mkdir();
		}
		
		try {
			final File outputfile = new File("C:/Users/Public/kujira/"+nom+".png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redimensionner une image.
	 * @param image � redimensionner
	 * @param largeur de la nouvelle image
	 * @param hauteur de la nouvelle image
	 * @return image redimensionn�e
	 */
	public static BufferedImage redimensionner(final BufferedImage image, final int largeur, final int hauteur) {
		final Image tmp = image.getScaledInstance(largeur, hauteur, Image.SCALE_SMOOTH);
	    final BufferedImage imageRedimensionnee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);

	    final Graphics2D g2d = imageRedimensionnee.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return imageRedimensionnee;
	}

    /**
     * Charger une image du dossier de ressources.
     * @param dossier sous-dossier du dossier Picture o� se trouve l'image
     * @param nom de l'image
     * @return image charg�e, compatible avec la configuration officielle
     * @throws IOException impossible d'ouvrir l'image
     */
    public static BufferedImage ouvrirImage(final String dossier, final String nom) throws IOException {
    	final String dossierSlashNom = dossier+"/"+nom;
    	try {
			return convertirEnImageCompatible(ImageIO.read(new File("./ressources/Graphics/"+dossierSlashNom)), dossierSlashNom);
		} catch (IOException e) {
			if (nom!=null && !nom.equals("")) {
				LOG.error("Impossible d'ouvrir l'image : "+dossierSlashNom);
				e.printStackTrace();
			} else {
				LOG.warn("Pas d'image pour ce "+dossier);
			}
			throw e;
		}
    }
    
    /**
	 * Convertir une image qui n'est pas dans la bonne configuration.
	 * @param image dans une autre configuration
	 * @param nomImage � loguer en cas d'erreur
	 * @return image dans la configuration officielle
	 */
    private static BufferedImage convertirEnImageCompatible(final BufferedImage image, final String nomImage) {
        if (image.getColorModel().equals(COLORMODEL)) {
            return image;
        }
        LOG.debug("Conversion de l'image \""+nomImage+"\" car elle n'a pas le ColorModel standard.");
        final BufferedImage compatibleImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_DES_IMAGES);
        final Graphics2D g = compatibleImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return compatibleImage;
    }

}
