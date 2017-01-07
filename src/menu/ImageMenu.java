package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import conditions.Condition;
import jeu.Objet;
import main.Commande;

/**
 * Un Menu est compos� d'Images.
 */
public class ImageMenu extends ElementDeMenu {

	/** Conditions d'affichage */
	public final ArrayList<Condition> conditions;
	
	/**
	 * Constructeur explicite
	 * @param apparence de l'El�ment de Menu : une BufferedImage ou bien un tableau (largeur,hauteur) repr�sentant une image vide
	 * @param x position x � l'�cran
	 * @param y position y � l'�cran
	 * @param largeurForcee si sp�cifi�e, l'image sera affich�e dans un rectangle invisible de telle largeur
	 * @param hauteurForcee si sp�cifi�e, l'image sera affich�e dans un rectangle invisible de telle hauteur
	 * @param conditions d'affichage
	 * @param selectionnable peut-on le s�lectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement � la validation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public ImageMenu(final BufferedImage apparence, 
			final int x, final int y, final int largeurForcee, final int hauteurForcee,
			final ArrayList<Condition> conditions, final boolean selectionnable, final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, final int id) {
		super(id, selectionnable, x, y, comportementSelection, comportementConfirmation);
		
		if (apparence instanceof BufferedImage) {
			//l'El�ment a une image comme apparence
			this.image = (BufferedImage) apparence;
			this.largeur = largeurForcee>0 ? largeurForcee : this.image.getWidth();
			this.hauteur = hauteurForcee>0 ? hauteurForcee : this.image.getHeight();
		}
		this.conditions = conditions;
		this.selectionne = false;
	}
	
	/**
	 * Constructeur explicite (bas� sur un Objet).
	 * Il faut poss�der l'Objet pour que l'Image s'affiche.
	 * L'Image est l'ic�ne de l'Objet.
	 * @param objet repr�sent� par cette Image dans le Menu
	 * @param x position x � l'�cran
	 * @param y position y � l'�cran
	 * @param largeurForcee si sp�cifi�e, l'image sera affich�e dans un rectangle invisible de telle largeur
	 * @param hauteurForcee si sp�cifi�e, l'image sera affich�e dans un rectangle invisible de telle hauteur
	 * @param selectionnable peut-on le s�lectionner ?
	 * @param id identifiant de l'ElementDeMenu
	 */
	public ImageMenu(final Objet objet, final int x, final int y, final int largeurForcee, final int hauteurForcee, final boolean selectionnable, final int id) {
		this(objet.getIcone(), x, y, largeurForcee, hauteurForcee, objet.getConditions(), selectionnable, objet.getComportementSelection(), objet.getComportementConfirmation(), id);
		for (Commande commande : comportementSurvol) {
			commande.element = this;
		}
		for (Commande commande : comportementConfirmation) {
			commande.element = this;
		}
	}
	
	@Override
	public final boolean ilFautAfficherCetElement() {
		if (this.conditions==null || this.conditions.size()<=0) {
			//pas de contrainte particuli�re sur l'affichage
			return true;
		}
		
		//on essaye toutes les Conditions
		for (Condition condition : this.conditions) {
			if (!condition.estVerifiee()) {
				return false;
			}
		}
		return true;
	}

}
