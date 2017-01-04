package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Lecteur;

/**
 * Tout El�ment constitutif d'un Menu : Image, Texte, Liste...
 * Un El�ment de Menu est �ventuellement s�lectionnable (surlignage jaune).
 */
public abstract class ElementDeMenu {
	//constantes
	private static final Logger LOG = LogManager.getLogger(ElementDeMenu.class);
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
	public final int id;
	
	/** Cet ElementDeMenu est il Selectionnable ? */
	public final boolean selectionnable;
	/** Cet ElementDeMenu est il selectionn� ? */
	public boolean selectionne = false;
	/** Faut-il lire les Commandes de survol ? */
	public boolean executionDesCommandesDeSurvol = false;
	/** Commandes � executer au survol */
	protected final ArrayList<Commande> comportementSurvol;
	/** Curseur de l'execution des commandes � executer au survol */
	public int curseurComportementSurvol = 0;
	/** Faut-il lire les Commandes de confirmation ? */
	public boolean executionDesCommandesDeConfirmation = false;
	/** Commandes � executer � la confirmation */
	protected final ArrayList<Commande> comportementConfirmation;
	/** Curseur de l'execution des commandes � executer � la confirmation */
	public int curseurComportementConfirmation = 0;
	
	/** L'�l�ment de Menu peut �tre une image */
	public BufferedImage image;
	/** Surlignage de l'image lors de la S�lection */
	public BufferedImage imageDeSelection = null;
	public int x;
	public int y;
	public int hauteur;
	public int largeur;
	
	/**
	 * Constructeur pour obliger l'affectation
	 * @param id identifiant de l'ElementDeMenu
	 * @param selectionnable peut-on s�lectionner cet ElementDeMenu ?
	 * @param x position sur l'�cran
	 * @param y position sur l'�cran
	 * @param comportementSelection CommandesMenu execut�es lors du survol
	 * @param comportementConfirmation CommandesMenu execut�es lors de la confirmation
	 */
	protected ElementDeMenu(final int id, final boolean selectionnable, final int x, final int y, 
			final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation) {
		this.id = id;
		this.selectionnable = selectionnable;
		this.x = x;
		this.y = y;
		this.comportementSurvol = comportementSelection;
		this.comportementConfirmation = comportementConfirmation;
	}
	
	/**
	 * Lorsqu'il est s�lectionn�, le S�lectionnable est surlign� en jaune.
	 * @return image contenant le surlignage jaune adapt� au S�lectionnable
	 */
	public final BufferedImage creerImageDeSelection() {
		if (this.imageDeSelection == null) { //ne calculer qu'une seule fois l'image
			final int larg;
			final int haut;
			if (this.image!=null) {
				larg = this.image.getWidth() + 2*Image.CONTOUR;
				haut = this.image.getHeight() + 2*Image.CONTOUR;
			} else {
				larg = this.largeur + 2*Image.CONTOUR;
				haut = this.hauteur + 2*Image.CONTOUR;
			}
			final BufferedImage selection = new BufferedImage(larg, haut, Lecteur.TYPE_DES_IMAGES);
			for (int i = 0; i<larg; i++) {
				for (int j = 0; j<haut; j++) {
					int r, g, b, a;
					final int r1, r2, g1, g2, b1, b2, a1, a2;
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
					if (i>=Image.CONTOUR && i<=larg-Image.CONTOUR) {
						//centre centre
						if (j>=Image.CONTOUR && j<=haut-Image.CONTOUR) {
							rate = 1.0;
						}
						//centre haut
						if (j<Image.CONTOUR) {
							rate = (double) (j) / (double) (Image.CONTOUR);
						}
						//centre bas
						if (j>haut-Image.CONTOUR) {
							rate = (double) (haut-j) / (double) Image.CONTOUR;
						}
					} else {
						if (i<Image.CONTOUR) {
							//gauche centre
							if (j>=Image.CONTOUR && j<=haut-Image.CONTOUR) {
								rate = (double) (i) / (double) Image.CONTOUR;
							}
							//gauche haut
							if (j<Image.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-Image.CONTOUR, 2) + Math.pow(j-Image.CONTOUR, 2) );
							}
							//gauche bas
							if (j>haut-Image.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-Image.CONTOUR, 2) + Math.pow(j-(haut-Image.CONTOUR), 2) );
							}
						} else {
							if (i>larg-Image.CONTOUR) {
								//droite centre
								if (j>=Image.CONTOUR && j<=haut-Image.CONTOUR) {
									rate = (double) (larg-i) / (double) Image.CONTOUR;
								}
								//droite haut
								if (j<Image.CONTOUR) {
									hypotenuse = Math.sqrt( Math.pow(i-(larg-Image.CONTOUR), 2) + Math.pow(j-Image.CONTOUR, 2) );
								}
								//droite bas
								if (j>haut-Image.CONTOUR) {
									hypotenuse = Math.sqrt( Math.pow(i-(larg-Image.CONTOUR), 2) + Math.pow(j-(haut-Image.CONTOUR), 2) );
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
			this.imageDeSelection = selection;
		}
		return this.imageDeSelection;
	}

	/**
	 * Commandes de Menu � executer � la confirmation de l'ElementDeMenu.
	 */
	public void executerLesCommandesDeConfirmation() {
		LOG.info("Execution des commandes de confirmation de l'�l�ment de menu.");
		boolean commandeInstantanee = true;
		int nouvelleValeurDuCurseur;
		try {
			while (commandeInstantanee) {
				final Commande commandeActuelle = this.comportementConfirmation.get(this.curseurComportementConfirmation);
				LOG.debug("Commande de menu execut�e : "+commandeActuelle.getClass());
				nouvelleValeurDuCurseur = commandeActuelle.executer(this.curseurComportementConfirmation, this.comportementConfirmation);
				commandeInstantanee = (nouvelleValeurDuCurseur != this.curseurComportementConfirmation);
				this.curseurComportementConfirmation = nouvelleValeurDuCurseur;
			}
		} catch (IndexOutOfBoundsException e) {
			//fin de la lecture des commandes
			LOG.trace("Fin de la lecture des commandes de confirmation de l'�l�ment de menu.", e);
			this.curseurComportementConfirmation = 0;
			this.executionDesCommandesDeConfirmation = false; //ne lire qu'une seule fois
		}
	}

	/**
	 * Commandes de Menu � executer au survol de l'ElementDeMenu.
	 */
	public void executerLesCommandesDeSurvol() {
		boolean commandeInstantanee = true;
		int nouvelleValeurDuCurseur;
		try {
			while (commandeInstantanee) {
				final Commande commandeActuelle = this.comportementSurvol.get(this.curseurComportementSurvol);
				nouvelleValeurDuCurseur = commandeActuelle.executer(this.curseurComportementSurvol, this.comportementSurvol);
				commandeInstantanee = (nouvelleValeurDuCurseur != this.curseurComportementSurvol);
				this.curseurComportementSurvol = nouvelleValeurDuCurseur;
			}
		} catch (IndexOutOfBoundsException e) {
			//fin de la lecture des commandes
			LOG.trace("Fin de la lecture des commandes de survol de l'�l�ment de menu.", e);
			this.curseurComportementSurvol = 0;
			this.executionDesCommandesDeSurvol = false;
		}
	}
	
}
