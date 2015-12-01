package org.saucistophe.lpc.beans;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.saucistophe.lpc.utils.Constants;

/**
 * A bean to convey authentification data from client to server.
 */
public class AuthentificationBean
{
	private String login = null;
	private String encryptedPassword = null;
	private Date date = null;

	public AuthentificationBean()
	{
		date = new Date();
	}

	public AuthentificationBean(String login, String password)
	{
		this.login = login;
		this.encryptedPassword = encrypt(password);
		date = new Date();
	}

	@Override
	public boolean equals(Object that)
	{
		if (that != null && that instanceof AuthentificationBean)
		{
			AuthentificationBean thatBean = (AuthentificationBean) that;
			return thatBean.login != null
					&& thatBean.encryptedPassword != null
					&& thatBean.login.equals(this.login)
					&& thatBean.encryptedPassword.equals(this.encryptedPassword);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.login);
		hash = 13 * hash + Objects.hashCode(this.encryptedPassword);
		return hash;
	}

	@Override
	public String toString()
	{
		return login + Constants.Auth.SEPARATOR + encryptedPassword + Constants.Auth.SEPARATOR + Long.toHexString(date.getTime());
	}

	public static AuthentificationBean fromString(String string)
	{
		AuthentificationBean bean = new AuthentificationBean();

		String[] strings = string.split(Constants.Auth.SEPARATOR);
		// Assert there are the right amount of separators.
		if (strings.length != 3)
		{
			return null;
		}
		bean.login = strings[0];
		bean.encryptedPassword = strings[1];
		bean.date.setTime(Long.parseLong(strings[2], 16));

		return bean;
	}

	public static String encrypt(String data)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			md.update(data.getBytes(Constants.ENCODING));
			byte[] digest = md.digest();

			String base64Output = DatatypeConverter.printBase64Binary(digest);

			return base64Output;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException ex)
		{
			Logger.getLogger(AuthentificationBean.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 * @return the login
	 */
	public String getLogin()
	{
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login)
	{
		this.login = login;
	}

	/**
	 * @return the date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date)
	{
		this.date = date;
	}

	/**
	 * @return the encryptedPassword
	 */
	public String getEncryptedPassword()
	{
		return encryptedPassword;
	}

	/**
	 * @param encryptedPassword the encryptedPassword to set
	 */
	public void setEncryptedPassword(String encryptedPassword)
	{
		this.encryptedPassword = encryptedPassword;
	}
}
