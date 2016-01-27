package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import commandesEvent.CommandeEvent;
import conditions.Condition;
import conditions.ConditionParler;
import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * Un Event peut avoir plusieurs comportements. Chaque comportement est d�crit par une Page de comportements.
 * La Page est d�clench�e si certaines Conditions sont v�rifi�es, ses Commandes sont alors execut�es.
 */
public class PageDeComportement {
	/** event auquel appartient la page */
	public Event event;
	/** numero de la page */
	public final int numero;
	/** ce flag est automatiquement mis � true si contient une page avec une condition Parler */
	private boolean sOuvreParParole = false;
	
	/** conditions de d�clenchement de la Page */
	public final ArrayList<Condition> conditions;
	
	/** liste de commandes � executer dans l'ordre si les conditions sont v�rifi�es */
	public final ArrayList<CommandeEvent> commandes;
	 /**
	  * Le curseur indique quelle commande executer.
	  * Il se d�place incr�mentalement, mais on peut lui faire faire des sauts.
	  */
	public int curseurCommandes = 0;
	
	//apparence
	public String nomImage;
	public BufferedImage image;
	public boolean estPetit; //si < 32, consid�r� au sol
	public int directionInitiale;
	public int animationInitiale;
	
	//param�tres
	public boolean animeALArret;
	public boolean animeEnMouvement;
	public boolean traversable;
	public boolean auDessusDeTout;
	public int vitesse;
	public int frequence;
	
	//mouvement
	public Deplacement deplacementNaturel;
	
	/**
	 * Constructeur explicite
	 * @Warning constructeur � n'utiliser que pour le H�ros
	 * @param numero de la Page
	 * @param conditions de d�clenchement de la Page
	 * @param commandes � executer si la page est d�clench�e
	 * @param nomImage nom du fichier image pour l'apparence
	 */
	public PageDeComportement(final int numero, final ArrayList<Condition> conditions, final ArrayList<CommandeEvent> commandes, final String nomImage) {
		this.numero = numero;
		
		//Conditions de d�clenchement de la Page
		this.conditions = conditions;
		//Commandes Event de la Page
		this.commandes = commandes;
		
		//apparence de l'Event pour cette Page
		this.nomImage = nomImage;
		
		//propri�t�s de l'Event pour cette Page
		this.vitesse = Heros.VITESSE_HEROS_PAR_DEFAUT;
		this.frequence = Heros.FREQUENCE_HEROS_PAR_DEFAUT;
		this.animeALArret = false;
		this.animeEnMouvement = true;
		this.traversable = false;
		this.auDessusDeTout = false;
		
		
		//d�placement de l'Event pour cette Page
		this.deplacementNaturel = null;
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			this.image = null;
			e.printStackTrace();
		}
		this.estPetit = false;
		
