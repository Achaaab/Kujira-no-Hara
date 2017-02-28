package map;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import conditions.Condition;
import main.Commande;
import utilitaire.InterpreteurDeJson;

/**
 * Pages de code commun � toutes les Maps.
 */
public class PageCommune extends PageEvent {
	private static final Logger LOG = LogManager.getLogger(PageCommune.class);
	public boolean active;
	
	/**
	 * Constructeur explicite
	 * @param pageJSON objet JSON repr�sentant la Page
	 */
	public PageCommune(final JSONObject pageJSON) {
		super(-1, pageJSON, -1); //pas d'Event correspondant, pas de num�ro
		this.active = false;
	}

	/**
	 * Activer la Page si les Conditions sont v�rifi�es.
	 */
	public final void essayerDActiver() {
		if (this.conditions!=null && this.conditions.size()>0) {
			//la Page a des Conditions de d�clenchement, on les analyse
			boolean cettePageConvient = true;
			//si une Condition est fausse, la Page ne convient pas
			for (int j = 0; j<this.conditions.size() && cettePageConvient; j++) {
				final Condition cond = this.conditions.get(j);
				if (!cond.estVerifiee()) {
					//la Condition n'est pas v�rifi�e
					cettePageConvient = false;
				}
			}
			//si toutes les Conditions sont v�rifi�es, on active la Page
			if (cettePageConvient) {
				this.active = true;
			}
			
		} else {
			//aucune Condition n�cessaire pour cette Page, donc la Page est activ�e
			this.active = true;
		}
	}
	
	
	@Override
	public final void executer() {
		if (commandes!=null) {
			try {
				if (curseurCommandes >= commandes.size()) {
					curseurCommandes = 0;
				}
				boolean onAvanceDansLesCommandes = true;
				while (onAvanceDansLesCommandes) {
					final int ancienCurseur = curseurCommandes;
					final Commande commande = this.commandes.get(curseurCommandes);
					commande.page = this; //on apprend � la Commande depuis quelle Page elle est appel�e
					curseurCommandes = commande.executer(curseurCommandes, commandes);
					if (curseurCommandes==ancienCurseur) { 
						//le curseur n'a pas chang�, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			} catch (IndexOutOfBoundsException e) {
				//on a fini la page
				curseurCommandes = 0;
				this.active = false;
			}
		}
	}
	
	/**
	 * R�cup�rer les Pages Communes d�crites dans un fichier JSON.
	 * @return Pages de code Event communes � toutes les Maps du jeu
	 */
	public static ArrayList<PageCommune> recupererLesPagesCommunes() {
		final ArrayList<PageCommune> pagesCommunes = new ArrayList<PageCommune>();
		
		try {
			final JSONObject jsonObjets = InterpreteurDeJson.ouvrirJson("pagesCommunes", ".\\ressources\\Data\\");
			final JSONArray jsonPagesCommunes = jsonObjets.getJSONArray("pages");
		
			for (Object o : jsonPagesCommunes) {
				final JSONObject jsonPageCommune = (JSONObject) o;
				final PageCommune pageCommune = new PageCommune(jsonPageCommune);
				pagesCommunes.add(pageCommune);
			}

		} catch (FileNotFoundException e) {
			LOG.error("Impossible de trouver le fichier des pages communes !", e);
		} catch (JSONException e) {
			LOG.error("Impossible de lire le fichier JSON des pages communes !", e);
		}
		
		return pagesCommunes;
	}
	
}
