package jeu;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.ChargerPartie;
import conditions.Condition;
import jeu.Quete.AvancementQuete;
import main.Commande;
import main.Fenetre;
import map.Map;
import map.Picture;
import map.meteo.Meteo;
import menu.Listable;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;

/**
 * Une Partie est l'ensemble des informations li�es � l'avanc�e du joueur dans le jeu.
 */
public final class Partie implements Listable {
	private static final Logger LOG = LogManager.getLogger(Partie.class);
	private static final int NOMBRE_D_INTERRUPTEURS = 100;
	private static final int NOMBRE_DE_VARIABLES = 100;
	
	public int id;
	public int numeroMap;
	public Map map;
	/** coordonn�e x (en pixels) */
	public int xHeros;
	/** coordonn�e y (en pixels) */
	public int yHeros;
	public int directionHeros;
	public int vie;
	public int vieMax;
	public int argent;
	
	public final boolean[] interrupteurs = new boolean[NOMBRE_D_INTERRUPTEURS];
	public final ArrayList<String> interrupteursLocaux = new ArrayList<String>();
	public final int[] variables = new int[NOMBRE_DE_VARIABLES];
	
	/** combien poss�de-t-on d'Objet num�ro i ? */
	public int[] objetsPossedes;
	/** la Qu�te num�ro i a-t-elle �t� faite ? */
	public AvancementQuete[] avancementDesQuetes;
	/** poss�de-t-on l'Arme num�ro i ? */
	public boolean[] armesPossedees;
	public int nombreDArmesPossedees;
	/** poss�de-t-on le gadget num�ro i ? */
	public boolean[] gadgetsPossedes;
	public int nombreDeGadgetsPossedes;
	
	public int idArmeEquipee;
	public int idGadgetEquipe;
	
	/** effet m�t�orologique en cours */
	public Meteo meteo = null;
	/** Images � afficher par dessus l'�cran */
	public HashMap<Integer, Picture> images = new HashMap<Integer, Picture>();
	
	/** Mot de passe � saisir lettre par lettre via un Menu */
	public final int tailleMaximaleDuMot = 10;
	public String mot = "";
	
	/**
	 * Constructeur d'une nouvelle Partie vierge
	 * @throws FileNotFoundException le JSON de param�trage d'une nouvelle Partie n'a pas �t� trouv�
	 */
	private Partie() throws FileNotFoundException {
		final JSONObject jsonNouvellePartie = InterpreteurDeJson.ouvrirJsonNouvellePartie();
		// Position du H�ros
		this.numeroMap = jsonNouvellePartie.getInt("numeroMap");
		this.xHeros = jsonNouvellePartie.getInt("xHeros")*Fenetre.TAILLE_D_UN_CARREAU;
		this.yHeros = jsonNouvellePartie.getInt("yHeros")*Fenetre.TAILLE_D_UN_CARREAU;
		this.directionHeros = jsonNouvellePartie.getInt("directionHeros");
		// Vie
		this.vie = jsonNouvellePartie.getInt("vie");
		this.vieMax = jsonNouvellePartie.getInt("vieMax");
		// Qu�tes
		this.avancementDesQuetes = new AvancementQuete[ Quete.chargerLesQuetesDuJeu() ];
		for (int i = 0; i<avancementDesQuetes.length; i++) {
			avancementDesQuetes[i] = AvancementQuete.INCONNUE;
		}
		// Objets
		this.objetsPossedes = new int[ Objet.chargerLesObjetsDuJeu() ];
		// Armes
		this.armesPossedees = new boolean[ Arme.chargerLesArmesDuJeu() ];
		this.nombreDArmesPossedees = 0;
		this.idArmeEquipee = -1;
		// Gadgets
		this.gadgetsPossedes = new boolean[ Gadget.chargerLesGadgetsDuJeu() ];
		this.nombreDeGadgetsPossedes = 0;
		this.idGadgetEquipe = -1;

		LOG.info("Partie charg�e.");
	}
	
