package map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.Deplacement;
import commandes.Message;
import conditions.Condition;
import conditions.ConditionParler;
import main.Commande;
import main.Main;
import mouvements.Mouvement;
import mouvements.SeRapprocher;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Un Event peut avoir plusieurs comportements. Chaque comportement est d�crit par une Page de comportements.
 * La Page est d�clench�e si certaines Conditions sont v�rifi�es, ses Commandes sont alors execut�es.
 */
public class PageEvent {
	private static final Logger LOG = LogManager.getLogger(PageEvent.class);
	
	/** Event auquel appartient la Page */
	public Event event;
	/** numero de la Page */
	public final int numero;
	
	/** Conditions de d�clenchement de la Page */
	public final ArrayList<Condition> conditions;
	
	/** liste de Commandes � executer dans l'ordre si les Conditions sont v�rifi�es */
	public final ArrayList<Commande> commandes;
	/**
	 * Le curseur indique quelle Commande executer.
	 * Il se d�place incr�mentalement, mais on peut lui faire faire des sauts.
	 */
	public int curseurCommandes = 0;
	
	//apparence
	private String nomImage;
	public BufferedImage image;
	public boolean apparenceEstUnTile;
	/** par d�faut, si image < 32px, l'Event est consid�r� comme plat (au sol) */
	public Boolean plat; //
	public int directionInitiale;
	public int animationInitiale;
	
	/**
	 * Lorsque cette Page sera active, qui pourra traverser cet Event ?
	 */
	public enum Traversabilite {
		TRAVERSABLE, OBSTACLE, TRAVERSABLE_PAR_LE_HEROS
	}

	//param�tres
	public Vitesse vitesse;
	public Frequence frequence;
	
	/** 
	 * <p>figer les autres Events pendant la lecture de cette Page</p>
	 * <p>automatiquement mis � true si la Page contient une condition Parler</p>
	 */
	private boolean figerLesAutresEvents;
	public boolean animeALArret;
	public boolean animeEnMouvement;
	public Traversabilite traversable;
	public boolean directionFixe;
	public boolean auDessusDeTout;
	public int opacite;
	public ModeDeFusion modeDeFusion;
	
	//mouvement
	public Deplacement deplacementNaturel = null;
	
