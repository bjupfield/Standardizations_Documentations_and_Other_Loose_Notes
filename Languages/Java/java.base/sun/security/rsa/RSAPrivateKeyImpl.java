/*
 * Copyright (c) 2003, 2023, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package sun.security.rsa;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigInteger;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.interfaces.*;
import java.util.Arrays;

import sun.security.util.*;
import sun.security.pkcs.PKCS8Key;

import sun.security.rsa.RSAUtil.KeyType;

/**
 * RSA private key implementation for "RSA", "RSASSA-PSS" algorithms in non-CRT
 * form (modulus, private exponent only).
 * <p>
 * For CRT private keys, see RSAPrivateCrtKeyImpl. We need separate classes
 * to ensure correct behavior in instanceof checks, etc.
 * <p>
 * Note: RSA keys must be at least 512 bits long
 *
 * @see RSAPrivateCrtKeyImpl
 * @see RSAKeyFactory
 *
 * @since   1.5
 * @author  Andreas Sterbenz
 */
public final class RSAPrivateKeyImpl extends PKCS8Key implements RSAPrivateKey {

    @java.io.Serial
    private static final long serialVersionUID = -33106691987952810L;

    private final BigInteger n;         // modulus
    private final BigInteger d;         // private exponent

    private final transient KeyType type;

    // optional parameters associated with this RSA key
    // specified in the encoding of its AlgorithmId.
    // must be null for "RSA" keys.
    private final transient AlgorithmParameterSpec keyParams;

    /**
     * Construct a key from its components. Used by the
     * RSAKeyFactory and the RSAKeyPairGenerator.
     */
    RSAPrivateKeyImpl(KeyType type, AlgorithmParameterSpec keyParams,
            BigInteger n, BigInteger d) throws InvalidKeyException {

        RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), null);

        this.n = n;
        this.d = d;

        try {
            // validate and generate the algid encoding
            algid = RSAUtil.createAlgorithmId(type, keyParams);
        } catch (ProviderException pe) {
            throw new InvalidKeyException(pe);
        }

        this.type = type;
        this.keyParams = keyParams;

        // generate the key encoding
        byte[] nbytes = n.toByteArray();
        byte[] dbytes = d.toByteArray();
        DerOutputStream out = new DerOutputStream(
                nbytes.length + dbytes.length + 50);
        // Enough for 7 zeroes (21) and 2 tag+length(4)
        out.putInteger(0); // version must be 0
        out.putInteger(nbytes);
        Arrays.fill(nbytes, (byte) 0);
        out.putInteger(0);
        out.putInteger(dbytes);
        Arrays.fill(dbytes, (byte) 0);
        out.putInteger(0);
        out.putInteger(0);
        out.putInteger(0);
        out.putInteger(0);
        out.putInteger(0);
        DerValue val = DerValue.wrap(DerValue.tag_Sequence, out);
        key = val.toByteArray();
        val.clear();
    }

    // see JCA doc
    @Override
    public String getAlgorithm() {
        return type.keyAlgo;
    }

    // see JCA doc
    @Override
    public BigInteger getModulus() {
        return n;
    }

    // see JCA doc
    @Override
    public BigInteger getPrivateExponent() {
        return d;
    }

    // see JCA doc
    @Override
    public AlgorithmParameterSpec getParams() {
        return keyParams;
    }

    /**
     * Restores the state of this object from the stream.
     * <p>
     * Deserialization of this object is not supported.
     *
     * @param  stream the {@code ObjectInputStream} from which data is read
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a serialized class cannot be loaded
     */
    @java.io.Serial
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        throw new InvalidObjectException(
                "RSAPrivateKeyImpl keys are not directly deserializable");
    }
}
