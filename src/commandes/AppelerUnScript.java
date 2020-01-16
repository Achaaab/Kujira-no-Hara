package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Appeler un script ruby.
 */
public class AppelerUnScript extends Commande implements CommandeEvent, CommandeMenu {
	// constantes
	private static final Logger LOG = LogManager.getLogger(AppelerUnScript.class);
	private static final String ATTENDRE_CET_EVENT = "wait_for_event(@event_id)";
	private static final String ATTENDRE_HEROS = "wait_for_event(0)";
	private static final String POSITIF = "[0-9]+";
	private static final String ATTENDRE_UN_EVENT ="wait_for_event\\(" + POSITIF + "\\)";

	private final String script;
	private ArrayList<Commande> commandes;
	private int curseur;
	public boolean interpretationImplementee;

	/**
	 * Constructeur explicite
	 * 
	 * @param script � ex�cuter
	 */
	public AppelerUnScript(final String script) {
		this.script = script;
		this.commandes = null;
		this.curseur = 0;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AppelerUnScript(final HashMap<String, Object> parametres) {
		this((String) parametres.get("script"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandesEvent) {
		// Initialisation
		if (this.commandes == null) {
			// On n'a pas encore pars� le script en liste de Commandes
			this.commandes = new ArrayList<>();

			// Convertir toutes les expressions du script en Commandes
			String[] expressions = this.script.split("/n");
			for (String expression : expressions) {
				Commande commande = traiter(expression);
				if (commande != null) {
					this.commandes.add(commande);
				}
			}
			
			this.curseur = 0;
		}
		
		// Executer les Commandes equivalentes au script ruby
		if (this.commandes != null && this.commandes.size() > 0) {
			// On d�j� pars� le script en liste de Commandes
			// On continue d'executer les Commandes de la liste
			if (this.curseur < this.commandes.size()) {
				// Il y a encore des Commandes � lire
				this.curseur = this.commandes.get(curseur).executer(this.curseur, this.commandes);
				return curseurActuel;
			} else {
				// Il n'y a plus de Commandes � lire
				this.curseur = 0;
				this.interpretationImplementee = true;
				return curseurActuel + 1;
			}
		} else {
			// Impossible de parser le script !
			LOG.error("L'appel de ce script n'est pas encore impl�ment� !");
			this.interpretationImplementee = false;
			return curseurActuel + 1;
		}
	}
	
	/**
	 * Interpr�ter un script ruby.
	 * @param expression (en ruby)
	 * @return une chaine de caract�re qui est un nombre lorsque l'interpr�tation est termin�e.
	 */
	private Commande traiter(String expression) {

		// Trim
		if (expression.startsWith(" ") || expression.endsWith(" ")) {
			System.out.println("trim");
			return traiter(expression.trim());
		}
		
		Pattern p;
		Matcher m;
		
		//-----------//
		// Commandes //
		//-----------//
		// Attendre cet Event / le Heros
		Integer idEventAAttendre = null;
		if (ATTENDRE_CET_EVENT.equals(this.script)) {
			// Attendre la fin des d�placements de cet Events
			idEventAAttendre = this.page.event.id;
		} else if (ATTENDRE_HEROS.equals(script)) {
			// Attendre la fin du D�placement du H�ros
			idEventAAttendre = this.page.event.map.heros.id;
		}
		if (idEventAAttendre != null) {
			// Attendre l'Event
			final AttendreLaFinDesDeplacements attendreEvent = new AttendreLaFinDesDeplacements(idEventAAttendre);
			attendreEvent.page = this.page; // On dit � la commande qui est son Event
			return attendreEvent;
		}
					
		// Attendre un Event
		p = Pattern.compile(ATTENDRE_UN_EVENT);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println(ATTENDRE_UN_EVENT);
			int idEvent = extraireLeNombre(m.group(0));
			return new AttendreLaFinDesDeplacements(idEvent);
		}
		
		// Autre
		// TODO restent � parser :
		// $game_map.events[@event_id].x == $game_variables[181] &&
		// $game_map.events[@event_id].y == $game_variables[182]

		// invoquer($game_map.events[1].x, $game_map.events[1].y, 21, 54)

		// $game_map.events[@event_id].transform(511, 6)

		// event = invoquer(15, 13, 457, 7)\nevent.jump($game_player.x-event.x,
		// $game_player.y-event.y)

		// $game_map.events[@event_id].life -= 1

		// $game_temp.animations.push([23, true, $game_map.events[@event_id].x,
		// $game_map.events[@event_id].y])

		// x = y = 0\ncase $game_player.direction\nwhen 2; y =
		// $game_map.events[@event_id].y - $game_player.y - 1\nwhen 4; x =
		// $game_map.events[@event_id].x - $game_player.x + 1\nwhen 6; x =
		// $game_map.events[@event_id].x - $game_player.x - 1\nwhen 8; y =
		// $game_map.events[@event_id].y - $game_player.y + 1\nend\n$game_player.jump(x,y)
		
		
		
		
		// Pas encore implemente
		LOG.error("Script impossible � interpr�ter : "+expression);
		this.interpretationImplementee = false;
		return null;
	}
	

	/**
	 * Trouver le nombre situ� dans une chaine de caract�res.
	 * @param nombreBrut chaine de caract�res contenant un nombre
	 * @return nombre contenu
	 */
	private static int extraireLeNombre(final String nombreBrut) {
		final Pattern p = Pattern.compile(POSITIF);
		final Matcher m = p.matcher(nombreBrut);
		m.find();
		final String nombreExtrait = m.group(0);
		return (int) Integer.parseInt(nombreExtrait);
	}

	@Override
	public final String toString() {
		return "AppelerUnScript : " + script;
	}

}
