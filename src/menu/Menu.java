package menu;

import java.util.ArrayList;

import son.LecteurAudio;

/**
 * Un Menu est constitu� d'images et de Textes, �ventuellement S�lectionnables.
 */
public abstract class Menu {
	public LecteurMenu lecteur;
	public ArrayList<Texte> textes;
	public ArrayList<ElementDeMenu> elements;
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
		Selectionnable elementASelectionner = chercherSelectionnableAuDessus();
		selectionner(elementASelectionner);
	}

	/**
	 * S�lectionner l'El�ment S�lectionnable situ� juste en dessous.
	 */
	public final void selectionnerElementEnBas() {
		Selectionnable elementASelectionner = chercherSelectionnableEnDessous();
		selectionner(elementASelectionner);
	}
	
	/**
	 * S�lectionner l'El�ment S�lectionnable situ� juste � gauche.
	 */
	public final void selectionnerElementAGauche() {
		Selectionnable elementASelectionner = chercherSelectionnableAGauche();
		selectionner(elementASelectionner);
	}
	
	/**
	 * S�lectionner l'El�ment S�lectionnable situ� juste � droite.
	 */
	public final void selectionnerElementADroite() {
		Selectionnable elementASelectionner = chercherSelectionnableADroite();
		selectionner(elementASelectionner);
	}
	
	/**
	 * S�lectionner cet El�ment de Menu.
	 * @param elementASelectionner nouvel Element s�lectionn�
	 */
	public final void selectionner(final Selectionnable elementASelectionner) {
		if (elementASelectionner != null) {
			//bruit de d�placement du curseur
			if (this.elementSelectionne!=null && !elementASelectionner.equals(this.elementSelectionne)) {
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
	 * Obtenir la liste des El�ments S�lectionnables.
	 * @return liste des S�lectionnables
	 */
	public final ArrayList<Selectionnable> getSelectionnables() {
		ArrayList<Selectionnable> selectionnables = new ArrayList<Selectionnable>();
		for (Texte t : this.textes) {
			if (t.selectionnable) {
				selectionnables.add(t);
			}
		}
		for (ElementDeMenu e : this.elements) {
			if (e.selectionnable) {
				selectionnables.add(e);
			}
		}
		return selectionnables;
	}
	
	/**
	 * Calculer quel est l'El�ment de Menu S�lectionnable situ� au dessus de celui-ci
	 * @return El�ment de Menu situ� au dessus
	 */
	private Selectionnable chercherSelectionnableAuDessus() {
		Selectionnable elementASelectionner = null;
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit �tre au dessus
			if (selectionnable.y < elementSelectionne.y) {
				if (elementASelectionner != null) {
					//on prend le plus proche en ordonn�e
					if (selectionnable.y > elementASelectionner.y) {
						//on prend le plus proche en abscisse
						if ( Math.abs(selectionnable.x - elementSelectionne.x) <= Math.abs(elementASelectionner.x - elementSelectionne.x) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
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
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit �tre en dessous
			if (selectionnable.y > elementSelectionne.y) {
				if (elementASelectionner != null) {
					//on prend le plus proche en ordonn�e
					if (selectionnable.y < elementASelectionner.y) {
						//on prend le plus proche en abscisse
						if ( Math.abs(selectionnable.x - elementSelectionne.x) <= Math.abs(elementASelectionner.x - elementSelectionne.x) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
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
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit �tre � gauche
			if (selectionnable.x < elementSelectionne.x) {
				if (elementASelectionner != null) {
					//on prend le plus proche en abscisse
					if (selectionnable.x > elementASelectionner.x) {
						//on prend le plus proche en ordonn�e
						if ( Math.abs(selectionnable.y - elementSelectionne.y) <= Math.abs(elementASelectionner.y - elementSelectionne.y) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
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
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit �tre � droite
			if (selectionnable.x > elementSelectionne.x) {
				if (elementASelectionner != null) {
					//on prend le plus proche en abscisse
					if (selectionnable.x < elementASelectionner.x) {
						//on prend le plus proche en ordonn�e
						if ( Math.abs(selectionnable.y - elementSelectionne.y) <= Math.abs(elementASelectionner.y - elementSelectionne.y) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
				}
			}
		}
		return elementASelectionner;
	}
	
}
