package jeu;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONObject;

import jeu.Quete.AvancementQuete;
import map.Map;
import map.meteo.Meteo;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;

/**
 * Une Partie est l'ensemble des informations li�es � l'avanc�e du joueur dans le jeu.
 */
public final class Partie {
	public int numeroMap;
	public Map map;
	public int xHeros;
	public int yHeros;
	public int directionHeros;
	public int vie;
	public int vieMax;
	public int argent;
	
	public final boolean[] interrupteurs;
	public final ArrayList<String> interrupteursLocaux;
	public final int[] variables;
	
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
	
	private int idArmeEquipee;
	private int idGadgetEquipe; //TODO faire en sorte qu'il soit plus soulign� en jaune
	
	/** effet m�t�orologique en cours */
	public Meteo meteo = null;
	
	
	/**
	 * Constructeur d'une nouvelle Partie vierge
	 * @throws FileNotFoundException le JSON de param�trage d'une nouvelle Partie n'a pas �t� trouv�
	 */
	private Partie() throws FileNotFoundException {
		final JSONObject jsonNouvellePartie = InterpreteurDeJson.ouvrirJsonNouvellePartie();
		// Position du H�ros
		this.numeroMap = jsonNouvellePartie.getInt("numeroMap");
		this.xHeros = jsonNouvellePartie.getInt("xHeros");
		this.yHeros = jsonNouvellePartie.getInt("yHeros");
		this.directionHeros = jsonNouvellePartie.getInt("directionHeros");
		// Vie
		this.vie = jsonNouvellePartie.getInt("vie");
		this.vieMax = jsonNouvellePartie.getInt("vieMax");
		// Interrupteurs et Variables
		this.interrupteurs = new boolean[ jsonNouvellePartie.getInt("nombreDInterrupteurs") ];
		this.interrupteursLocaux = new ArrayList<String>();
		this.variables = new int[ jsonNouvellePartie.getInt("nombreDeVariables") ];
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
		
		System.out.println("Partie charg�e.");
	}
	
	/**
	 * Constructeur explicite
	 * @param numeroMap num�ro de la Map o� se trouve le H�ros en reprenant la Partie
	 * @param xHeros coordonn�e x du H�ros en reprenant la Partie
	 * @param yHeros coordonn�e y du H�ros en reprenant la Partie
	 * @param directionHeros direction dans laquelle se trouve le Heros en reprenant la Partie
	 * @param vie niveau d'�nergie vitale du H�ros en reprenant la Partie
	 * @param vieMax niveau maximal possible d'�nergie vitale du H�ros en reprenant la Partie
	 * ----------------------------------------------------------------------------------------
	 * @param objetsPossedes combien poss�de-t-on d'Objet num�ro i ?
	 * @param avancementDesQuetes la Qu�te num�ro i a-t-elle �t� faite ?
	 * @param armesPossedees poss�de-t-on l'Arme num�ro i ?
	 * @param nombreDArmesPossedees combien a-t-on d'Armes ?
	 * ---------------------------------------------------------------------------------------- 
	 * @param idArmeEquipee identifiant de l'Arme actuelle �quip�e
	 * @throws FileNotFoundException le JSON de param�trage d'une nouvelle Partie n'a pas �t� trouv�
	 */
	private Partie(final int numeroMap, final int xHeros, final int yHeros, final int directionHeros, final int vie, final int vieMax, final int idArmeEquipee, final int[] objetsPossedes, final AvancementQuete[] avancementDesQuetes, final boolean[] armesPossedees, final int nombreDArmesPossedees) throws FileNotFoundException {
		this();
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.directionHeros = directionHeros;
		this.vie = vie;
		this.vieMax = vieMax;
		
		this.objetsPossedes = objetsPossedes;
		this.avancementDesQuetes = avancementDesQuetes;
		this.armesPossedees = armesPossedees;
		this.nombreDArmesPossedees = nombreDArmesPossedees;
		
		this.idArmeEquipee = idArmeEquipee;
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
	 * Charger une partie � l'aide d'un fichier de sauvegarde.
	 * @param numeroSauvegarde num�ro de la partie sauvegard�e
	 * @return une partie sauvegard�e
	 */
	public static Partie chargerPartie(final int numeroSauvegarde) {
		//TODO cr�er un objet Partie � partir d'un fichier de sauvegarde
		return null;
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
			System.out.println("arme suivante : "+this.getArmeEquipee().nom);
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
		System.out.println("arme pr�c�dente : "+ this.getArmeEquipee().nom);
	}
	
}
