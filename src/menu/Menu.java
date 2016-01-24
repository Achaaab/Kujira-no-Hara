package menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import son.LecteurAudio;

/**
 * Un Menu est constitu� d'images et de Textes, �ventuellement S�lectionnables.
 */
public abstract class Menu {
	//constantes
	protected static final BufferedImage ICONE_VIDE = chargerIconeVide(); 
	//TODO utiliser un constructeur largeur;hauteur au lieu d'une icone vide
	//un rectangle fictif est plus rapide � superposer qu'une image vide
	
	public LecteurMenu lecteur;
	public BufferedImage fond;
	public final ArrayList<Texte> textes = new ArrayList<Texte>();
	public final ArrayList<ElementDeMenu> elements = new ArrayList<ElementDeMenu>();
	private ArrayList<Selectionnable> selectionnables;
	public Selectionnable elementSelectionne;
	public String nomBGM;
	
	/**
	 * Quitter ce Menu.
	 */
	public abstract void quitter();

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
	 * S�lectionner l'El�ment S�lectionnable situ� juste au dessus.
	 */
	public final void selectionnerElementEnHaut() {
		final Selectionnable elementASelectionner = chercherSelectionnableAuDessus();
		selectionner(elementASelectionner);
	}

	/**
	 * S�lectionner l'El�ment S�lectionnable situ� juste en dessous.
	 */
	public final void selectionnerElementEnBas() {
		final Selectionnable elementASelectionner = chercherSelectionnableEnDessous();
		selectionner(elementASelectionner);
	}
	
	/**
	 * S�lectionner l'El�ment S�lectionnable situ� juste � gauche.
	 */
	public final void selectionnerElementAGauche() {
		final Selectionnable elementASelectionner = chercherSelectionnableAGauche();
		selectionner(elementASelectionner);
	}
	
	/**
	 * S�lectionner l'El�ment S�lectionnable situ� juste � droite.
	 */
	public final void selectionnerElementADroite() {
		final Selectionnable elementASelectionner = chercherSelectionnableADroite();
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
			for (ElementDeMenu e : this.elements) {
				if (e.selectionnable) {
					this.selectionnables.add(e);
				}
			}
		}
		//on l'a cr��e
		return this.selectionnables;
	}
	
	/**
	 * Calculer quel est l'El�ment de Menu S�lectionnable situ� au dessus de celui-ci
	 * @return El�ment de Menu situ� au dessus
	 */
	private Selectionnable chercherSelectionnableAuDessus() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaY;
		Integer deltaYMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.x-s.x) <= 2*this.elementSelectionne.largeur 
				&& this.elementSelectionne.y > s.y
			) {
				deltaY = Math.abs(this.elementSelectionne.y-s.y);
				if (deltaYMin==null || deltaY<deltaYMin) {
					elementASelectionner = s;
					deltaYMin = deltaY; //on m�morise le plus proche rencontr�
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Calculer quel est l'El�ment de Menu S�lectionnable situ� en dessous de celui-ci
	 * @return El�ment de Menu situ� en dessous
	 */
	private Selectionnable chercherSelectionnableEnDessous() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaY;
		Integer deltaYMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.x-s.x) <= 2*this.elementSelectionne.largeur 
				&& this.elementSelectionne.y < s.y
			) {
				deltaY = Math.abs(this.elementSelectionne.y-s.y);
				if (deltaYMin==null || deltaY<deltaYMin) {
					elementASelectionner = s;
					deltaYMin = deltaY; //on m�morise le plus proche rencontr�
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Calculer quel est l'El�ment de Menu S�lectionnable situ� � gauche de celui-ci
	 * @return El�ment de Menu situ� � gauche
	 */
	private Selectionnable chercherSelectionnableAGauche() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaX;
		Integer deltaXMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.y-s.y) <= 2*this.elementSelectionne.hauteur 
				&& this.elementSelectionne.x > s.x
			) {
				deltaX = Math.abs(this.elementSelectionne.x-s.x);
				if (deltaXMin==null || deltaX<deltaXMin) {
					elementASelectionner = s;
					deltaXMin = deltaX; //on m�morise le plus proche rencontr�
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Calculer quel est l'El�ment de Menu S�lectionnable situ� � droite de celui-ci
	 * @return El�ment de Menu situ� � droite
	 */
	private Selectionnable chercherSelectionnableADroite() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaX;
		Integer deltaXMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.y-s.y) <= 2*this.elementSelectionne.hauteur 
				&& this.elementSelectionne.x < s.x
			) {
				deltaX = Math.abs(this.elementSelectionne.x-s.x);
				if (deltaXMin==null || deltaX<deltaXMin) {
					elementASelectionner = s;
					deltaXMin = deltaX; //on m�morise le plus proche rencontr�
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Icone vide pour les objets non poss�d�s dans les Menus
	 * @return image d'ic�ne vide
	 */
	private static BufferedImage chargerIconeVide() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\icone vide32.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
