package menu;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Objet;
import map.Event.Direction;

/**
 * Une Liste est un tableau d'ElementsDeMenu � plusieurs lignes et colonnes.
 * 
 * @param <T> est un collectable du jeu susceptible d'�tre list�.
 */
public class Liste<T extends Listable> {
	private static final Logger LOG = LogManager.getLogger(Liste.class);
	
	/** Position x de la Liste */
	private final int x;
	/** Position y de la Liste */
	private final int y;
	/** Nombre de colonnes du tableau bidimensionnel affich� */
	private final int nombreDeColonnes;
	/** Nombre de lignes visibles � l'�cran � la fois */
	private final int nombreDeLignesVisibles;
	/** Combien de lignes dans le tableau en tout ? */
	private final int nombreDeLignesTotal;
	/** largeur (en pixels) maximale pour l'image d'un des ElementsDeMenu de la Liste */
	private int largeurMaximaleElement;
	/** hauteur (en pixels) maximale pour l'image d'un des ElementsDeMenu de la Liste */
	private int hauteurMaximaleElement;
	
	/** ligne dans la Liste de l'ElementDeMenu s�lectionn� */
	private int iElementSelectionne;
	/** colonne dans la Liste de l'ElementDeMenu s�lectionn� */
	private int jElementSelectionne;
	/** premi�re ligne visibles � l'�cran */
	private int premiereLigneVisible = 0;
	/** ElementsDeMenu de la Liste */
	public ArrayList<ImageMenu> elements;
	/** ElementsDeMenu visibles de la Liste */
	public ImageMenu[][] elementsAffiches;
	
	/**
	 * Constructeur explicite
	 * @param x position x de la Liste dans le Menu
	 * @param y position y de la Liste dans le Menu
	 * @param nombreDeColonnes nombre de colonnes du tableau
	 * @param nombreDeLignesVisibles nombre de lignes visibles simultan�ment � l'�cran
	 * @param provenance quel est la nature du Listable � afficher ?
	 * @param possedes n'affiche-t-on que les Listables poss�d�s par le joueur ?
	 * @param avec liste exhaustive des num�ros des Listables � afficher
	 * @param toutSauf liste exhaustive des num�ros des Listables � ne pas afficher
	 * @param informations � afficher pour chaque ElementDeMenu
	 */
	public Liste(final int x, final int y, final int nombreDeColonnes, final int nombreDeLignesVisibles,
			final Class<T> provenance, final boolean possedes, final ArrayList<Integer> avec, 
			final ArrayList<Integer> toutSauf, final ArrayList<String> informations) {
		this.x = x;
		this.y = y;
		this.nombreDeColonnes = nombreDeColonnes;
		this.nombreDeLignesVisibles = nombreDeLignesVisibles;
		
		this.elements = construireLesElements(provenance, possedes, avec, toutSauf, informations);
		this.nombreDeLignesTotal = this.elements.size() / this.nombreDeColonnes + (this.elements.size() % this.nombreDeColonnes != 0 ? 1 : 0);
		
		// On remplit le tableau bidimensionnel avec le contenu de la liste
		this.elementsAffiches = new ImageMenu[this.nombreDeLignesVisibles][this.nombreDeColonnes];
		final int taille = elements.size();
		int iElement, jElement;
		ImageMenu element;
		for (int n = 0; n<taille; n++) {
			element = this.elements.get(n);
			element.liste = this;
			iElement = n / this.nombreDeColonnes;
			jElement = n % this.nombreDeColonnes;
			if (iElement < this.nombreDeLignesVisibles) {
				this.elementsAffiches[iElement][jElement] = element;
			} else {
				element.invisible = true;
			}
			
			if (element.image.getWidth() > this.largeurMaximaleElement) {
				this.largeurMaximaleElement = element.image.getWidth();
			}
			if (element.image.getHeight() > this.hauteurMaximaleElement) {
				this.hauteurMaximaleElement = element.image.getHeight();
			}
		}
		for (int i = 0; i<this.nombreDeLignesVisibles; i++) {
			for (int j = 0; j<this.nombreDeColonnes; j++) {
				if (i * this.nombreDeColonnes + j < taille) {
					element = this.elementsAffiches[i][j];
					element.x = this.x + (this.largeurMaximaleElement+Texte.MARGE_A_DROITE) * j;
					element.y = this.y + (this.hauteurMaximaleElement+Texte.INTERLIGNE) * (i - this.premiereLigneVisible);
				}
			}
		}
	}
	
