package com.mobile2app.eventtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Manages the SQLCipher database passphrase.
 * A random passphrase is generated on first use and stored encrypted
 * in SharedPreferences using a key held in the Android Keystore.
 */
public class DatabaseKeyManager {

    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String KEY_ALIAS = "event_tracker_db_key";
    private static final String PREFS_NAME = "db_encryption_prefs";
    private static final String PREF_ENCRYPTED_PASSPHRASE = "encrypted_passphrase";
    private static final String PREF_IV = "passphrase_iv";
    private static final int GCM_TAG_LENGTH = 128;

    private final Context context;

    public DatabaseKeyManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Returns the passphrase for opening the database.
     * Generates a new random passphrase on first call and stores it encrypted.
     */
    public byte[] getOrCreatePassphrase() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String encryptedPassphrase = prefs.getString(PREF_ENCRYPTED_PASSPHRASE, null);
        String ivString = prefs.getString(PREF_IV, null);

        if (encryptedPassphrase == null || ivString == null) {
            // First run: generate and store a new passphrase
            byte[] newPassphrase = generateRandomPassphrase();
            storePassphrase(newPassphrase, prefs);
            return newPassphrase;
        }

        // Subsequent runs: decrypt and return the stored passphrase
        byte[] iv = Base64.decode(ivString, Base64.DEFAULT);
        byte[] encrypted = Base64.decode(encryptedPassphrase, Base64.DEFAULT);
        return decryptPassphrase(encrypted, iv);
    }

    private byte[] generateRandomPassphrase() {
        byte[] passphrase = new byte[32]; // 256-bit key
        new java.security.SecureRandom().nextBytes(passphrase);
        return passphrase;
    }

    private void storePassphrase(byte[] passphrase, SharedPreferences prefs) {
        try {
            SecretKey secretKey = getOrCreateKeystoreKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encrypted = cipher.doFinal(passphrase);

            prefs.edit()
                    .putString(PREF_ENCRYPTED_PASSPHRASE, Base64.encodeToString(encrypted, Base64.DEFAULT))
                    .putString(PREF_IV, Base64.encodeToString(iv, Base64.DEFAULT))
                    .apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store database passphrase", e);
        }
    }

    private byte[] decryptPassphrase(byte[] encrypted, byte[] iv) {
        try {
            SecretKey secretKey = getOrCreateKeystoreKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt database passphrase", e);
        }
    }

    private SecretKey getOrCreateKeystoreKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);

        if (keyStore.containsAlias(KEY_ALIAS)) {
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER);
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build();
        keyGenerator.init(spec);
        return keyGenerator.generateKey();
    }
}