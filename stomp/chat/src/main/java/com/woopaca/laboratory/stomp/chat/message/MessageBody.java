package com.woopaca.laboratory.stomp.chat.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageBody(String content, long senderId, long roomId,
                          @JsonProperty("type") @JsonAlias("type") MessageType messageType) {
}
