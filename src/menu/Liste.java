package menu;

import map.Event.Direction;

/**
 * Une Liste est un tableau d'ElementsDeMenu � plusieurs lignes et colonnes.
 */
public class Liste<T extends Listable> {
	/** Position x de la Liste */
	private final int x;
	/** Position y de la Liste */
	private final int y;
	/** Nombre de lignes visibles � l'�cran � la fois */
	private final int lignesAffichees;
	/** Nombre de colonnes visibles � l'�cran � la fois */
	private final int colonnesAffichees;
	
	/** coordonn�e horizontale dans la Liste de l'ElementDeMenu s�lectionn� */
	private int iElementSelectionne;
	/** coordonn�e verticale dans la Liste de l'ElementDeMenu s�lectionn� */
	private int jElementSelectionne;
	/** ElementsDeMenu de la Liste */
	private ElementDeMenu[][] elements; //TODO remplir ce tableau
	
	public Liste(final int x, final int y, final int lignesAffichees, final int colonnesAffichees) {
		this.x = x;
		this.y = y;
		this.lignesAffichees = lignesAffichees;
		this.colonnesAffichees = colonnesAffichees;
	}
	
	/**
	 * Chercher un autre ElementDeMenu � s�lectionner dans la Liste.
	 * @param direction dans laquelle chercher
	 * @return ElementDeMenu � s�lectionner, ou null si bord de Liste
	 */
	public ElementDeMenu selectionnerUnAutreElementDansLaListe(final int direction) {
		switch (direction) {
			case Direction.GAUCHE :
				if (this.iElementSelectionne==0) {
					// on sort de la Liste
					return null;
				}
				this.iElementSelectionne--;
				break;
			case Direction.HAUT :
				if (this.jElementSelectionne==0) {
					// on sort de la Liste
					return null;
				}
				this.jElementSelectionne--;
				break;
			case Direction.DROITE :
				if (this.iElementSelectionne==this.elements.length-1) {
					// on sort de la Liste
					return null;
				}
				this.iElementSelectionne++;
				break;
			case Direction.BAS :
				if (this.jElementSelectionne==this.elements[this.iElementSelectionne].length-1) {
					// on sort de la Liste
					return null;
				}
				this.jElementSelectionne++;
				break;
		}
		
		//TODO �ventuellement masquer/afficher certains ElementsDeMenu en fonction du nombre de lignes/colonnes � afficher
		
		return this.elements[this.iElementSelectionne][this.jElementSelectionne];
	}

}
