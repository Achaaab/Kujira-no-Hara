package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Commande;
import main.Lecteur;

/**
 * Tout El�ment constitutif d'un Menu : Image, Texte, Liste...
 * Un El�ment de Menu est �ventuellement s�lectionnable (surlignage jaune).
 */
public abstract class ElementDeMenu {
	//constantes
	public static final int CONTOUR = 16;
	private static final int COULEUR_CENTRE_SELECTION_R = 255;
	private static final int COULEUR_CENTRE_SELECTION_G = 255;
	private static final int COULEUR_CENTRE_SELECTION_B = 120;
	private static final int COULEUR_CENTRE_SELECTION_A = 175;
	private static final int COULEUR_CONTOUR_SELECTION_R = 255;
	private static final int COULEUR_CONTOUR_SELECTION_G = 150;
	private static final int COULEUR_CONTOUR_SELECTION_B = 0;
	private static final int COULEUR_CONTOUR_SELECTION_A = 0;
	
	public Menu menu;
	public int numero;
	public boolean selectionnable = true;
	public boolean selectionne = false;
	protected ArrayList<Commande> comportementSelection;
	protected ArrayList<Commande> comportementConfirmation;
	public BufferedImage image;
	public int x;
	public int y;
	public int hauteur;
	public int largeur;
	
	/**
	 * Lorsqu'on survole l'�l�ment, il peut d�clencher une action.
	 */
	public abstract void executerLeComportementALArrivee();
	
	/**
	 * Lorsqu'il est s�lectionn�, le S�lectionnable est surlign� en jaune.
	 * @return image contenant le surlignage jaune adapt� au S�lectionnable
	 */
	public final BufferedImage creerImageDeSelection() {
		int largeur;
		int hauteur;
		if (this.image!=null) {
			largeur = this.image.getWidth() + 2*Image.CONTOUR;
			hauteur = this.image.getHeight() + 2*Image.CONTOUR;
		} else {
			largeur = this.largeur + 2*Image.CONTOUR;
			hauteur = this.hauteur + 2*Image.CONTOUR;
		}
		final BufferedImage selection = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				int r, g, b, a, r1, r2, g1, g2, b1, b2, a1, a2;
				//couleur du vide
				r = 0;
				g = 0;
				b = 0;
				a = 0;
				//couleur au centre de la s�lection
				r1 = COULEUR_CENTRE_SELECTION_R;
				g1 = COULEUR_CENTRE_SELECTION_G;
				b1 = COULEUR_CENTRE_SELECTION_B;
				a1 = COULEUR_CENTRE_SELECTION_A;
				//couleur � l'ext�rieur de la s�lection
				r2 = COULEUR_CONTOUR_SELECTION_R;
				g2 = COULEUR_CONTOUR_SELECTION_G;
				b2 = COULEUR_CONTOUR_SELECTION_B;
				a2 = COULEUR_CONTOUR_SELECTION_A; 
				double rate = 0.0, hypotenuse = 0.0;
				//calcul du taux "rate" d'�loignement avec le centre de la s�lection
				if (i>=Image.CONTOUR && i<=largeur-Image.CONTOUR) {
					//centre centre
					if (j>=Image.CONTOUR && j<=hauteur-Image.CONTOUR) {
						rate = 1.0;
					}
					//centre haut
					if (j<Image.CONTOUR) {
						rate = (double) (j) / (double) (Image.CONTOUR);
					}
					//centre bas
					if (j>hauteur-Image.CONTOUR) {
						rate = (double) (hauteur-j) / (double) Image.CONTOUR;
					}
				} else {
					if (i<Image.CONTOUR) {
						//gauche centre
						if (j>=Image.CONTOUR && j<=hauteur-Image.CONTOUR) {
							rate = (double) (i) / (double) Image.CONTOUR;
						}
						//gauche haut
						if (j<Image.CONTOUR) {
							hypotenuse = Math.sqrt( Math.pow(i-Image.CONTOUR, 2) + Math.pow(j-Image.CONTOUR, 2) );
						}
						//gauche bas
						if (j>hauteur-Image.CONTOUR) {
							hypotenuse = Math.sqrt( Math.pow(i-Image.CONTOUR, 2) + Math.pow(j-(hauteur-Image.CONTOUR), 2) );
						}
					} else {
						if (i>largeur-Image.CONTOUR) {
							//droite centre
							if (j>=Image.CONTOUR && j<=hauteur-Image.CONTOUR) {
								rate = (double) (largeur-i) / (double) Image.CONTOUR;
							}
							//droite haut
							if (j<Image.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-(largeur-Image.CONTOUR), 2) + Math.pow(j-Image.CONTOUR, 2) );
							}
							//droite bas
							if (j>hauteur-Image.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-(largeur-Image.CONTOUR), 2) + Math.pow(j-(hauteur-Image.CONTOUR), 2) );
							}
						}
					}
				}
				if (hypotenuse!=0) {
					if (hypotenuse>Image.CONTOUR) {
						rate = 0;
					} else {
						rate = 1.0-hypotenuse/(double) Image.CONTOUR;
					}
				}
				//calcul de la couleur en fonction du taux "rate" d'�loignement du centre de la s�lection
				r = (int) (r1*rate+r2*(1-rate));
				g = (int) (g1*rate+g2*(1-rate));
				b = (int) (b1*rate+b2*(1-rate));
				a = (int) (a1*rate+a2*(1-rate));
				final Color couleur = new Color(r, g, b, a);
				selection.setRGB(i, j, couleur.getRGB());
			}
		}
		return selection;
	}
	
	/**
	 * Valider ce choix
	 */
	public final void confirmer() {
		if (this.comportementConfirmation != null && this.comportementConfirmation.size()>0) {
			int i = 0;
			for (Commande commande : this.comportementConfirmation) {
				i = commande.executer(i, this.comportementConfirmation);
			}
		}
	}
	
}
