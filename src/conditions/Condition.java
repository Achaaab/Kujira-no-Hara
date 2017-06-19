package conditions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Commande;

/**
 * Une Condition peut servir � d�finir le moment de d�clenchement d'une Page, ou faire partie du code Event.
 */
public abstract class Condition extends Commande {
	private static final Logger LOG = LogManager.getLogger(Condition.class);
	
	public int numero = -1; //le num�ro de condition est le m�me que le num�ro de fin de condition qui correspond

	/**
	 * La Condition est elle v�rifi�e ?
	 * @return true si v�rifi�e, false si non v�rifi�e
	 */
	public abstract boolean estVerifiee();
	
	/**
	 * Une Condition est une Commande Event, elle peut �tre execut�e pour faire des sauts de curseur.
	 * Son execution est instantan�e.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		//une Condition doit avoir un num�ro pour �tre ex�cut�e comme Commande Event
		if (this.numero == -1) {
			LOG.error("La condition "+this.getClass().getName()+" n'a pas de num�ro !");
		}
		
		if ( estVerifiee() ) {
			return curseurActuel+1;
		} else {
			int nouveauCurseur = curseurActuel;
			boolean onATrouveLaFinDeSi = false;
			while (!onATrouveLaFinDeSi) {
				nouveauCurseur++;
				try {
					//la fin de si a le m�me numero que la condition
					if ( ((Condition) commandes.get(nouveauCurseur)).numero == numero ) {
						onATrouveLaFinDeSi = true;
					}
				} catch (IndexOutOfBoundsException e) {
					if (this instanceof CommandeEvent) {
						LOG.error("L'�v�nement n�"+this.page.event.id+" n'a pas trouv� sa fin de condition "+this.numero+" :", e);
					}
					if (this instanceof CommandeMenu) {
						LOG.error("L'�l�ment de menu n�"+this.element.id+" n'a pas trouv� sa fin de condition "+this.numero+" :", e);
					}
				} catch (Exception e) {
					//pas une condition
				}
			}
			return nouveauCurseur+1;
		}
	}
	
	/** 
	 * Est-ce que la Condition demande un mouvement particulier du H�ros ?
	 * Contact, Arriv�eAuContact, Parler... 
	 * @return false si la Condition est � consid�rer pour l'apparence d'un Event, false sinon
	 */
	public abstract boolean estLieeAuHeros();
	
	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 * Cette m�thode, exig�e par CommandeMenu, est la m�me pour toutes les Conditions.
	 */
	public void executer() {
		//rien
	}
	
	/**
	 * Traduit les Conditions depuis le format JSON et les range dans la liste des Conditions de la Page.
	 * @param conditions liste des Conditions de la Page
	 * @param conditionsJSON tableau JSON contenant les Conditions au format JSON
	 */
	public static void recupererLesConditions(final ArrayList<Condition> conditions, final JSONArray conditionsJSON) {
		
		for (Object conditionJSON : conditionsJSON) {
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
				LOG.error("Erreur lors de l'instanciation de la Condition :", e1);
			}
		}
	}
	
}
