package jeu;

/**
 * Le Chronometre s'affiche � l'�cran et indique les secondes restantes.
 * Il peut �tre croissant ou d�croissant.
 */
public class Chronometre {
	public final boolean croissant;
	public int secondes;
	
	/**
	 * Constructeur explicite
	 * @param croissant le temps augmente-t-il ?
	 * @param secondes temps actuel
	 */
	public Chronometre (final boolean croissant, final int secondes) {
		this.croissant = croissant;
		this.secondes = secondes;
	}
}
