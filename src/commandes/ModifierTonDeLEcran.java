package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.LecteurMap;
import utilitaire.graphismes.Graphismes;

/**
 * Modifier le ton de l'�cran.
 * Utile pour les ambiances lumineuses.
 * Le ton de l'�cran est propre au Tileset, 
 * donc le changement de ton est conserv� d'une Map � l'autre si le Tileset est le m�me
 */
public class ModifierTonDeLEcran extends Commande implements CommandeEvent {
	//constante
	private static final int MEDIANE = Graphismes.OPACITE_MAXIMALE / 2;
	
	private final int rouge, vert, bleu, gris;
	
	/**
	 * Constructeur explicite
	 * @param rouge importance du rouge dans le nouveau ton de l'�cran
	 * @param vert importance du vert dans le nouveau ton de l'�cran
	 * @param bleu importance du bleu dans le nouveau ton de l'�cran
	 * @param gris d�saturation de l'image
	 */
	public ModifierTonDeLEcran(final int rouge, final int vert, final int bleu, final int gris) {
		this.rouge = rouge;
		this.vert = vert;
		this.bleu = bleu;
		this.gris = gris;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierTonDeLEcran(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("rouge") ? (int) parametres.get("rouge") : MEDIANE,
				parametres.containsKey("vert") ? (int) parametres.get("vert") : MEDIANE,
				parametres.containsKey("bleu") ? (int) parametres.get("bleu") : MEDIANE,
				parametres.containsKey("gris") ? (int) parametres.get("gris") : 0
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		((LecteurMap) Fenetre.getFenetre().lecteur).map.tileset.ton = new int[] {gris, rouge, vert, bleu};
		return curseurActuel+1;
	}

}
