package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Brouillard;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Modifier l'image de Panorama pour en avoir une autre que celle associ�e par d�faut au Tileset.
 * On peut �galement changer le degr� de parallaxe.
 */
public class ModifierBrouillard extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(ModifierBrouillard.class);
	
	private final String nomImage;
	private final int opacite;
	private final String nomModeDeFusion;
	private final int defilementX, defilementY;
	private final int zoom;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom de l'image de panorama
	 * @param opacite transparence de 0 � 255
	 * @param nomModeDeFusion normal, addition, soustraction...
	 * @param defilementX vitesse du d�filement horizontal
	 * @param defilementY vitesse du d�filement vertical
	 * @param zoom homoth�tie
	 */
	public ModifierBrouillard(final String nomImage, final int opacite, final String nomModeDeFusion,
			final int defilementX, final int defilementY, final int zoom) {
		this.nomImage = nomImage;
		this.opacite = opacite;
		this.nomModeDeFusion = nomModeDeFusion;
		this.defilementX = defilementX;
		this.defilementY = defilementY;
		this.zoom = zoom;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierBrouillard(final HashMap<String, Object> parametres) {
		this( 
				(String) parametres.get("nomImage"),
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : Graphismes.OPACITE_MAXIMALE,
				parametres.containsKey("modeDeFusion") ? (String) parametres.get("modeDeFusion") : ModeDeFusion.NORMAL.nom,
				parametres.containsKey("defilementX") ? (int) parametres.get("defilementX") : 0,
				parametres.containsKey("defilementY") ? (int) parametres.get("defilementY") : 0,
				parametres.containsKey("zoom") ? (int) parametres.get("zoom") : Graphismes.PAS_D_HOMOTHETIE
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.page.event.map.brouillard = new Brouillard(this.nomImage,
				this.opacite, 
				ModeDeFusion.parNom(this.nomModeDeFusion), 
				this.defilementX, 
				this.defilementY, 
				this.zoom
		);
		return curseurActuel+1;
	}
}
