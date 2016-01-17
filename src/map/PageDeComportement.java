package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import comportementEvent.CommandeEvent;
import conditions.Condition;
import map.Event.Direction;

/**
 * Un Event peut avoir plusieurs comportements. Chaque comportement est d�crit par une Page de comportements.
 * La Page est d�clench�e si certaines Conditions sont v�rifi�es, ses Commandes sont alors execut�es.
 */
public class PageDeComportement {
	/** event auquel appartient la page */
	public Event event;
	/** numero de la page */
	public int numero;
	/** ce flag est automatiquement mis � true si contient une page avec une condition Parler */
	private boolean sOuvreParParole = false;
	
	/** conditions de d�clenchement */
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
	public final ArrayList<CommandeEvent> deplacementNaturel;
	public boolean repeterLeDeplacement;
	public boolean ignorerLesMouvementsImpossibles;

	
	/**
	 * Constructeur explicite de la Page de comportement.
	 * @warning constructeur � n'utiliser que pour le H�ros
	 * @param conditions de d�clenchement de la page
	 * @param commandes � executer si la page est d�clench�e
	 * @param nomImage nom du fichier image pour l'apparence
	 */
	public PageDeComportement(final ArrayList<Condition> conditions, final ArrayList<CommandeEvent> commandes, 
			final String nomImage) {
		
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
		this.repeterLeDeplacement = false;
		this.ignorerLesMouvementsImpossibles = true;
		
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			this.image = null;
			e.printStackTrace();
		}
		//on pr�cise si c'est une page qui s'ouvre en parlant � l'�vent
		if (conditions!=null) {
			for (Condition cond : conditions) {
				//TODO dans le futur il y aura aussi la condition "arriv�e sur la case" en plus de "parler" :
				if (cond.getClass().getName().equals("conditions.ConditionParler")) {
					this.sOuvreParParole = true;
				}
			}
		}
	}
	
	/**
	 * La page de comportement est cr��e � partir du fichier JSON.
	 * @param pageJSON objet JSON d�crivant la page de comportements
	 */
	public PageDeComportement(final JSONObject pageJSON) {
		//conditions de d�clenchement de la page
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		try {
			for (Object conditionJSON : pageJSON.getJSONArray("conditions")) {
				try {
					final Class<?> classeCondition = Class.forName("conditions.Condition"+((JSONObject) conditionJSON).get("nom"));
					try {
						//cas d'une Condition sans param�tres
						
						final Constructor<?> constructeurCondition = classeCondition.getConstructor();
						final Condition condition = (Condition) constructeurCondition.newInstance();
						conditions.add(condition);
						
					} catch (NoSuchMethodException e0) {
						//cas d'une Condition utilisant des param�tres
						
						final Iterator<String> parametresNoms = ((JSONObject) conditionJSON).keys();
						String parametreNom; //nom du param�tre pour instancier la Condition
						Object parametreValeur; //valeur du param�tre pour instancier la Condition
						final HashMap<String, Object> parametres = new HashMap<String, Object>();
						while (parametresNoms.hasNext()) {
							parametreNom = parametresNoms.next();
							if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
								parametreValeur = ((JSONObject) conditionJSON).get(parametreNom);
								parametres.put( parametreNom, parametreValeur );
							}
						}
						final Constructor<?> constructeurCondition = classeCondition.getConstructor(parametres.getClass());
						final Condition condition = (Condition) constructeurCondition.newInstance(parametres);
						conditions.add(condition);
					
					}
					
				} catch (Exception e1) {
					System.err.println("Erreur lors de l'instanciation de la Condition :");
					e1.printStackTrace();
				}
			}
			
		} catch (JSONException e2) {
			//pas de Conditions de d�clenchement pour cette Page
		}
		this.conditions = conditions;
		
		//commandes de la page
		final ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
		try {
			for (Object commandeJSON : pageJSON.getJSONArray("commandes")) {
				try {
					final Class<?> classeCommande = Class.forName("comportementEvent."+((JSONObject) commandeJSON).get("nom"));
					final Iterator<String> parametresNoms = ((JSONObject) commandeJSON).keys();
					String parametreNom; //nom du param�tre pour instancier la Commande Event
					Object parametreValeur; //valeur du param�tre pour instancier la Commande Event
					final HashMap<String, Object> parametres = new HashMap<String, Object>();
					while (parametresNoms.hasNext()) {
						parametreNom = parametresNoms.next();
						if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
							parametreValeur = ((JSONObject) commandeJSON).get(parametreNom);
							parametres.put( parametreNom, parametreValeur );
						}
					}
					final Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
					final CommandeEvent commande = (CommandeEvent) constructeurCommande.newInstance(parametres);
					commandes.add(commande);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
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
		} catch (JSONException e1) {
			this.vitesse = Event.VITESSE_PAR_DEFAUT;
		}
		try {
			this.frequence = (int) pageJSON.get("frequence");
		} catch (JSONException e1) {
			this.frequence = Event.FREQUENCE_PAR_DEFAUT;
		}
		
		//mouvement de l'event lors de cette page
		this.deplacementNaturel = new ArrayList<CommandeEvent>();
		try {
			for (Object actionDeplacementJSON : pageJSON.getJSONArray("deplacement")) {
				try {
					final Class<?> classeActionDeplacement = Class.forName("comportementEvent."+((JSONObject) actionDeplacementJSON).get("nom"));
					final Iterator<String> parametresNoms = ((JSONObject) actionDeplacementJSON).keys();
					String parametreNom;
					Object parametreValeur;
					final HashMap<String, Object> parametres = new HashMap<String, Object>();
					while (parametresNoms.hasNext()) {
						parametreNom = parametresNoms.next();
						if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
							parametreValeur = ((JSONObject) actionDeplacementJSON).get(parametreNom);
							parametres.put(parametreNom, parametreValeur);
						}
					}
					final Constructor<?> constructeurActionDeplacement = classeActionDeplacement.getConstructor(HashMap.class);
					final CommandeEvent actionDeplacement = (CommandeEvent) constructeurActionDeplacement.newInstance(parametres);
					deplacementNaturel.add(actionDeplacement);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (JSONException e2) {
			//pas de d�placement pour cette Page
		}
		try {
			this.repeterLeDeplacement = (boolean) pageJSON.get("repeterLeDeplacement");
		} catch (JSONException e2) {
			this.repeterLeDeplacement = Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT;
		}
		try {
			this.ignorerLesMouvementsImpossibles = (boolean) pageJSON.get("ignorerLesMouvementsImpossibles");
		} catch (JSONException e2) {
			this.ignorerLesMouvementsImpossibles = Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT;
		}
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			//e.printStackTrace();
		}
		//on pr�cise si c'est une page qui s'ouvre en parlant � l'event
		if (conditions!=null) {
			for (Condition cond : conditions) {
				//TODO dans le futur il y aura aussi la condition "arriv�e sur la case" en plus de "parler" :
				if (cond.getClass().getName().equals("conditions.ConditionParler")) {
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
