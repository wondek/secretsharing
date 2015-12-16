/*

Copyright 2014 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

 */

package org.mitre.secretsharing;

import java.math.BigInteger;
import java.util.Arrays;

import org.mitre.secretsharing.codec.PartFormats;

/**
 * Utility class for holding split parts of a secret
 * @author Robin Kirkman
 *
 */
public class Part {
	/**
	 * The public components of a secret part.  These are the same
	 * for all parts of a secret.
	 * @author Robin Kirkman
	 *
	 */
	public static class PublicSecretPart {
		/**
		 * The number of bytes in the secret
		 */
		private int length;
		/**
		 * The number of parts required to reconstruct the secret
		 */
		private int requiredParts = -1;
		/**
		 * The prime modulo for the secret parts
		 */
		private BigInteger modulus;
		
		/**
		 * Create a new {@link PublicSecretPart}
		 * @param length
		 * @param modulus
		 */
		private PublicSecretPart(int length, int requiredParts, BigInteger modulus) {
			if(modulus == null)
				throw new IllegalArgumentException();
			this.length = length;
			this.requiredParts = requiredParts;
			this.modulus = modulus;
		}
		
		/**
		 * The length (in bytes) of the secret
		 * @return
		 */
		public int getLength() {
			return length;
		}

		/**
		 * The number of parts required to reconstruct the secret
		 * @return
		 */
		public int getRequiredParts() {
			return requiredParts;
		}
		
		/**
		 * The prime modulus for the key parts
		 * @return
		 */
		public BigInteger getModulus() {
			return modulus;
		}
	}
	
	/**
	 * The private component of a secret part.  This is different for every secret part.
	 * @author Robin Kirkman
	 *
	 */
	public static class PrivateSecretPart {
		/**
		 * The point on the polynomial
		 */
		private BigPoint point;
		
		/**
		 * Create a new {@link PrivateSecretPart}
		 * @param point
		 */
		private PrivateSecretPart(BigPoint point) {
			if(point == null)
				throw new IllegalArgumentException();
			this.point = point;
		}

		/**
		 * Return the point on the polynomial
		 * @return
		 */
		public BigPoint getPoint() {
			return point;
		}
	}
	
	/**
	 * The format version of this {@link Part}
	 */
	private int version;
	/**
	 * The public component of the part
	 */
	private PublicSecretPart publicPart;
	/**
	 * The private component of the part
	 */
	private PrivateSecretPart privatePart;
	
	/**
	 * Create a new {@link Part}
	 * @param version
	 * @param publicPart
	 * @param privatePart
	 */
	public Part(int version, PublicSecretPart publicPart, PrivateSecretPart privatePart) {
		this.version = version;
		this.publicPart = publicPart;
		this.privatePart = privatePart;
	}
	
	/**
	 * Create a new {@link Part}
	 * @param length
	 * @param modulus
	 * @param point
	 */
	public Part(int length, int requiredParts, BigInteger modulus, BigPoint point) {
		this(PartFormats.currentStringFormat().getVersion(), new PublicSecretPart(length, requiredParts, modulus), new PrivateSecretPart(point));
	}
	
	/**
	 * Create a new {@link Part}
	 * @param length
	 * @param modulus
	 * @param point
	 */
	public Part(int version, int length, int requiredParts, BigInteger modulus, BigPoint point) {
		this(version, new PublicSecretPart(length, requiredParts, modulus), new PrivateSecretPart(point));
	}

	private Part(Part other) {
		this(other.getVersion(), other.getPublicPart(), other.getPrivatePart());
	}
	
	/**
	 * Parse a string representation of a {@link Part}
	 * @param s
	 */
	public Part(String s) {
		this(PartFormats.parse(s));
	}
	
	@Override
	public String toString() {
		return PartFormats.currentStringFormat().format(this);
	}

	/**
	 * Return the public part of this secret part
	 * @return
	 */
	public PublicSecretPart getPublicPart() {
		return publicPart;
	}

	/**
	 * Return the private part of this secret part
	 * @return
	 */
	public PrivateSecretPart getPrivatePart() {
		return privatePart;
	}
	
	/**
	 * Return the format version of this secret part
	 * @return
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Return the length in bytes of the secret
	 * @return
	 */
	public int getLength() {
		return getPublicPart().getLength();
	}
	
	public int getRequiredParts() {
		return getPublicPart().getRequiredParts();
	}
	
	/**
	 * Return the prime modulus for the secret parts
	 * @return
	 */
	public BigInteger getModulus() {
		return getPublicPart().getModulus();
	}
	
	/**
	 * Return the point on the polynomial
	 * @return
	 */
	public BigPoint getPoint() {
		return getPrivatePart().getPoint();
	}
	
	public byte[] join(Part... otherParts) {
		Part[] parts = Arrays.copyOf(otherParts, otherParts.length + 1);
		parts[parts.length - 1] = this;
		return Secrets.joinMultibyte(parts);
	}
}
