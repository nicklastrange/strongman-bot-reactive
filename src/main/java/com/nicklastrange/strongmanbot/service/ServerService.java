package com.nicklastrange.strongmanbot.service;

import com.nicklastrange.strongmanbot.model.Server;
import com.nicklastrange.strongmanbot.repository.ServerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public Mono<Server> findServerByServerId(Long serverId) {
        return serverRepository.findServerByServerId(serverId)
                .switchIfEmpty(Mono.error(new NoSuchElementException()));
    }

    public Mono<Server> addNewServer(Server server) {
        return serverRepository.save(server);
    }

    public Mono<Server> updateServer(Server server) {
        return serverRepository.save(server);
    }
}
