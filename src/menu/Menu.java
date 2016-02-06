package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import map.Event;
import son.LecteurAudio;

/**
 * Un Menu est constitu� d'images et de Textes, �ventuellement S�lectionnables.
 */
public abstract class Menu {
	//constantes
	protected static final int LARGEUR_ELEMENT_PAR_DEFAUT = 48;
	protected static final int HAUTEUR_ELEMENT_PAR_DEFAUT = 32;
	
	public LecteurMenu lecteur;
	public BufferedImage fond;
	public final ArrayList<Texte> textes = new ArrayList<Texte>();
	public final ArrayList<Element> elements = new ArrayList<Element>();
	private ArrayList<Selectionnable> selectionnables;
	public Selectionnable elementSelectionne;
	public String nomBGM;
	public Menu menuSuivant;
	public Menu menuPrecedent;
	public Menu menuParent;

	/**
	 * Confirmer l'El�ment de Menu s�lectionn�.
	 */
	public final void confirmer() {
		if (elementSelectionne != null) {
			LecteurAudio.playSe("Confirmer.wav");
			elementSelectionne.confirmer();
		} else {
			System.out.println("l'�l�ment s�lectionn� de ce menu est null.");
		}
	}
	
	/**
	 * S�lectionner l'El�ment S�lectionnable situ� dans cette direction
	 * @param direction dans laquelle on recherche un nouvel El�ment � s�lectionner
	 */
	public final void selectionnerElementDansLaDirection(final int direction) {
		final Selectionnable elementASelectionner = chercherSelectionnableDansLaDirection(direction);
		selectionner(elementASelectionner);
	}
	
	/**
	 * S�lectionner cet El�ment de Menu.
	 * @param elementASelectionner nouvel Element s�lectionn�
	 */
	public final void selectionner(final Selectionnable elementASelectionner) {
		if (elementASelectionner != null) {
			//bruit de d�placement du curseur
			if (this.elementSelectionne!=null 
				&& (elementASelectionner.x!=this.elementSelectionne.x || elementASelectionner.y!=this.elementSelectionne.y)
			) {
				LecteurAudio.playSe("DeplacementCurseur.wav");
			}
			//d�s�lection du pr�c�dent
			if (this.elementSelectionne != null) {
				this.elementSelectionne.selectionne = false;
			}
			//s�lection du nouveau
			this.elementSelectionne = elementASelectionner;
			elementASelectionner.selectionne = true;
			
			//d�clenchement du comportement
			elementASelectionner.executerLeComportementALArrivee();
		}
	}
	
	/**
	 * Obtenir la liste des El�ments S�lectionnables de ce Menu.
	 * @return liste des S�lectionnables
	 */
	public final ArrayList<Selectionnable> getSelectionnables() {
		if (this.selectionnables==null) {
			//on ne l'a pas encore cr��e
			this.selectionnables = new ArrayList<Selectionnable>();
			for (Texte t : this.textes) {
				if (t.selectionnable) {
					this.selectionnables.add(t);
				}
			}
			for (Element e : this.elements) {
				if (e.selectionnable) {
					this.selectionnables.add(e);
				}
			}
		}
		//on l'a cr��e
		return this.selectionnables;
	}
	
	/**
	 * V�rifie si l'El�ment (x2,y2) est situ� dans la bonne direction par rapport � l'El�ment de r�f�rence (x1,y1).
	 * @param direction dans laquelle il faut que l'El�ment test� se situe (par rapport � l'El�ment de r�f�rence) pour �tre valide
	 * @param x1 coordonn�e x de l'El�ment de r�f�rence
	 * @param y1 coordonn�e y de l'El�ment de r�f�rence
	 * @param x2 coordonn�e x de l'El�ment test�
	 * @param y2 coordonn�e y de l'El�ment test�
	 * @param largeur tol�r�e pour l'�cart avec la direction voulue
	 * @param hauteur tol�r�e pour l'�cart avec la direction voulue
	 * @return true si l'El�ment test� est dans la bonne direction
	 */
	private Boolean estCandidatALaSelection(final int direction, final int x1, final int y1, final int x2, final int y2, final int largeur, final int hauteur) {
		switch(direction) {
			case Event.Direction.HAUT :
				return (Math.abs(x2-x1) <= 2*largeur) && (y2 > y1);
			case Event.Direction.BAS :
				return (Math.abs(x2-x1) <= 2*largeur) && (y2 < y1);
			case Event.Direction.GAUCHE :
				return (Math.abs(y2-y1) <= 2*hauteur) && (x2 > x1);
			case Event.Direction.DROITE :
				return (Math.abs(y2-y1) <= 2*hauteur) && (x2 < x1);
			default :
				return false;
		}
	}
	
	/**
	 * Calculer quel est l'El�ment de Menu S�lectionnable situ� dans une certaine direction par rapport � celui-ci
	 * @param direction dans laquelle on doit rechercher un El�ment � s�lectionner
	 * @return El�ment de Menu situ� dans cette direction
	 */
	private Selectionnable chercherSelectionnableDansLaDirection(final int direction) {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaX;
		int deltaY;
		int distance;
		Integer distanceMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( estCandidatALaSelection(direction, s.x, s.y, this.elementSelectionne.x, this.elementSelectionne.y, this.elementSelectionne.largeur, this.elementSelectionne.hauteur) ) {
				deltaX = this.elementSelectionne.x-s.x;
				deltaY = this.elementSelectionne.y-s.y;
				distance = deltaX*deltaX + deltaY*deltaY;
				if (distanceMin==null || distance<distanceMin) {
					elementASelectionner = s;
					distanceMin = distance; //on m�morise le plus proche rencontr�
				}
			}
		}
		return elementASelectionner;
	}
	
}
