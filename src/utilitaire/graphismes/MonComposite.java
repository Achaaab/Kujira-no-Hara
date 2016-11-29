package utilitaire.graphismes;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

/**
 * Un Composite est utilis� par le Graphics2D d'une image pour effectuer les superpositions d'images.
 * Il contient les deux informations n�cessaires � la superposition : le mode de fusion et l'opacit�.
 */
public final class MonComposite implements Composite {
	public final ModeDeFusion modeDeFusion;
	public final float opacite;
	
	/**
	 * G�n�rer un Composite qui effectuera la superposition telle que voulue
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @param opacite de l'image � superposer (valeur r�elle entre 0 et 1)
	 * @return composite qui effectuera la superposition telle que voulue
	 */
	public static Composite creerComposite(final ModeDeFusion modeDeFusion, final float opacite) {
		if (ModeDeFusion.NORMAL.equals(modeDeFusion)) {
			return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacite);
		} else {
			return new MonComposite(modeDeFusion, opacite);
		}
	}

	/**
	 * Constructeur explicite
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @param opacite de l'image superpos�e � l'image support
	 */
	private MonComposite(final ModeDeFusion modeDeFusion, final float opacite) {
		this.modeDeFusion = modeDeFusion;
		this.opacite = opacite;
	}

	@Override
	public CompositeContext createContext(final ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
		return new ContexteDeComposite(this);
	}

}