	/**
	 * G�n�rer les ElementsDeMenu qui figureront dans la Liste.
	 * @param provenance nature des collectables � lister
	 * @param possedes consid�rer seulement les Listables poss�d�s ou non
	 * @param avec identifiants des collectables � inclure
	 * @param toutSauf identifiants des collectables � ne pas inclure
	 * @param informations quelles informations afficher sur le collectable ?
	 * @return ElementsDeMenu de la Liste
	 */
	private ArrayList<ImageMenu> construireLesElements(final Class<T> provenance, final Boolean possedes, 
			final ArrayList<Integer> avec, final ArrayList<Integer> toutSauf, final ArrayList<String> informations) {
		
		final ArrayList<ImageMenu> elements = new ArrayList<ImageMenu>();
		try {
			final Method obtenirTousLesListables = provenance.getMethod("obtenirTousLesListables", Boolean.class);
			@SuppressWarnings("unchecked")
			final Map<Integer, Listable> tousLesListables = 
					(Map<Integer, Listable>) obtenirTousLesListables.invoke(Objet.objetsDuJeu[0], (Object) possedes);
		
			// Recensement des num�ros des Listables � consid�rer
			final ArrayList<Integer> numerosDesListables;
			if (avec != null && avec.size()>0) {
				// liste blanche
				numerosDesListables = avec;
			} else {
				// liste noire
				numerosDesListables = new ArrayList<Integer>();
				numerosDesListables.addAll(tousLesListables.keySet());
				if (toutSauf != null && toutSauf.size()>0) {
					for (Integer numeroARetirer : toutSauf) {
						numerosDesListables.remove(numeroARetirer);
					}
				}
			}

			// Cr�er un ElementDeMenu pour chaque num�ro
			Listable listable;
			BufferedImage image;
			ImageMenu element;
			for (Integer numero : numerosDesListables) {
				listable = tousLesListables.get(numero);
				image = listable.construireImagePourListe(informations);
				element = new ImageMenu(
						image, //apparence
						0, 0, //coordonn�es (en pixel) temporaires
						-1, -1, //largeur/hauteur forc�es
						null, //conditions
						true, //s�lectionnable
						listable.getComportementSelection(), //comportement au survol
						listable.getComportementConfirmation(), //comportement � la confirmation
						-1 //id temporaire de l'ElementDeMenu
				);
				elements.add(element);
			}
		
		} catch (NoSuchMethodException e) {
			LOG.error("M�thode non trouv�e pour obtenir les Listables.", e);
		} catch (SecurityException e) {
			LOG.error("Probl�me de s�curit� ! Tous aux abris !", e);
		} catch (IllegalAccessException e) {
			LOG.error("Acc�s incorrect � la m�thode d'un Listable.", e);
		} catch (IllegalArgumentException e) {
			LOG.error("Arguments incorrects pour la m�thode d'un Listable.", e);
		} catch (InvocationTargetException e) {
			LOG.error("Impossible de joindre ce type de Listable.", e);
		}
		
		return elements;
	}

	/**
	 * Chercher un autre ElementDeMenu � s�lectionner dans la Liste.
	 * @param direction dans laquelle chercher
	 * @return ElementDeMenu � s�lectionner, ou null si bord de Liste
	 */
	public ElementDeMenu selectionnerUnAutreElementDansLaListe(final int direction) {
		final int ancienI = this.iElementSelectionne;
		final int ancienJ = this.jElementSelectionne;
		
		// D�placement du curseur dans le tableau
		switch (direction) {
			case Direction.GAUCHE :
				if (this.jElementSelectionne<=0) {
					// on sort de la Liste
					return null;
				} else {
					this.jElementSelectionne--;
				}
				break;
			case Direction.HAUT :
				if (this.iElementSelectionne<=0) {
					// on sort de la Liste
					return null;
				} else {
					this.iElementSelectionne--;
				}
				break;
			case Direction.DROITE :
				if (this.jElementSelectionne >= this.nombreDeColonnes-1) {
					// on sort de la Liste
					return null;
				} else {
					this.jElementSelectionne++;
				}
				break;
			case Direction.BAS :
				if (this.iElementSelectionne >= this.nombreDeLignesTotal-1) {
					// on sort de la Liste
					return null;
				} else {
					this.iElementSelectionne++;
				}
				break;
		}
		
		//on ne fait finalement rien s'il n'y a pas d'ElementDeMenu dans cette case
		final ElementDeMenu nouvelElementSelectionne;
		try {
			nouvelElementSelectionne = this.elements.get(this.iElementSelectionne*this.nombreDeColonnes + this.jElementSelectionne);
		} catch (ArrayIndexOutOfBoundsException e) {
			// on sort de la Liste
			this.iElementSelectionne = ancienI;
			this.jElementSelectionne = ancienJ;
			return null;
		}

		LOG.debug("position du curseur dans la liste : ligne "+ this.iElementSelectionne+" colonne "+this.jElementSelectionne);
		
		// Eventuellement masquer/afficher certains ElementsDeMenu en fonction du nombre de lignes/colonnes � afficher
		boolean decalageDuTableau = false;
		if (this.iElementSelectionne < this.premiereLigneVisible ) {
			this.premiereLigneVisible--;
			decalageDuTableau = true;
		} else if (this.iElementSelectionne >= this.premiereLigneVisible + this.nombreDeLignesVisibles) {
			this.premiereLigneVisible++;
			decalageDuTableau = true;
		}
		if (decalageDuTableau) {
			this.iElementSelectionne = ancienI;
			this.jElementSelectionne = ancienJ;
			
			final int taille = this.elements.size();
			int i, j;
			ImageMenu element;
			this.elementsAffiches = new ImageMenu[this.nombreDeLignesVisibles][this.nombreDeColonnes];
			for (int n = 0; n < taille; n++) {
				element = this.elements.get(n);
				i = n / this.nombreDeColonnes;
				j = n % this.nombreDeColonnes;
				if (i>=this.premiereLigneVisible && i<this.premiereLigneVisible+this.nombreDeLignesVisibles) {
					element.invisible = false;
					element.x = this.x + (this.largeurMaximaleElement+Texte.MARGE_A_DROITE) * j;
					element.y = this.y + (this.hauteurMaximaleElement+Texte.INTERLIGNE) * (i - this.premiereLigneVisible);
					this.elementsAffiches[i - this.premiereLigneVisible][j] = element;
				} else {
					element.invisible = true;
				}
			}
		}
		
		return nouvelElementSelectionne;
	}

}
