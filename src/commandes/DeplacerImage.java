package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import utilitaire.Graphismes;
import utilitaire.Graphismes.ModeDeFusion;

/**
 * D�marrer une transition progressive de l'�tat actuel de l'image vers un �tat d'arriv�e.
 * Cette transition peut concerner le position de l'image, son opacit�, son zoom, son angle.
 * Il est �galement possible de changer de mode de fusion, mais ce changement sera imm�diat.
 */
public class DeplacerImage extends Commande implements CommandeEvent {
	/** num�ro de l'image � d�placer */
	private int numero;
	/** dur�e (en frames) de la transition */
	private int nombreDeFrames;
	private int dejaFait;
	
	/** la nouvelle origine est-elle le centre de l'image ? */
	private boolean centre;
	/** les coordonn�es sont-elles stock�es dans des variables ? */
	private boolean variables;
	private int x;
	private int y;
	
	private int xDebut;
	private int yDebut;
	private int xFin;
	private int yFin;
	private int zoomXDebut;
	private int zoomYDebut;
	private int zoomXFin;
	private int zoomYFin;
	private int opaciteDebut;
	private int opaciteFin;
	private ModeDeFusion modeDeFusion;
	private int angleFin;
	
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
	private DeplacerImage(final int numero, final int nombreDeFrames, final boolean centre, final boolean variables, final int x, final int y, 
			final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion, final int angle) {
		this.numero = numero;
		this.centre = centre;
		this.variables = variables;
		this.x = x;
		this.y = y;
		this.zoomXFin = zoomX;
		this.zoomXFin = zoomY;
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
				(int) parametres.get("nombreDeFrames"),
				parametres.containsKey("centre") ? (boolean) parametres.get("centre") : false,
				parametres.containsKey("variables") ? (boolean) parametres.get("variables") : false,
				parametres.containsKey("x") ? (int) parametres.get("x") : 0,
				parametres.containsKey("y") ? (int) parametres.get("y") : 0,
				parametres.containsKey("zoomX") ? (int) parametres.get("zoomX") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("zoomY") ? (int) parametres.get("zoomY") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : Graphismes.OPACITE_MAXIMALE,
				ModeDeFusion.parNom(parametres.get("modeDeFusion")),
				parametres.containsKey("angle") ? (int) parametres.get("angle") : Graphismes.PAS_DE_ROTATION
		);
	}

	@Override
	public int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		
		
		//TODO

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