	/**
	 * Constructeur g�n�rique
	 * La page de comportement est cr��e � partir du fichier JSON.
	 * @param numero de la Page
	 * @param pageJSON objet JSON d�crivant la page de comportements
	 * @param idEvent identifiant de l'Event
	 * @param map de l'Event
	 */
	public PageEvent(final int numero, final JSONObject pageJSON, final Integer idEvent, final Map map) {
		this.numero = numero;
		
		// Conditions de d�clenchement de la Page
		this.conditions = new ArrayList<Condition>();
		if (pageJSON.has("conditions")) {
			Condition.recupererLesConditions(this.conditions, pageJSON.getJSONArray("conditions"));
			if (this.conditions.size() > 0) {
				//on apprend aux Conditions qui est leur Page
				for (Condition condition : this.conditions) {
					condition.page = this;
				}
			} else {
				LOG.trace("Liste de conditions vide pour la page "+this.numero+" de l'event "+idEvent);
			}
		} else {
			LOG.trace("Pas de conditions pour la page "+this.numero+" de l'event "+idEvent);
		}
		
		// Commandes de la Page
		this.commandes = new ArrayList<Commande>();
		if (pageJSON.has("commandes")) {
			Commande.recupererLesCommandesEvent(this.commandes, pageJSON.getJSONArray("commandes"));
			if (this.commandes.size() > 0) {
				//on apprend aux Commandes qui est leur Page
				boolean laPageContientUnMessage = false;
				for (Commande commande : this.commandes) {
					commande.page = this;
					
					if (commande instanceof Message) {
						laPageContientUnMessage = true;
					}
				}
				
				//on rapproche le H�ros de l'Event si c'est un interlocuteur
				if (this.contientUneConditionParler() && laPageContientUnMessage) {
					final ArrayList<Mouvement> mouvements = new ArrayList<>();
					mouvements.add(new SeRapprocher(0, idEvent));
					final Commande deplacement = new Deplacement(0, mouvements, true, false, true);
					this.commandes.add(0, deplacement);
				}
			} else {
				LOG.trace("Liste de commandes vide pour la page "+this.numero+" de l'event "+idEvent);
			}
		} else {
			LOG.trace("Pas de commandes pour la page "+this.numero+" de l'event "+idEvent);
		}
		
		// Apparence de l'Event lors de cette Page
		this.directionInitiale = pageJSON.has("directionInitiale") ? pageJSON.getInt("directionInitiale") : Event.DIRECTION_PAR_DEFAUT;
		this.animationInitiale = pageJSON.has("animationInitiale") ? pageJSON.getInt("animationInitiale") : 0;
		
		Integer tileDeLApparence = null;
		if (pageJSON.has("image")) {
			// l'Event a une apparence
			try {
				tileDeLApparence = pageJSON.getInt("image");
				// l'apparence est un Tile
				this.nomImage = "" + tileDeLApparence;
				this.image = map.tileset.carreaux[tileDeLApparence];
				this.apparenceEstUnTile = true;
			} catch (JSONException e) {
				// l'apparence est une Image
				this.nomImage = pageJSON.getString("image");
				try {
					//ouverture de l'image d'apparence
					this.image = Graphismes.ouvrirImage("Characters", nomImage);
				} catch (IOException e1) {
					//l'image d'apparence n'existe pas
					LOG.error("L'image d'apparence \""+nomImage+"\" de l'event "+idEvent+" n'existe pas !", e1);
					this.image = null;
				}
			}
		} else {
			// pas d'image
			this.image = null;
			LOG.trace("Pas d'image d'apparence pour la page "+this.numero+" de l'event "+idEvent);
		}
	
		// Propri�t�s de cette Page
		try {
			this.frequence = pageJSON.has("frequence") 
					? Frequence.parNom(pageJSON.getString("frequence"))
					: Event.FREQUENCE_PAR_DEFAUT;
		} catch (Exception e) {
			LOG.error("La fr�quence de l'�vent "+idEvent+" n'est pas une chaine de caract�res : "
					+pageJSON.toString());
			this.frequence = Event.FREQUENCE_PAR_DEFAUT;
		}
		if (this.frequence == null) {
			LOG.error("La fr�quence de l'event "+idEvent+" est nulle ! Cela va cr�er une erreur lors de l'animation !");
		}
		try {
			this.vitesse = pageJSON.has("vitesse")
					? Vitesse.parNom(pageJSON.getString("vitesse"))
					: Event.VITESSE_PAR_DEFAUT;
		} catch (Exception e) {
			LOG.error("La vitesse de l'event "+idEvent+" n'est pas une chaine de caract�res : "
					+pageJSON.toString());
			this.vitesse = Event.VITESSE_PAR_DEFAUT;
		}
		if (contientUneConditionParler()) {
			this.figerLesAutresEvents = true;
		} else if (pageJSON.has("figerLesAutresEvents")) {
			this.figerLesAutresEvents = pageJSON.getBoolean("figerLesAutresEvents");
		} else {
			this.figerLesAutresEvents = false;
		}
		this.animeALArret = pageJSON.has("animeALArret") ? pageJSON.getBoolean("animeALArret") : Event.ANIME_A_L_ARRET_PAR_DEFAUT;
		this.animeEnMouvement = pageJSON.has("animeEnMouvement") ? pageJSON.getBoolean("animeEnMouvement") : Event.ANIME_EN_MOUVEMENT_PAR_DEFAUT;

		if (pageJSON.has("traversable")) {
			if (pageJSON.getBoolean("traversable")) {
				this.traversable = Traversabilite.TRAVERSABLE;
			} else {
				if (tileDeLApparence != null) {
					//le tile impose sa traversabilit� si l'Event n'est pas marqu� explicitement traversable
					this.traversable = (map.tileset.passabiliteDeLaCase(tileDeLApparence) != Passabilite.OBSTACLE) ? Traversabilite.TRAVERSABLE : Traversabilite.OBSTACLE;
				} else if (this.image == null) {
					this.traversable = Traversabilite.TRAVERSABLE_PAR_LE_HEROS;
				} else {
					this.traversable = Traversabilite.OBSTACLE;
				} 
			}
		} else {
			if (tileDeLApparence != null) {
				//le tile impose sa traversabilit� si l'Event n'est pas marqu� explicitement traversable
				this.traversable = (map.tileset.passabiliteDeLaCase(tileDeLApparence) != Passabilite.OBSTACLE) ? Traversabilite.TRAVERSABLE : Traversabilite.OBSTACLE;
			} else if (this.image == null) {
				this.traversable = Traversabilite.TRAVERSABLE_PAR_LE_HEROS;
			} else {
				this.traversable = Traversabilite.OBSTACLE;
			}
		}

		this.directionFixe = pageJSON.has("directionFixe") ? pageJSON.getBoolean("directionFixe") : Event.DIRECTION_FIXE_PAR_DEFAUT;
		this.auDessusDeTout = pageJSON.has("auDessusDeTout") ? pageJSON.getBoolean("auDessusDeTout") : Event.AU_DESSUS_DE_TOUT_PAR_DEFAUT;
		if (pageJSON.has("plat")) {
			this.plat = pageJSON.getBoolean("plat");
		} else if (this.image != null) {
			//si non pr�cis�, est d�termin� en fonction de la taille de l'image
			this.plat = (this.image.getHeight()/Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE) <= Main.TAILLE_D_UN_CARREAU;
		} else {
			// pas d'image d'apparence
			this.plat = true;
		}
		this.modeDeFusion = pageJSON.has("modeDeFusion") ? ModeDeFusion.parNom(pageJSON.get("modeDeFusion")) : ModeDeFusion.NORMAL;
		this.opacite = pageJSON.has("opacite") ? pageJSON.getInt("opacite") : Graphismes.OPACITE_MAXIMALE;
		
		// Mouvement de l'Event lors de cette Page
		try {
			if (pageJSON.has("deplacement")) {
				this.deplacementNaturel = (Deplacement) Commande.recupererUneCommande(pageJSON.getJSONObject("deplacement"));
				this.deplacementNaturel.page = this; //on apprend au D�placement quelle est sa Page
				this.deplacementNaturel.naturel = true; //pour le distinguer des D�placements forc�s
			} else {
				//pas de d�placement pour cette Page
				this.deplacementNaturel = null;
			}
		} catch (Exception e) {
			LOG.warn("Erreur lors du chargement du d�placement naturel de la page "+this.numero+" de l'event "+idEvent, e);
		}
	}
	
