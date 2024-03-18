package de.cidaas.quarkus.extension;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMTPMXLookup {

	private static final Logger LOG = LoggerFactory.getLogger(SMTPMXLookup.class);

	private static List<String> getMX(String hostName) throws NamingException {
		// Perform a DNS lookup for MX records in the domain
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		Attribute attr = attrs.get("MX");

		// if we don't have an MX record, try the machine itself
		if ((attr == null) || (attr.size() == 0)) {
			attrs = ictx.getAttributes(hostName, new String[] { "A" });
			attr = attrs.get("A");
			if (attr == null) {
				LOG.error("No match for the name {} ", hostName);
				throw new NamingException("No match for the name '" + hostName + "'");
			}
		}

		List<String> res = new ArrayList<String>();
		NamingEnumeration<?> en = attr.getAll();

		while (en.hasMore()) {
			String mailhost;
			String x = (String) en.next();
			String f[] = x.split(" ");
			// THE fix *************
			if (f.length == 1)
				mailhost = f[0];
			else if (f[1].endsWith("."))
				mailhost = f[1].substring(0, (f[1].length() - 1));
			else
				mailhost = f[1];
			// THE fix *************
			res.add(mailhost);
		}
		return res;
	}

	/**
	 * Checks if is address valid.
	 *
	 * @param address the address
	 * @return true, if is address valid
	 * @throws AddressValidationException the address validation exception
	 */
	public static boolean isAddressValid(String address) throws AddressValidationException {
		long validationStart = System.currentTimeMillis();
		// Find the separator for the domain name
		int pos = address.indexOf('@');

		// If the address does not contain an '@', it's not valid
		if (pos == -1) {
			return false;
		}

		// Isolate the domain/machine name and get a list of mail exchangers
		String domain = address.substring(++pos);
		try {
			List<String> mxList = getMX(domain);
			if (mxList.size() > 0) {
				LOG.info("Request timing: {}ms", System.currentTimeMillis() - validationStart);
				return true;
			}
		} catch (NamingException ne) {
			LOG.info("Error during DNS lookup.", ne);
			throw new AddressValidationException(
					String.format("Exception while validating the Address {} domain {} Exception is {}", address,
							domain, ne.getMessage()));
		}

		LOG.info("Email Validation Request timing: {}ms", System.currentTimeMillis() - validationStart);
		return false;
	}

}
