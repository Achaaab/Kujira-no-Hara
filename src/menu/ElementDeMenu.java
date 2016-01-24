package menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Un Menu est compos� d'El�ments de Menu
 */
public class ElementDeMenu extends Selectionnable {
	
	/**
	 * Constructeur explicite
	 * @param dossierImage nom du dossier o� se trouve l'image
	 * @param nomImage nom de l'image
	 * @param x position x � l'�cran
	 * @param y position y � l'�cran
	 * @param selectionnable peut-on le s�lectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement � la validation
	 * @param menu auquel appartient l'El�ment
	 * @throws IOException 
	 */
	public ElementDeMenu(final String dossierImage, final String nomImage, final int x, final int y, final boolean selectionnable, final ComportementElementDeMenu comportementSelection, final ComportementElementDeMenu comportementConfirmation, final Menu menu) throws IOException {
		this(ImageIO.read(new File(".\\ressources\\Graphics\\"+dossierImage+"\\"+nomImage)),
				x, y, selectionnable, comportementSelection, comportementConfirmation, menu);
	}
	
	/**
	 * Constructeur explicite
	 * @param image apparence de l'El�ment de Menu
	 * @param x position x � l'�cran
	 * @param y position y � l'�cran
	 * @param selectionnable peut-on le s�lectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement � la validation
	 * @param menu auquel appartient l'El�ment
	 */
	public ElementDeMenu(final BufferedImage image, final int x, final int y, final boolean selectionnable, final ComportementElementDeMenu comportementSelection, final ComportementElementDeMenu comportementConfirmation, final Menu menu) {
		this.menu = menu;
		
		try {
			this.image = image;
			this.largeur = this.image.getWidth();
			this.hauteur = this.image.getHeight();
		} catch (Exception e) {
			//impossible de charger l'image de fond
			this.image = null;
			this.largeur = 0;
			this.hauteur = 0;
			e.printStackTrace();
		}
		this.largeur = this.image.getWidth();
		this.hauteur = this.image.getHeight();
		this.x = x;
		this.y = y;
		this.selectionnable = selectionnable;
		this.comportementSelection = comportementSelection;
		this.comportementConfirmation = comportementConfirmation;
		
		this.selectionne = false;
	}
	
	/**
	 * Lancer le comportement de l'El�ment au survol.
	 */
	@Override
	public final void executerLeComportementALArrivee() {
		this.selectionne = true;
	}
	
	/**
	 * Lancer le comportement de l'Element � la s�lection ou � l'annulation
	 * @param keycode num�ro de la touche press�e lorsque l'El�ment est s�lectionn�
	 */
	public void comportementSiTouchePressee(final Integer keycode) {
		
	}
}
