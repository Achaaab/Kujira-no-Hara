package main;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Une Commande modifie l'�tat du jeu.
 * Elle peut �tre lanc�e par une Page d'Event, ou par un El�ment de Menu.
 */
public abstract class Commande {
	//cl� de cryptage
	private static final Logger LOG = LogManager.getLogger(Commande.class);
	private static final String CLE_CRYPTAGE_SAUVEGARDE = "t0p_k3k";
	private static final int NOMBRE_OCTETS_HASH = 16;
	
	/** [CommandeEvent] Eventuelle Page d'Event qui a appel� cette Commande */
	public PageEvent page;
	/** [CommandeMenu] Element de Menu qui a appel� cette Commande de Menu */
	public ElementDeMenu element;
	
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut �tre inchang� (attendre n frames...) ;
	 * le curseur peut �tre incr�ment� (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit execut�e
	 * @param commandes liste des Commandes de la Page de comportement en train d'�tre lue
	 * @return nouvelle position du curseur apr�s l'execution totale ou partielle de la Commande
	 */
	public abstract int executer(int curseurActuel, ArrayList<Commande> commandes);
	
	/**
	 * Construire la cl� de cryptage.
	 * @return cl� de cryptage
	 */
	protected final SecretKeySpec construireCleDeCryptage() {
		try {
			// Hashage de la cl�
			byte[] cle = CLE_CRYPTAGE_SAUVEGARDE.getBytes("UTF-8");
			final MessageDigest sha = MessageDigest.getInstance("SHA-1");
			cle = sha.digest(cle);
			
			cle = Arrays.copyOf(cle, NOMBRE_OCTETS_HASH); //seulement les 128 premiers bits
			return new SecretKeySpec(cle, "AES");
			
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e);
		}
		return null;
	}
}
