/*
 * Copyright (c) 2005, 2023, Oracle and/or its affiliates. All rights reserved.
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

package sun.security.mscapi;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;

/**
 * The handle for a private key using the Microsoft Crypto API.
 *
 * @author Stanley Man-Kit Ho
 * @since 1.6
 */
class CPrivateKey extends CKey implements PrivateKey {

    @java.io.Serial
    private static final long serialVersionUID = 8113152807912338063L;

    private CPrivateKey(String alg, NativeHandles handles, int keyLength) {
        super(alg, handles, keyLength, false);
    }

    // Called by native code inside security.cpp
    static CPrivateKey of(
            String alg, long hCryptProv, long hCryptKey, int keyLength) {
        return of(alg, new NativeHandles(hCryptProv, hCryptKey), keyLength);
    }

    public static CPrivateKey of(String alg, NativeHandles handles, int keyLength) {
        return new CPrivateKey(alg, handles, keyLength);
    }

    // this key does not support encoding
    public String getFormat()     {
        return null;
    }

    // this key does not support encoding
    public byte[] getEncoded() {
        return null;
    }

    // This class is not serializable
    @java.io.Serial
    private void writeObject(java.io.ObjectOutputStream out)
            throws java.io.IOException {
        throw new java.io.InvalidObjectException(
                "CPrivateKeys are not serializable");
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
                "CPrivateKeys are not deserializable");
    }
}