	/**
	 * Constructeur explicite
	 * @param id du fichier de sauvegarde
	 * @param numeroMap num�ro de la Map o� se trouve le H�ros en reprenant la Partie
	 * @param xHeros coordonn�e x du H�ros (en pixels) en reprenant la Partie
	 * @param yHeros coordonn�e y du H�ros (en pixels) en reprenant la Partie
	 * @param directionHeros direction dans laquelle se trouve le Heros en reprenant la Partie
	 * @param vie niveau d'�nergie vitale du H�ros en reprenant la Partie
	 * @param vieMax niveau maximal possible d'�nergie vitale du H�ros en reprenant la Partie
	 * @param argent poss�d�
	 * ----------------------------------------------------------------------------------------
	 * @param objetsPossedes combien poss�de-t-on d'Objet num�ro i ?
	 * @param avancementDesQuetes la Qu�te num�ro i a-t-elle �t� faite ?
	 * @param armesPossedees poss�de-t-on l'Arme num�ro i ?
	 * @param gadgetsPossedes poss�de-t-on le Gadget num�ro i ?
	 * @param interrupteurs �tat des interrupteurs du jeu
	 * @param variables �tat des variables locaux du jeu
	 * @param interrupteursLocaux �tat des interrupteurs locaux du jeu
	 * ---------------------------------------------------------------------------------------- 
	 * @param idArmeEquipee identifiant de l'Arme actuelle �quip�e
	 * @param idGadgetEquipe identifiant du Gadget actuel �quip�
	 * @throws FileNotFoundException le JSON de param�trage d'une nouvelle Partie n'a pas �t� trouv�
	 */
	public Partie(final int id, final int numeroMap, final int xHeros, final int yHeros, final int directionHeros, final int vie, 
			final int vieMax, final int argent, final int idArmeEquipee, final int idGadgetEquipe, final JSONArray objetsPossedes, 
			final JSONArray avancementDesQuetes, final JSONArray armesPossedees, final JSONArray gadgetsPossedes, 
			final JSONArray interrupteurs, final JSONArray variables, final JSONArray interrupteursLocaux) 
	throws FileNotFoundException {
		this();
		this.id = id;
		this.numeroMap = numeroMap;
		this.xHeros = xHeros; //TODO faire �a pour tous les Events de la Map
		this.yHeros = yHeros; //TODO faire �a pour tous les Events de la Map
		this.directionHeros = directionHeros; //TODO faire �a pour tous les Events de la Map
		this.vie = vie; //TODO faire �a pour tous les Events de la Map
		this.vieMax = vieMax; //TODO faire �a pour tous les Events de la Map
		this.argent = argent;
		
		//objets
		final int[] tableauObjetsPossedes = new int[Objet.objetsDuJeu.length];
		for (Object o : objetsPossedes) {
			final JSONObject objetPossede = (JSONObject) o;
			tableauObjetsPossedes[objetPossede.getInt("numero")] = objetPossede.getInt("quantite");
		}
		this.objetsPossedes = tableauObjetsPossedes;
		
		//qu�tes
		final AvancementQuete[] tableauAvancementDesQuetes = new AvancementQuete[Quete.quetesDuJeu.length];
		for (Object o : avancementDesQuetes) {
			final JSONObject avancementQuete = (JSONObject) o;
			tableauAvancementDesQuetes[avancementQuete.getInt("numero")] = AvancementQuete.getEtat(avancementQuete.getString("avancement"));
		}
		this.avancementDesQuetes = tableauAvancementDesQuetes;
		
		//armes
		final boolean[] tableauArmesPossedees = new boolean[Arme.chargerLesArmesDuJeu()];
		int nombreDArmesPossedees = 0;
		for (Object o : armesPossedees) {
			final int armePossedee = (Integer) o;
			tableauArmesPossedees[armePossedee] = true;
			nombreDArmesPossedees++;
		}
		this.armesPossedees = tableauArmesPossedees; 
		this.nombreDArmesPossedees = nombreDArmesPossedees;
		
		//gadgets
		final boolean[] tableauDesGadgetsPossedes = new boolean[Gadget.chargerLesGadgetsDuJeu()];
		int nombreDeGadgetsPossedes = 0;
		for (Object o : gadgetsPossedes) {
			final int gadgetPossede = (Integer) o;
			tableauDesGadgetsPossedes[gadgetPossede] = true;
			nombreDeGadgetsPossedes++;
		}
		this.gadgetsPossedes = tableauDesGadgetsPossedes;
		this.nombreDeGadgetsPossedes = nombreDeGadgetsPossedes;
		
		//interrupteurs
		for (Object o : interrupteurs) {
			final int interrupteurActif = (Integer) o;
			this.interrupteurs[interrupteurActif] = true;
		}
		//variables
		for (Object o : variables) {
			final JSONObject variable = (JSONObject) o;
			this.variables[variable.getInt("numero")] = variable.getInt("valeur");
		}
		//interrupteurs locaux
		for (Object o : interrupteursLocaux) {
			final String code = (String) o;
			this.interrupteursLocaux.add(code);
		}
				
		this.idArmeEquipee = idArmeEquipee;
		this.idGadgetEquipe = idGadgetEquipe;
	}
	