		//on pr�cise si c'est une Page qui s'ouvre en parlant � l'Event
		if (conditions!=null) {
			for (Condition cond : conditions) {
				if (cond.getClass().getName().equals("conditions.ConditionParler")) {
					this.sOuvreParParole = true;
				}
			}
		}
	}
	
	/**
	 * Constructeur g�n�rique
	 * La page de comportement est cr��e � partir du fichier JSON.
	 * @param numero de la Page
	 * @param pageJSON objet JSON d�crivant la page de comportements
	 */
	public PageDeComportement(final int numero, final JSONObject pageJSON) {
		this.numero = numero;
		
		//conditions de d�clenchement de la page
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		try {
			InterpreteurDeJson.recupererLesConditions(conditions, pageJSON.getJSONArray("conditions"));
		} catch (JSONException e2) {
			//pas de Conditions de d�clenchement pour cette Page
		}
		this.conditions = conditions;
		
		//commandes de la page
		final ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
		try {
			InterpreteurDeJson.recupererLesCommandes(commandes, pageJSON.getJSONArray("commandes"));
		} catch (JSONException e2) {
			//pas de Commandes Event pour cette Page
		}
		this.commandes = commandes;
		
		
		//apparence de l'event lors de cette page
		try {
			this.nomImage = (String) pageJSON.get("image");
		} catch (JSONException e) {
			this.nomImage = "";
		}
		try {
			this.directionInitiale = (int) pageJSON.get("directionInitiale");
		} catch (JSONException e) {
			this.directionInitiale = Event.Direction.BAS;
		}
		try {
			this.animationInitiale = (int) pageJSON.get("animationInitiale");
		} catch (JSONException e) {
			this.animationInitiale = 0;
		}
		
		
		//propri�t�s de l'event lors de cette page
		try {
			this.animeALArret = (boolean) pageJSON.get("animeALArret");
		} catch (JSONException e) {
			this.animeALArret = Event.ANIME_A_L_ARRET_PAR_DEFAUT;
		}
		try {
			this.animeEnMouvement = (boolean) pageJSON.get("animeEnMouvement");
		} catch (JSONException e) {
			this.animeEnMouvement = Event.ANIME_EN_MOUVEMENT_PAR_DEFAUT;
		}
		try {
			this.traversable = (boolean) pageJSON.get("traversable");
		} catch (JSONException e) {
			this.traversable = Event.TRAVERSABLE_PAR_DEFAUT;
		}
		try {
			this.auDessusDeTout = (boolean) pageJSON.get("auDessusDeTout");
		} catch (JSONException e) {
			this.auDessusDeTout = Event.AU_DESSUS_DE_TOUT_PAR_DEFAUT;
		}
		
		try {
			this.vitesse = (int) pageJSON.get("vitesse");
		} catch (JSONException e) {
			this.vitesse = Event.VITESSE_PAR_DEFAUT;
		}
		try {
			this.frequence = (int) pageJSON.get("frequence");
		} catch (JSONException e) {
			this.frequence = Event.FREQUENCE_PAR_DEFAUT;
		}
		
		//mouvement de l'event lors de cette page
		try {
			this.deplacementNaturel = new Deplacement(pageJSON.getJSONObject("deplacement"));
		} catch (Exception e) {
			//pas de d�placement pour cette Page
			this.deplacementNaturel = null;
		}
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
			this.estPetit = (this.image.getHeight()/Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE) <= Fenetre.TAILLE_D_UN_CARREAU;
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			this.estPetit = true;
			//e.printStackTrace();
		}
		//on pr�cise si c'est une Page qui s'ouvre en parlant � l'Event
		if (conditions!=null) {
			for (Condition cond : conditions) { //TODO s'arr�ter d�s que true trouv�
				if (cond instanceof ConditionParler) {
					this.sOuvreParParole = true;
				}
			}
		}
	}

	/**
	 * Executer la Page de comportement.
	 * C'est-�-dire que les conditions de d�clenchement ont �t� r�unies.
	 * On va donc lire les commandes une par une avec un curseur.
	 */
	public final void executer() {
		//si la page est une page "Parler", elle active le stopEvent qui fige tous les events
		if (sOuvreParParole) {
			this.event.map.lecteur.stopEvent = true;
		}
		//lecture des commandes event
		if (commandes!=null) {
			try {
				if (curseurCommandes >= commandes.size()) {
					curseurCommandes = 0;
					if (sOuvreParParole) {
						this.event.map.lecteur.stopEvent = false; //on d�sactive le stopEvent si fin de la page
					}
				}
				boolean onAvanceDansLesCommandes = true;
				while (onAvanceDansLesCommandes) {
					final int ancienCurseur = curseurCommandes;
					curseurCommandes = this.commandes.get(curseurCommandes).executer(curseurCommandes, commandes);
					if (curseurCommandes==ancienCurseur) { 
						//le curseur n'a pas chang�, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			} catch (IndexOutOfBoundsException e) {
				//on a fini la page
				curseurCommandes = 0;
				if (sOuvreParParole) {
					this.event.map.lecteur.stopEvent = false; //on d�sactive le stopEvent si fin de la page
				}
				this.event.pageActive = null; 
			}
		}
	}
	
}
