package com.example.modulith.adoptions;


import org.springframework.modulith.events.Externalized;

@Externalized("messageChannelName")
public record DogAdoptedEvent(int dogId) {
}
