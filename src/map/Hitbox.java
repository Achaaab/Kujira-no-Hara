package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Fenetre;
import main.Partie;

/**
 * Une hitbox peut �tre assign�e � une arme afin de calculer sa port�e et son �tendue.
 */
public class Hitbox {
	public final int portee;
	public final int etendue;
	
	/**
	 * Constructeur explicite
	 * @param portee : profondeur de la zone d'attaque
	 * @param etendue : largeur de la zone d'attaque
	 */
	public Hitbox(final int portee, final int etendue) {
		this.portee = portee;
		this.etendue = etendue;
	}
	
	/**
	 * Cet Event est-il dans la zone d'attaque du H�ros ?
	 * @param e un Event
	 * @param h le H�ros
	 * @return true si l'Event est dans la zone d'attaque, false sinon
	 */
	public static Boolean estDansZoneDAttaque(final Event e, final Heros h) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		final Boolean estCeQueLeHerosAUneArme = (partieActuelle.idArmesPossedees.size() > 0) && partieActuelle.getArmeEquipee()!=null;
		if (estCeQueLeHerosAUneArme) {
			//on calcule les bords de la zone d'attaque en fonction de l'orientation du h�ros
			final int[] coord = calculerCoordonneesAbsolues(h);
			final int xminHitbox = coord[0];
			final int xmaxHitbox = coord[1];
			final int yminHitbox = coord[2];
			final int ymaxHitbox = coord[3];
			
			final int xminEvent = e.x;
			final int xmaxEvent = e.x + e.largeurHitbox;
			final int yminEvent = e.y;
			final int ymaxEvent = e.y + e.hauteurHitbox;
			//calcul du croisement entre la bodybox de l'event et la hitbox de l'arme
			return lesDeuxRectanglesSeChevauchent(xminHitbox, xmaxHitbox, yminHitbox, ymaxHitbox, xminEvent, xmaxEvent, yminEvent, ymaxEvent, 1, 2, 3, 4); //1,2,3,4 �tant diff�rents, tous les types de croisements seront test�s
		}
		return false;
	}
	
	/**
	 * Calcule les coordonn�es x et y minimales et maximales du rectangle de la Hitbox.
	 * @param h le H�ros
	 * @return xmin, xmax, ymin, ymax
	 */
	public static int[] calculerCoordonneesAbsolues(final Heros h) {
		final int[] coordonneesAbsolues = new int[4];
		final Hitbox b = Fenetre.getPartieActuelle().getArmeEquipee().hitbox;
		int xminHitbox;
		int xmaxHitbox;
		int yminHitbox;
		int ymaxHitbox;
		switch(h.direction) {
			case Event.Direction.BAS :
				xminHitbox = (h.x+h.largeurHitbox/2) - b.etendue/2;
				xmaxHitbox = (h.x+h.largeurHitbox/2) + b.etendue/2;
				yminHitbox = h.y+h.hauteurHitbox;
				ymaxHitbox = h.y+h.hauteurHitbox + b.portee;
				break;
			case Event.Direction.GAUCHE :
				xminHitbox = h.x - b.portee;
				xmaxHitbox = h.x;
				yminHitbox = (h.y+h.hauteurHitbox/2) - b.etendue/2;
				ymaxHitbox = (h.y+h.hauteurHitbox/2) + b.etendue/2;
				break;
			case Event.Direction.DROITE :
				xminHitbox = h.x+h.largeurHitbox;
				xmaxHitbox = h.x+h.largeurHitbox + b.portee;
				yminHitbox = (h.y+h.hauteurHitbox/2) - b.etendue/2;
				ymaxHitbox = (h.y+h.hauteurHitbox/2) + b.etendue/2;
				break;
			default : //HAUT
				xminHitbox = (h.x+h.largeurHitbox/2) - b.etendue/2;
				xmaxHitbox = (h.x+h.largeurHitbox/2) + b.etendue/2;
				yminHitbox = h.y - b.portee;
				ymaxHitbox = h.y;
				break;
		}
		coordonneesAbsolues[0] = xminHitbox;
		coordonneesAbsolues[1] = xmaxHitbox;
		coordonneesAbsolues[2] = yminHitbox;
		coordonneesAbsolues[3] = ymaxHitbox;
		return coordonneesAbsolues;
	}

	/**
	 * Faire une capture d'�cran avec deux rectangles
	 * @param x1min coordonn�e x minimale du rectangle rouge
	 * @param x1max coordonn�e x maximale du rectangle rouge
	 * @param y1min coordonn�e y minimale du rectangle rouge
	 * @param y1max coordonn�e y maximale du rectangle rouge
	 * @param x2min coordonn�e x minimale du rectangle bleu
	 * @param x2max coordonn�e x maximale du rectangle bleu
	 * @param y2min coordonn�e y minimale du rectangle bleu
	 * @param y2max coordonn�e y maximale du rectangle bleu
	 */
	public static void printCroisement(final int x1min, final int x1max, final int y1min, final int y1max, final int x2min, final int x2max, final int y2min, final int y2max) {
		//on part d'une image blanche
		final BufferedImage img = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, LecteurMap.TYPE_DES_IMAGES);
		final Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		
		//on dessine le rouge
		graphics.setPaint(Color.red);
		graphics.fillRect(x1min, y1min, x1max-x1min, y1max-y1min);

		//on dessine le bleu
		graphics.setPaint(Color.blue);
		graphics.fillRect(x2min, y2min, x2max-x2min, y2max-y2min);
		
		//on enregistre
		LecteurMap.sauvegarderImage(img);
	}
	
	/**
	 * Calcule si deux rectangles se chevauchent.
	 * @param x1min coordonn�e x minimale du rectangle 1
	 * @param x1max coordonn�e x maximale du rectangle 1
	 * @param y1min coordonn�e y minimale du rectangle 1
	 * @param y1max coordonn�e y maximale du rectangle 1
	 * @param x2min coordonn�e x minimale du rectangle 2
	 * @param x2max coordonn�e x maximale du rectangle 2
	 * @param y2min coordonn�e y minimale du rectangle 2
	 * @param y2max coordonn�e y maximale du rectangle 2
	 * @param largHitbox largeur du rectangle 1 (non recalcul� pour les parformances)
	 * @param hautHitbox hauteur du rectangle 1 (non recalcul� pour les parformances)
	 * @param largHitboxAutre largeur du rectangle 2 (non recalcul� pour les parformances)
	 * @param hautHitboxAutre hauteur du rectangle 2 (non recalcul� pour les parformances)
	 * @return true si les rectangles se chevauchent, false sinon
	 */
	public static final Boolean lesDeuxRectanglesSeChevauchent(final int x1min, final int x1max, final int y1min, final int y1max, final int x2min, final int x2max, final int y2min, final int y2max, final int largHitbox, final int hautHitbox, final int largHitboxAutre, final int hautHitboxAutre) {
		//premier cas : deux coins se chevauchent
		Boolean deuxCoinsSeChevauchent = ((x1min<=x2min && x2min<x1max && x1max<=x2max)
										 	||(x2min<=x1min && x1min<x2max && x2max<=x1max))
									  && ((y1min<=y2min && y2min<y1max && y1max<=y2max)
											||(y2min<=y1min && y1min<y2max && y2max<=y1max));
		if (deuxCoinsSeChevauchent) {
			return true; 
		}
		
		if (largHitbox==largHitboxAutre && hautHitbox==hautHitboxAutre) {
			//si deux events ont la m�me taille, ils ne peuvent se chevaucher que par le coin
			//(pour �tre plus exact : le cas o� deux events de m�me taille se chevauchent par l'ar�te est un cas particulier de la formule du chevauchement par coin)
			return false;
		}
		
		if (largHitbox!=largHitboxAutre) { //si deux events n'ont pas la m�me largeur, ils peuvent se chevaucher par ar�te horizontale
			//deuxi�me cas : deux cot�s de chevauchent
			Boolean deuxCotesSeChevauchent = ((x1min<=x2min && x2max<=x1max)&&((y2min<=y1min && y1min<y2max && y2max<=y1max)||(y1min<=y2min && y2min<y1max && y1max<=y2max)))
										  || ((x2min<=x1min && x1max<=x2max)&&((y1min<=y2min && y2min<y1max && y1max<=y2max)||(y2min<=y1min && y1min<y2max && y2max<=y1max)));
											//autre m�thode pour r�sultat identique :
										   /*(((x2min<x1min && x1min<x2max)||(x2min<x1max && x1max<x2max)) 
											&& ((y2min<y1min && y1min<y2max)||(y2min<y1max && y1max<y2max)))
										   ||(((x1min<x2min && x2min<x1max)||(x1min<x2max && x2max<x1max)) 
											&& ((y1min<y2min && y2min<y1max)||(y1min<y2max && y2max<y1max)));*/
			if (deuxCotesSeChevauchent) {
				return true;
			}
		}
		if (hautHitbox!=hautHitboxAutre) { //si deux events n'ont pas la m�me hauteur, ils peuvent se chevaucher par ar�te verticale
			//deuxi�me cas : deux cot�s de chevauchent
			Boolean deuxCotesSeChevauchent = ((y1min<=y2min && y2max<=y1max)&&((x2min<=x1min && x1min<x2max && x2max<=x1max)||(x1min<=x2min && x2min<x1max && x1max<=x2max)))
										  || ((y2min<=y1min && y1max<=y2max)&&((x1min<=x2min && x2min<x1max && x1max<=x2max)||(x2min<=x1min && x1min<x2max && x2max<=x1max)));
			if (deuxCotesSeChevauchent) {
				return true;
			}
		}
		
		//troisi�me cas : une hitbox incluse dans l'autre (pfff faut vraiment le faire expr�s lol)
		Boolean unInclusDansLAutre = ((x1min<=x2min && x2max<=x1max)&&(y1min<=y2min && y2max<=y1max))
								   ||((x2min<=x1min && x1max<=x2max)&&(y2min<=y1min && y1max<=y2max));
		return unInclusDansLAutre;
	}
	
}
