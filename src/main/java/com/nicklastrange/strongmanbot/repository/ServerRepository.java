package com.nicklastrange.strongmanbot.repository;

import com.nicklastrange.strongmanbot.model.Server;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ServerRepository extends ReactiveCrudRepository<Server, Long> {

    @Query("SELECT * FROM server WHERE server_id = :serverId")
    Mono<Server> findServerByServerId(Long serverId);
}
