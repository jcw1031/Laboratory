package com.woopaca.laboratory.playground.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
class BlockchainTest {

    private final List<Block> blockchain = new ArrayList<>();
    private final int prefix = 4;
    private final String prefixString = new String(new char[prefix]).replace('\0', '0');

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 3; i++) {
            Block block = Block.builder()
                    .data(String.valueOf(i))
                    .previousHash(i == 0 ? "0" : blockchain.get(i - 1).getHash())
                    .timestamp(System.currentTimeMillis())
                    .build();
            block.mineBlock(prefix);
            blockchain.add(block);
        }
    }

    @Test
    void givenBlockchain_whenNewBlockAdded_thenSuccess() {
        Block newBlock = Block.builder()
                .data("New Block")
                .previousHash(blockchain.getLast().getHash())
                .timestamp(System.currentTimeMillis())
                .build();
        newBlock.mineBlock(prefix);

        Assertions.assertThat(newBlock.getHash().substring(0, prefix)).isEqualTo(prefixString);
        blockchain.add(newBlock);

        log.info("blockchain: {}", blockchain);
    }

    @Test
    public void givenBlockchain_whenValidated_thenSuccess() {
        boolean allMatch = IntStream.range(0, blockchain.size()).allMatch(i -> {
            String previousHash = i == 0 ? "0" : blockchain.get(i - 1).getHash();
            Block block = blockchain.get(i);

            return block.getHash().equals(block.calculateHash())
                   && previousHash.equals(block.getPreviousHash())
                   && block.getHash().substring(0, prefix).equals(prefixString);
        });

        Assertions.assertThat(allMatch).isTrue();
    }
}