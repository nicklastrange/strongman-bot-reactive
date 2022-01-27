package com.nicklastrange.strongmanbot.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
@Table("servers")
public class Server {
    @Id
    private Long id;
    @Column("server_id")
    private Long serverId;
    @Column("server_name")
    private String serverName;
    @Column("server_prefix")
    private String serverPrefix;
}
