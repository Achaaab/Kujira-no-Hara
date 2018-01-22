package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

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
		
		// Autotiles cousins
		calculerLesAutotilesCousins();
		
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
							vignettes[i][0] = imageAutotile.getSubimage(0, 0, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
							vignettes[i][1] = imageAutotile.getSubimage(Fenetre.TAILLE_D_UN_CARREAU, 0, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
						} catch (IOException ioe) {
							LOG.error("Impossible d'ouvrir l'image du tileset "+fichierTileset.getName(), ioe);
						}
					}
					for (int i = 0; i < nombreDAutotiles; i++) {
						final JSONObject jsonAutotile = (JSONObject) jsonAutotiles.get(i);
						JSONArray cousins = new JSONArray();
						for (int j = 0; j < nombreDAutotiles; j++) {
							if (i != j && (Graphismes.memeImage(vignettes[i][0], vignettes[j][1]) || Graphismes.memeImage(vignettes[i][1], vignettes[j][0]))) {
								cousins.put(j);
							}
						}
						jsonAutotile.put("cousins", cousins);
					}
					// �crire nouveau JSON
					PrintWriter out = new PrintWriter(".\\ressources\\Data\\Tilesets\\"+fichierTileset.getName()+".new");
					out.println(jsonTileset.toString()
							.replaceAll("\\],", "\\\n],\n")
							.replaceAll("\\},", "\\\n},\n")
							.replaceAll("\\{", "\\{\n")
							.replaceAll("\\[", "\\[\n")
							.replaceAll("\\]\\}", "\\]\n\\}")
							.replaceAll("\\}\\]", "\\}\n\\]")
							.replaceAll("\",", "\",\n")
					);
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
}