	/**
	 * G�n�re une nouvelle Partie vierge.
	 * @return une nouvelle partie
	 * @throws FileNotFoundException le JSON de param�trage d'une nouvelle Partie n'a pas �t� trouv�
	 */
	public static Partie creerNouvellePartie() throws FileNotFoundException {
		return new Partie();
	}
	
	/**
	 * Connaitre le Gadget actuellement �quip�
	 * @return Gadget �quip�
	 */
	public Gadget getGadgetEquipe() {
		return Gadget.getGadget(this.idGadgetEquipe);
	}
	
	/**
	 * Equiper un Gadget au Heros
	 * @param idGadget identifiant du Gadget � �quiper
	 */
	public void equiperGadget(final int idGadget) {
		if (this.gadgetsPossedes[idGadget]) {
			this.idGadgetEquipe = idGadget;
		}
	}
	
	/**
	 * Connaitre l'Arme actuellement �quip�e
	 * @return Arme �quip�e
	 */
	public Arme getArmeEquipee() {
		return Arme.getArme(this.idArmeEquipee);
	}
	
	/**
	 * Equiper une Arme au Heros
	 * @param idArme identifiant de l'Arme � �quiper
	 */
	public void equiperArme(final int idArme) {
		if (this.armesPossedees[idArme]) {
			this.idArmeEquipee = idArme;
		}
	}
	
	/**
	 * Equiper l'Arme suivante dans la liste des Armes poss�d�es par le H�ros
	 */
	public void equiperArmeSuivante() {
		//pas d'Armes poss�d�es
		if (nombreDArmesPossedees<=0) {
			return;
		}
		//si pas d'Arme �quip�e, on �quipe la derni�re poss�d�e
		if (idArmeEquipee<0) {
			idArmeEquipee += nombreDArmesPossedees-1;
			return;
		}
		//on �quipe l'Arme suivante
		this.idArmeEquipee = Maths.modulo(this.idArmeEquipee + 1, nombreDArmesPossedees);
		//affichage console
		if (this.getArmeEquipee()!=null) {
			LOG.info("arme suivante : "+this.getArmeEquipee().nom);
		}
	}
	
	/**
	 * Equiper l'Arme pr�c�dente dans la liste des Armes poss�d�es par le H�ros
	 */
	public void equiperArmePrecedente() {		
		//pas d'Armes poss�d�es
		if (nombreDArmesPossedees<=0) {
			return;
		}
		//si pas d'Arme �quip�e, on �quipe la derni�re poss�d�e
		if (idArmeEquipee<0) {
			idArmeEquipee += nombreDArmesPossedees-1;
			return;
		}
		//on �quipe l'Arme pr�c�dente
		this.idArmeEquipee = Maths.modulo(this.idArmeEquipee - 1, nombreDArmesPossedees);
		//affichage console
		LOG.info("arme pr�c�dente : "+ this.getArmeEquipee().nom);
	}

	@Override
	public java.util.Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		// TODO Auto-generated method stub
		// ce sera des parties bidons, creuses (ou alors avec quelques donn�es seulement), qui ne contiennent que le num�ro de sauvegarde
		return null;
	}

	@Override
	public BufferedImage construireImagePourListe(final ArrayList<String> information) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Condition> getConditions() {
		return null;
	}

	@Override
	public ArrayList<Commande> getComportementSelection() {
		return null;
	}

	@Override
	public ArrayList<Commande> getComportementConfirmation() {
		final ArrayList<Commande> comportementConfirmation = new ArrayList<Commande>();
		comportementConfirmation.add(new ChargerPartie(this.id));
		return comportementConfirmation;
	}
	
}
