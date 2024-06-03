package com.woopaca.laboratory.stomp.chat;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChatMessage(String content, String sender, @JsonProperty("type") @JsonAlias("type") MessageType messageType) {
}
