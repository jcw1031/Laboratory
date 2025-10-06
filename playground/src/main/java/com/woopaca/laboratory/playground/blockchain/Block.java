package com.woopaca.laboratory.playground.blockchain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@ToString
@Getter
public class Block {

    private String hash;
    private String previousHash;
    private String data;
    private long timestamp;
    private int nonce;

    @Builder
    public Block(String previousHash, String data, long timestamp) {
        this.previousHash = previousHash;
        this.data = data;
        this.timestamp = timestamp;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String dataToHash = previousHash + timestamp + nonce + data;
        MessageDigest messageDigest;
        byte[] bytes;

        try {
            messageDigest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
            bytes = messageDigest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!hash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            hash = calculateHash();
        }
        return hash;
    }
}
