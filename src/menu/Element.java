package menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import commandesMenu.CommandeMenu;

/**
 * Un Menu est compos� d'El�ments de Menu
 */
public class Element extends Selectionnable {
	
	/**
	 * Constructeur explicite (avec image comme apparence)
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
	public Element(final String dossierImage, final String nomImage, final int x, final int y, final boolean selectionnable, final CommandeMenu comportementSelection, final CommandeMenu comportementConfirmation, final Menu menu) throws IOException {
		this(ImageIO.read(new File(".\\ressources\\Graphics\\"+dossierImage+"\\"+nomImage)),
				x, y, selectionnable, comportementSelection, comportementConfirmation, menu);
	}
	
	/**
	 * Constructeur explicite (avec rectangle vide comme apparence)
	 * @param x position x � l'�cran
	 * @param y y position y � l'�cran
	 * @param largeur de l'El�ment
	 * @param hauteur de l'El�ment
	 * @param selectionnable peut-on le s�lectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement � la validation
	 * @param menu auquel appartient l'El�ment
	 */
	public Element(final int x, final int y, final int largeur, final int hauteur, final boolean selectionnable, final CommandeMenu comportementSelection, final CommandeMenu comportementConfirmation, final Menu menu) {
		this(new int[]{largeur, hauteur}, x, y, selectionnable, comportementSelection, comportementConfirmation, menu);
	}
	
	/**
	 * Constructeur explicite
	 * @param apparence de l'El�ment de Menu : une BufferedImage ou bien un tableau (largeur,hauteur) repr�sentant une image vide
	 * @param x position x � l'�cran
	 * @param y position y � l'�cran
	 * @param selectionnable peut-on le s�lectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement � la validation
	 * @param menu auquel appartient l'El�ment
	 */
	private Element(final Object apparence, final int x, final int y, final boolean selectionnable, final CommandeMenu comportementSelection, final CommandeMenu comportementConfirmation, final Menu menu) {
		this.menu = menu;
		
		if (apparence instanceof BufferedImage) {
			//l'El�ment a une image comme apparence
			this.image = (BufferedImage) apparence;
			this.largeur = this.image.getWidth();
			this.hauteur = this.image.getHeight();
		} else {
			//l'El�ment est un rectangle vide
			this.image = null;
			final int[] dimensionsImageVide = (int[]) apparence;
			this.largeur = dimensionsImageVide[0];
			this.hauteur = dimensionsImageVide[1];
		}
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
