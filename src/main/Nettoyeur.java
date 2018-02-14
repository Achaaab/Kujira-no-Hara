package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.AppelerUnScript;
import conditions.ConditionScript;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Adapter les fichiers JSON du jeu au moteur Java sur des sp�cificit�s.
 */
public abstract class Nettoyeur {
	private static final Logger LOG = LogManager.getLogger(Nettoyeur.class);
	
	/**
	 * Lancer le nettoyeur.
	 * @param args rien du tout
	 */
	public static void main(final String[] args) {
		// V�rifier si le nettoyage a d�j� �t� fait
		if (leNettoyageADejaEteFait()) {
			return;
		}
		
		// conditions script
		final ArrayList<String> scripts = listerLesConditionsScript();
		System.out.println("-----------------------------");
		System.out.println("-----------------------------");
		System.out.println("-----------------------------");
		for (String s : scripts) {
			System.out.println(s);
		}
		
		// Autotiles cousins
		//calculerLesAutotilesCousins();
		
		// Noms des touches du clavier dans les messages
		reecrireLesTouchesDuClavier();
		
		// Egaliser les musiques
		egaliserLesMusiques();
		
	}

	private static boolean leNettoyageADejaEteFait() {
		// TODO Auto-generated method stub
		return false;
	}

	private static void calculerLesAutotilesCousins() {
		final File dossierTileset = new File(".\\ressources\\Data\\Tilesets\\");
		final File[] fichiersTileset = dossierTileset.listFiles();
		for (File fichierTileset : fichiersTileset) {
			if (!fichierTileset.getName().endsWith(".new")) {
				try {
					final String nomSansExtension = fichierTileset.getName().substring(0, fichierTileset.getName().lastIndexOf('.'));
					final JSONObject jsonTileset = InterpreteurDeJson.ouvrirJsonTileset(nomSansExtension);
					final JSONArray jsonAutotiles = jsonTileset.getJSONArray("autotiles");
					final int nombreDAutotiles = jsonAutotiles.length();
					final BufferedImage[][] vignettes = new BufferedImage[nombreDAutotiles][2];
					for (int i = 0; i < nombreDAutotiles; i++) {
						final JSONObject jsonAutotile = (JSONObject) jsonAutotiles.get(i);
						final String nomImageAutotile = (String) jsonAutotile.get("nomImage");
						try {
							final BufferedImage imageAutotile = Graphismes.ouvrirImage("Autotile", nomImageAutotile);
							vignettes[i][0] = imageAutotile.getSubimage(0, 0, Main.TAILLE_D_UN_CARREAU, Main.TAILLE_D_UN_CARREAU);
							vignettes[i][1] = imageAutotile.getSubimage(Main.TAILLE_D_UN_CARREAU, 0, Main.TAILLE_D_UN_CARREAU, Main.TAILLE_D_UN_CARREAU);
						} catch (IOException ioe) {
							LOG.error("Impossible d'ouvrir l'image du tileset "+fichierTileset.getName(), ioe);
						}
					}
					final JSONObject jsonCousin = new JSONObject();
					final JSONArray cousinages = new JSONArray();
					for (int i = 0; i < nombreDAutotiles; i++) {
						JSONArray cousinage = new JSONArray();
						cousinage.put(i-8);
						for (int j = 0; j < nombreDAutotiles; j++) {
							if (i != j && (Graphismes.memeImage(vignettes[i][0], vignettes[j][1]) || Graphismes.memeImage(vignettes[i][1], vignettes[j][0]))) {
								cousinage.put(j-8);
							}
						}
						cousinages.put(cousinage);
					}
					jsonCousin.put("cousins", cousinages);
					// �crire nouveau JSON
					PrintWriter out = new PrintWriter(".\\ressources\\Data\\Tilesets\\Cousins\\"+fichierTileset.getName());
					out.println(jsonCousin.toString());
					out.close();
				} catch (Exception e) {
					LOG.error("Impossible d'ouvrir le tileset "+fichierTileset.getName(), e);
				}
			}
		}
	}

	private static void reecrireLesTouchesDuClavier() {
		// TODO Auto-generated method stub
	}

	private static void egaliserLesMusiques() {
		//TODO calculer le volume moyen de chaque musique
		//TODO d�finir 1.0 comme volume par d�faut pour la plus basse
		//TODO d�duire les autres volumes par d�faut par produit en croix
		//TODO recenser les occurences de chaque musique dans le jeu avec leur volume assign�
		//TODO pour le plus grand volumed'usage, utiliser le volume par d�faut
		//TODO d�duire les remplacements des autres volumes d'usage par produit en croix 
	}
	
	private static ArrayList<String> listerLesConditionsScript() {
		ArrayList<String> scripts = new ArrayList<>();
		for (int numeroMap = 1; numeroMap <=572; numeroMap++) {
			try {
				final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numeroMap);
				final JSONArray jsonEvents = jsonMap.getJSONArray("events");
				for (Object oEvent : jsonEvents) {
					try {
						JSONObject jsonEvent = (JSONObject) oEvent;
						final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
						for (Object oPage : jsonPages) {
							try {
								JSONObject jsonPage = (JSONObject) oPage;
								final JSONArray jsonCommandes = jsonPage.getJSONArray("commandes");
								for (Object oCommande : jsonCommandes) {
									try {
										JSONObject jsonCommande = (JSONObject) oCommande;
										final Commande commande = Commande.recupererUneCommande(jsonCommande);
										if (commande instanceof ConditionScript || commande instanceof AppelerUnScript) {
											scripts.add(commande.toString());
										}
									} catch (Exception e) {
										//LOG.error("Commande irr�cup�rable : "+oCommande);
									}
								}
							} catch (Exception e) {
								//e.printStackTrace();
							}
						}
					} catch (Exception e) {
						//LOG.error("Pas de pages : "+oEvent);
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
		}
		return scripts;
	}
}