	/**
	 * La PageEvent contient-elle une ConditionParler ?
	 * Auquel cas, cette Page fige les autres Events de la Map pendant son execution.
	 * @return pr�sence d'une ConditionParler
	 */
	private boolean contientUneConditionParler() {
		if (conditions != null) {
			for (int i = 0; i<conditions.size(); i++) {
				final Condition cond = conditions.get(i);
				if (cond instanceof ConditionParler) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Executer la Page de comportement.
	 * C'est-�-dire que les conditions de d�clenchement ont �t� r�unies.
	 * On va donc lire les commandes une par une avec un curseur.
	 */
	public void executer() {
		//si la page est une page "Parler", elle active le stopEvent qui fige tous les Events
		if (figerLesAutresEvents) {
			this.event.map.lecteur.stopEvent = true;
			this.event.map.lecteur.eventQuiALanceStopEvent = this.event;
		}
		//lecture des Commandes event
		if (commandes != null) {
			boolean onAvanceDansLesCommandes = true;
			//on n'enchaine durant la m�me frame que les commandes instantan�es
			//si une commande longue est rencontr�e, on reporte la lecture de la Page � la frame suivante
			parcoursDesCommandes:
			while (onAvanceDansLesCommandes) {
				if (curseurCommandes < commandes.size()) {
					//il y a encore des commandes dans la liste
					final int ancienCurseur = curseurCommandes;
					final Commande commande = this.commandes.get(curseurCommandes);
					commande.page = this; //on apprend � la Commande depuis quelle Page elle est appel�e
					try {
						curseurCommandes = commande.executer(curseurCommandes, commandes);
					} catch (Exception e1) {
						LOG.error(
								(this.event != null ? "Event "+this.event.id + ", " : "")
								+ "page " + this.numero
								+ ", commande "+curseurCommandes
								+ " ("+commande.getClass().getSimpleName()
								+ ") a �chou� :", e1
						);
						curseurCommandes++;
						throw e1;
					}
					if (curseurCommandes == ancienCurseur) {
						//le curseur n'a pas chang�, c'est donc une commande qui prend du temps
						//la lecture de cette Page sera continu�e � la frame suivante
						onAvanceDansLesCommandes = false;
					}
				} else {
					//on a fini la page
					refermerLaPage();
					break parcoursDesCommandes;
				}
			}
		}
	}
	
	/**
	 * D�sactiver la Page.
	 * Remettre le curseur des commandes � z�ro.
	 * Lib�rer les autres Events s'ils ont �t� fig�s par cette Page.
	 */
	private void refermerLaPage() {
		curseurCommandes = 0;
		if (figerLesAutresEvents) {
			this.event.map.lecteur.stopEvent = false; //on d�sactive le stopEvent si fin de la page
			this.event.map.lecteur.messagePrecedent = null; //plus besoin d'afficher le Message pr�c�dent dans un Choix
		}
		this.event.pageActive = null;
	}
	
}
