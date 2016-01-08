package menu;

import java.awt.image.BufferedImage;

/**
 * Un Menu est compos� d'El�ments de Menu
 */
public class ElementDeMenu extends Selectionnable {
	
	/**
	 * Constructeur explicite
	 * @param image l'El�ment de Menu peut �tre une image
	 * @param x position x � l'�cran
	 * @param y position y � l'�cran
	 * @param selectionnable peut-on le s�lectionner ?
	 * @param menu auquel appartient l'El�ment
	 */
	public ElementDeMenu(final BufferedImage image, final int x, final int y, final Boolean selectionnable, final Menu menu) {
		this.menu = menu;
		this.image = image;
		this.x = x;
		this.y = y;
		this.selectionnable = selectionnable;
		this.selectionne = false;
	}
	
	/**
	 * Lancer le comportement de l'El�ment au survol
	 */
	@Override
	public void executerLeComportementALArrivee() {
		this.selectionne = true;
	}
	
	/**
	 * Lancer le comportement de l'Element � la s�lection ou � l'annulation
	 * @param keycode num�ro de la touche press�e lorsque l'El�ment est s�lectionn�
	 */
	public void comportementSiTouchePressee(final Integer keycode) {
		
	}
}
