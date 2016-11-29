package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Picture;
import utilitaire.graphismes.ModeDeFusion;

/**
 * D�marrer une transition progressive de l'�tat actuel de l'image vers un �tat d'arriv�e.
 * Cette transition peut concerner le position de l'image, son opacit�, son zoom, son angle.
 * Il est �galement possible de changer de mode de fusion, mais ce changement sera imm�diat.
 */
public class DeplacerImage extends Commande implements CommandeEvent {
	/** Le d�placement d'image est instantan� */
	private static final int INSTANTANE = 0;
	
	/** num�ro de l'image � d�placer */
	private Integer numero; //Integer car utilis� comme cl� d'une HashMap
	/** dur�e (en frames) de la transition */
	private int nombreDeFrames;
	private int dejaFait;
	
	private Integer x;
	private Integer y;
	/** la nouvelle origine est-elle le centre de l'image ? */
	private Boolean centre;
	/** les coordonn�es sont-elles stock�es dans des variables ? */
	private boolean variables;
	
	private int xDebut;
	private int yDebut;
	private int xFin;
	private int yFin;
	private int zoomXDebut;
	private int zoomYDebut;
	private Integer zoomXFin;
	private Integer zoomYFin;
	private int opaciteDebut;
	private Integer opaciteFin;
	private ModeDeFusion modeDeFusion;
	private int angleDebut;
	private Integer angleFin;
	
	/**
	 * Constructeur explicite
	 * @param numero de l'image � modifier
	 * @param nombreDeFrames dur�e de la transition
	 * @param centre l'origine pour les nouvelles coordonn�es de l'image est elle son centre ?
	 * @param variables les nouvelles coordonn�es sont-elles stock�es dans des variables ?
	 * @param x coordonn�e d'arriv�e
	 * @param y coordonn�e d'arriv�e
	 * @param zoomX zoom d'arriv�e
	 * @param zoomY zoom d'arriv�e
	 * @param opacite opacit� d'arriv�e
	 * @param modeDeFusion d'arriv�e
	 * @param angle d'arriv�e
	 */
	private DeplacerImage(final Integer numero, final int nombreDeFrames, final Boolean centre, 
			final boolean variables, final Integer x, final Integer y, final Integer zoomX, final Integer zoomY, 
			final Integer opacite, final ModeDeFusion modeDeFusion, final Integer angle) {
		this.numero = numero;
		this.centre = centre;
		this.variables = variables;
		this.x = x;
		this.y = y;
		this.zoomXFin = zoomX;
		this.zoomYFin = zoomY;
		this.opaciteFin = opacite;
		this.modeDeFusion = modeDeFusion;
		this.angleFin = angle;
		
		this.nombreDeFrames = nombreDeFrames;
		this.dejaFait = 0;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public DeplacerImage(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
				parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : INSTANTANE,
				parametres.containsKey("centre") ? (boolean) parametres.get("centre") : null,
				parametres.containsKey("variables") ? (boolean) parametres.get("variables") : false,
				parametres.containsKey("x") ? (int) parametres.get("x") : null,
				parametres.containsKey("y") ? (int) parametres.get("y") : null,
				parametres.containsKey("zoomX") ? (int) parametres.get("zoomX") : null,
				parametres.containsKey("zoomY") ? (int) parametres.get("zoomY") : null,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : null,
				parametres.containsKey("modeDeFusion") ? ModeDeFusion.parNom(parametres.get("modeDeFusion")) : null,
				parametres.containsKey("angle") ? (int) parametres.get("angle") : null
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final double progression = (double) this.dejaFait / (double) this.nombreDeFrames;
		final Picture picture = Fenetre.getPartieActuelle().images.get(this.numero);
		
		//initialisation des extr�mums
		if (this.dejaFait <= 0) {
			this.xDebut = picture.x;
			this.yDebut = picture.y;
			
			if (this.x != null && this.y != null) {
				if (this.variables) {
					//valeurs stock�es dans des variables
					this.xFin = Fenetre.getPartieActuelle().variables[this.x];
					this.yFin = Fenetre.getPartieActuelle().variables[this.y];
				} else {
					//valeurs brutes
					this.xFin = this.x;
					this.yFin = this.y;
				}
			}
			
			this.zoomXDebut = picture.zoomX;
			this.zoomYDebut = picture.zoomY;
			this.angleDebut = picture.angle;
			this.opaciteDebut = picture.opacite;
			
			//n'est modifi� que ce qui a �t� explicitement sp�cifi�
			if (this.modeDeFusion != null) {
				picture.modeDeFusion = this.modeDeFusion;
			}
			if (this.centre != null) {
				picture.centre = this.centre;
			}
		}
		
		//n'est modifi� que ce qui a �t� explicitement sp�cifi�
		if (this.x != null) {
			picture.x = (int) Math.round(progression * this.xFin + (1-progression) * this.xDebut);
		}
		if (this.y != null) {
			picture.y = (int) Math.round(progression * this.yFin + (1-progression) * this.yDebut);
		}
		if (this.zoomXFin != null) {
			picture.zoomX = (int) Math.round(progression * this.zoomXFin + (1-progression) * this.zoomXDebut);
		}
		if (this.zoomYFin != null) {
			picture.zoomY = (int) Math.round(progression * this.zoomYFin + (1-progression) * this.zoomYDebut);
		}
		if (this.opaciteFin != null) {
			picture.opacite = (int) Math.round(progression * this.opaciteFin + (1-progression) * this.opaciteDebut);
		}
		if (this.angleFin != null) {
			picture.angle = (int) Math.round(progression * this.angleFin + (1-progression) * this.angleDebut);
		}

		if (this.dejaFait < this.nombreDeFrames) {
			//pas fini
			this.dejaFait++;
			return curseurActuel;
		} else {
			//fini
			this.dejaFait = 0;
			return curseurActuel+1;
		}
	}
	
}
